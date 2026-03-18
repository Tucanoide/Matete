'use server'

import prisma from '@/lib/prisma'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { cookies } from 'next/headers'

const GUEST_COOKIE_NAME = 'matete-guest-id'

export async function migrateGuestProgress() {
  const session = await getServerSession(authOptions)
  if (!session?.user?.email) return { success: false, error: 'No authenticated session' }

  const cookieStore = await cookies()
  const guestId = cookieStore.get(GUEST_COOKIE_NAME)?.value

  if (!guestId) return { success: true, message: 'No guest data to migrate' }

  // 1. Get both users
  const [guestUser, authUser] = await Promise.all([
    prisma.user.findUnique({ 
        where: { id: guestId },
        include: { progress: true }
    }),
    prisma.user.findUnique({ 
        where: { email: session.user.email }
    })
  ])

  if (!guestUser || !authUser) {
    // If guestUser is gone, just clear cookie
    if (!guestUser) cookieStore.delete(GUEST_COOKIE_NAME)
    return { success: false, error: 'User mapping failed' }
  }

  // If it's the same user (unlikely but possible), just clear cookie
  if (guestUser.id === authUser.id) {
    cookieStore.delete(GUEST_COOKIE_NAME)
    return { success: true }
  }

  // 2. Transfer Progress
  // We want to move progress from Guest to AuthUser, evitando duplicados
  for (const p of guestUser.progress) {
    await prisma.progress.upsert({
      where: {
        userId_wordId: {
          userId: authUser.id,
          wordId: p.wordId
        }
      },
      update: {
        guessed: p.guessed || undefined,
        skipped: p.skipped || undefined,
        timeTaken: p.timeTaken,
        endTime: p.endTime
      },
      create: {
        userId: authUser.id,
        wordId: p.wordId,
        guessed: p.guessed,
        skipped: p.skipped,
        timeTaken: p.timeTaken,
        endTime: p.endTime
      }
    })
  }

  // 3. Update AuthUser stats
  await prisma.user.update({
    where: { id: authUser.id },
    data: {
      neuronas: { increment: guestUser.neuronas - 50 }, // Only migrate "extra" neuronas
      points: { increment: guestUser.points },
      totalTime: { increment: guestUser.totalTime },
      levelId: Math.max(authUser.levelId, guestUser.levelId)
    }
  })

  // 4. Cleanup
  await prisma.user.delete({ where: { id: guestUser.id } })
  cookieStore.delete(GUEST_COOKIE_NAME)

  return { success: true }
}
