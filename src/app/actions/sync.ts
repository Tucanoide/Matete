'use server'

import prisma from '@/lib/prisma'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { clearGuestCookies, readGuestCookie } from '@/lib/identity'

const INITIAL_NEURONAS = 50

/**
 * Funde al invitado de este navegador en la cuenta de Google logueada (spec FR-018..FR-022).
 *
 * - Atómica: todo en una transacción; si algo falla no queda nada sumado a medias.
 * - Idempotente: el invitado se bloquea con FOR UPDATE y se borra al final; una segunda
 *   llamada (recarga, otra pestaña) no lo encuentra y no hace nada.
 * - Puntos y tiempo se RECALCULAN desde Progress, así que una palabra resuelta por ambos
 *   cuenta una sola vez.
 */
export async function migrateGuestProgress(): Promise<
  { success: true; migrated: boolean } | { success: false; error: string }
> {
  const session = await getServerSession(authOptions)
  if (!session?.user?.email) return { success: false, error: 'No authenticated session' }

  const guestCookie = await readGuestCookie()
  if (!guestCookie) return { success: true, migrated: false }

  const authUser = await prisma.user.findUnique({ where: { email: session.user.email } })
  if (!authUser) return { success: false, error: 'User mapping failed' }

  if (authUser.id === guestCookie.userId) {
    await clearGuestCookies()
    return { success: true, migrated: false }
  }

  const migrated = await prisma.$transaction(async (tx) => {
    // 1. Bloquear al invitado (solo usuarios sin email: nunca una cuenta registrada).
    const locked = await tx.$queryRaw<{ id: string; neuronas: number; levelId: number }[]>`
      SELECT id, neuronas, "levelId" FROM "User"
      WHERE id = ${guestCookie.userId} AND email IS NULL
      FOR UPDATE`
    if (locked.length === 0) return false
    const guest = locked[0]

    // 2. Fusionar progreso palabra por palabra.
    const [guestProgress, authProgress] = await Promise.all([
      tx.progress.findMany({ where: { userId: guest.id } }),
      tx.progress.findMany({ where: { userId: authUser.id } }),
    ])
    const authByWord = new Map(authProgress.map((p) => [p.wordId, p]))

    for (const g of guestProgress) {
      const a = authByWord.get(g.wordId)
      if (!a) {
        await tx.progress.create({
          data: {
            userId: authUser.id,
            wordId: g.wordId,
            guessed: g.guessed,
            skipped: g.skipped,
            revealedIndices: g.revealedIndices,
            startTime: g.startTime,
            endTime: g.endTime,
            timeTaken: g.timeTaken,
          },
        })
        continue
      }
      if (a.guessed) continue // la cuenta ya la tenía resuelta: prevalece la suya
      const guessed = g.guessed
      await tx.progress.update({
        where: { id: a.id },
        data: {
          guessed,
          skipped: !guessed && (a.skipped || g.skipped),
          endTime: guessed ? g.endTime : a.endTime,
          timeTaken: guessed ? g.timeTaken : a.timeTaken,
          revealedIndices: a.revealedIndices.length ? a.revealedIndices : g.revealedIndices,
        },
      })
    }

    // 3. Borrar progreso e invitado (Progress.user es RESTRICT: primero el progreso).
    await tx.progress.deleteMany({ where: { userId: guest.id } })
    await tx.user.delete({ where: { id: guest.id } })

    // 4. Recalcular totales desde Progress y sumar solo neuronas excedentes.
    const agg = await tx.progress.aggregate({
      where: { userId: authUser.id, guessed: true },
      _count: { _all: true },
      _sum: { timeTaken: true },
    })
    const current = await tx.user.findUniqueOrThrow({ where: { id: authUser.id } })
    await tx.user.update({
      where: { id: authUser.id },
      data: {
        points: agg._count._all,
        totalTime: agg._sum.timeTaken ?? 0,
        neuronas: { increment: Math.max(0, guest.neuronas - INITIAL_NEURONAS) },
        levelId: Math.max(current.levelId, guest.levelId),
      },
    })
    return true
  })

  await clearGuestCookies()
  return { success: true, migrated }
}
