import { describe, it, expect } from 'vitest'
import prisma from '@/lib/prisma'
import { migrateGuestProgress } from '@/app/actions/sync'
import { startWord, submitGuess, requestHint } from '@/app/actions/game'
import { GUEST_COOKIE, LEGACY_GUEST_COOKIE, verifyGuestToken } from '@/lib/guest-cookie'
import { jar, loginAs, createRegisteredUser, getUserById, WORDS } from './harness'

// US3 — Loguearme con Google no rompe ni duplica mi progreso (FR-018..FR-022)

const [W1, W2, W3] = WORDS.L1
const EMAIL = 'jugador@example.com'

/** Juega como invitado: acierta W1, saltea W2 y pide una pista en W3. */
async function playAsGuest() {
  await startWord(W1.id)
  const guestId = verifyGuestToken(jar.get(GUEST_COOKIE)!.value)!
  await submitGuess(W1.id, 'MUDANZA') // +10 → 60, +1 punto
  await requestHint(W2.id, 'skip') // −15 → 45
  await requestHint(W3.id, 'letter') // −5 → 40
  return guestId
}

describe('US3 migración invitado → Google', () => {
  it('[SYNC-01] [SYNC-03] un invitado con progreso migra completo y el invitado deja de existir (FR-018, US3-1)', async () => {
    const guestId = await playAsGuest()
    const account = await createRegisteredUser(EMAIL, { neuronas: 50 })
    loginAs(EMAIL)

    const r = await migrateGuestProgress()
    expect(r).toEqual({ success: true, migrated: true })

    expect(await prisma.user.findUnique({ where: { id: guestId } })).toBeNull()
    expect(await prisma.progress.count({ where: { userId: guestId } })).toBe(0)
    expect(jar.has(GUEST_COOKIE)).toBe(false)
    expect(jar.has(LEGACY_GUEST_COOKIE)).toBe(false)

    const progress = await prisma.progress.findMany({ where: { userId: account.id }, orderBy: { wordId: 'asc' } })
    expect(progress.map((p) => [p.wordId, p.guessed, p.skipped])).toEqual([
      [W1.id, true, false],
      [W2.id, false, true],
      [W3.id, false, false],
    ])
    expect(progress[2].revealedIndices).toHaveLength(1)

    const u = await getUserById(account.id)
    expect(u.points).toBe(1)
    expect(u.totalTime).toBeCloseTo(progress[0].timeTaken!, 3)
    // El invitado terminó con 40 (< 50): la cuenta no pierde neuronas (FR-021, B-2).
    expect(u.neuronas).toBe(50)
  })

  it('[SYNC-02] recargar 10 veces después de migrar no cambia los totales (FR-020, SC-005)', async () => {
    await playAsGuest()
    const account = await createRegisteredUser(EMAIL)
    loginAs(EMAIL)
    await migrateGuestProgress()
    const after = await getUserById(account.id)

    for (let i = 0; i < 10; i++) {
      expect(await migrateGuestProgress()).toEqual({ success: true, migrated: false })
    }
    expect(await getUserById(account.id)).toEqual(after)
  })

  it('[SYNC-08] migraciones concurrentes (dos pestañas) ocurren una sola vez', async () => {
    await startWord(W1.id)
    await submitGuess(W1.id, 'MUDANZA')
    const account = await createRegisteredUser(EMAIL)
    loginAs(EMAIL)

    const results = await Promise.all([migrateGuestProgress(), migrateGuestProgress(), migrateGuestProgress()])
    expect(results.filter((r) => r.success && r.migrated)).toHaveLength(1)
    const u = await getUserById(account.id)
    expect(u.points).toBe(1)
    expect(await prisma.progress.count({ where: { userId: account.id } })).toBe(1)
  })

  it('[SYNC-03] neuronas excedentes del invitado se suman (FR-021)', async () => {
    await startWord(W1.id)
    await submitGuess(W1.id, 'MUDANZA') // invitado queda con 60
    const account = await createRegisteredUser(EMAIL, { neuronas: 100 })
    loginAs(EMAIL)
    await migrateGuestProgress()
    expect((await getUserById(account.id)).neuronas).toBe(110)
  })

  it('[SYNC-04] una palabra resuelta por ambos cuenta una sola vez (FR-022, US3-5)', async () => {
    const account = await createRegisteredUser(EMAIL, { points: 1, totalTime: 30 })
    await prisma.progress.create({ data: { userId: account.id, wordId: W1.id, guessed: true, timeTaken: 30 } })

    await startWord(W1.id)
    await submitGuess(W1.id, 'MUDANZA')
    loginAs(EMAIL)
    await migrateGuestProgress()

    const u = await getUserById(account.id)
    expect(u.points).toBe(1)
    expect(u.totalTime).toBe(30)
    const p = await prisma.progress.findUniqueOrThrow({ where: { userId_wordId: { userId: account.id, wordId: W1.id } } })
    expect(p.timeTaken).toBe(30)
  })

  it('[SYNC-01] el nivel resultante es el mayor de los dos', async () => {
    await startWord(W1.id)
    const guestId = verifyGuestToken(jar.get(GUEST_COOKIE)!.value)!
    await prisma.user.update({ where: { id: guestId }, data: { levelId: 2 } })
    const account = await createRegisteredUser(EMAIL, { levelId: 1 })
    loginAs(EMAIL)
    await migrateGuestProgress()
    expect((await getUserById(account.id)).levelId).toBe(2)
  })

  it('[SYNC-01] invitado sin progreso: se borra sin error', async () => {
    await startWord(WORDS.L2[0].id) // not_in_play: crea el invitado sin progreso
    const guestId = verifyGuestToken(jar.get(GUEST_COOKIE)!.value)!
    expect(await prisma.progress.count({ where: { userId: guestId } })).toBe(0)
    await createRegisteredUser(EMAIL)
    loginAs(EMAIL)
    expect(await migrateGuestProgress()).toEqual({ success: true, migrated: true })
    expect(await prisma.user.findUnique({ where: { id: guestId } })).toBeNull()
  })

  it('[SYNC-05] [SYNC-06] sin cookie de invitado no hay nada que migrar; sin sesión, error', async () => {
    expect(await migrateGuestProgress()).toEqual({ success: false, error: 'No authenticated session' })
    await createRegisteredUser(EMAIL)
    loginAs(EMAIL)
    expect(await migrateGuestProgress()).toEqual({ success: true, migrated: false })
  })
})
