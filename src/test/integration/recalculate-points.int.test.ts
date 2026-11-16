import { describe, it, expect, afterEach } from 'vitest'
import prisma from '@/lib/prisma'
import type { PrismaClient } from '@prisma/client'
import { recalculatePoints, BACKUP_TABLE } from '../../../scripts/recalculate-points'
import { createRegisteredUser, createGuestUser, getUserById, WORDS } from './harness'

// US4 / FR-026, FR-027 — recálculo único de puntos y tiempo desde Progress.

const db = prisma as unknown as PrismaClient

afterEach(async () => {
  await prisma.$executeRawUnsafe(`DROP TABLE IF EXISTS "${BACKUP_TABLE}"`)
})

async function seedInflated() {
  // Inflado como en producción: 349 puntos con 2 palabras acertadas.
  const inflated = await createRegisteredUser('inflado@example.com', { points: 349, totalTime: 5000, neuronas: 2420, username: 'inflado' })
  await prisma.progress.createMany({
    data: [
      { userId: inflated.id, wordId: WORDS.L1[0].id, guessed: true, timeTaken: 10 },
      { userId: inflated.id, wordId: WORDS.L1[1].id, guessed: true, timeTaken: 20.5 },
      { userId: inflated.id, wordId: WORDS.L1[2].id, skipped: true },
    ],
  })
  const ok = await createGuestUser({ points: 1, totalTime: 7 })
  await prisma.progress.create({ data: { userId: ok.id, wordId: WORDS.L1[0].id, guessed: true, timeTaken: 7 } })
  return { inflated, ok }
}

describe('scripts/recalculate-points', () => {
  it('[DATA-07] dry-run informa la diferencia y no escribe nada', async () => {
    const { inflated } = await seedInflated()
    const r = await recalculatePoints(db, { apply: false })
    expect(r).toMatchObject({ applied: false, usersAffected: 1, pointsDelta: -347 })
    expect(r.timeDelta).toBeCloseTo(30.5 - 5000, 3)
    expect(r.sample[0]).toMatchObject({ username: 'inflado', points: 349, realPoints: 2 })
    expect(r.sample[0]).not.toHaveProperty('id')
    expect((await getUserById(inflated.id)).points).toBe(349)
    const backup = await prisma.$queryRawUnsafe<{ exists: boolean }[]>(`SELECT to_regclass('public."${BACKUP_TABLE}"') IS NOT NULL AS exists`)
    expect(backup[0].exists).toBe(false)
  })

  it('[DATA-07] --apply respalda y deja points = aciertos y totalTime = suma de tiempos; neuronas intactas', async () => {
    const { inflated, ok } = await seedInflated()
    const r = await recalculatePoints(db, { apply: true })
    expect(r.applied).toBe(true)

    const u = await getUserById(inflated.id)
    expect(u.points).toBe(2)
    expect(u.totalTime).toBeCloseTo(30.5, 3)
    expect(u.neuronas).toBe(2420)
    expect((await getUserById(ok.id)).points).toBe(1)

    const backup = await prisma.$queryRawUnsafe<{ points: number; neuronas: number }[]>(
      `SELECT points, neuronas FROM "${BACKUP_TABLE}" WHERE id = $1`, inflated.id
    )
    expect(backup[0]).toMatchObject({ points: 349, neuronas: 2420 })

    // Después del recálculo no queda ninguna diferencia.
    expect((await recalculatePoints(db, { apply: false })).usersAffected).toBe(0)
  })

  it('[DATA-07] no se aplica dos veces si ya existe el respaldo', async () => {
    await seedInflated()
    await recalculatePoints(db, { apply: true })
    await expect(recalculatePoints(db, { apply: true })).rejects.toThrow(/ya existe/)
  })
})
