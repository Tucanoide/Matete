import { describe, it, expect } from 'vitest'
import prisma from '@/lib/prisma'
import * as gameActions from '@/app/actions/game'
import * as syncActions from '@/app/actions/sync'
import {
  getInitialGameState,
  getNextWord,
  startWord,
  submitGuess,
  requestHint,
  moveToNextLevel,
  getGlobalRanking,
  getLevelRanking,
  updateUsername,
} from '@/app/actions/game'
import { migrateGuestProgress } from '@/app/actions/sync'
import { authOptions } from '@/lib/auth'
import { GUEST_COOKIE, LEGACY_GUEST_COOKIE, signGuestToken, verifyGuestToken } from '@/lib/guest-cookie'
import { jar, loginAs, createGuestUser, createRegisteredUser, solveWord, WORDS } from './harness'

// Casos del catálogo (tests/CASOS_DE_PRUEBA.md) que no tenían test propio.
// Cada test cita su ID.

const [W1, W2, W3] = WORDS.L1

function guestFromCookie() {
  return verifyGuestToken(jar.get(GUEST_COOKIE)!.value)!
}

describe('AUTH', () => {
  it.each([['no-existe'], [''], ['x'.repeat(10_000)], ["'; DROP TABLE \"User\"; --"]])(
    '[AUTH-05] una cookie con valor arbitrario (%#) se trata como visitante nuevo sin error',
    async (value) => {
      process.env.GUEST_LEGACY_COOKIE_UNTIL = '2099-12-31' // también por el camino legacy
      jar.set(GUEST_COOKIE, { value })
      jar.set(LEGACY_GUEST_COOKIE, { value })
      const state = await getInitialGameState()
      expect(state?.needsUsername).toBe(false)
      expect(await startWord(W1.id)).toEqual({ ok: true })
      expect(await prisma.user.count()).toBe(1)
      delete process.env.GUEST_LEGACY_COOKIE_UNTIL
    }
  )

  it('[AUTH-06] con sesión de Google la cookie de invitado se ignora', async () => {
    const a = await createRegisteredUser('a@example.com')
    const b = await createGuestUser()
    jar.set(GUEST_COOKIE, { value: signGuestToken(b.id)! })
    loginAs('a@example.com')
    await startWord(W1.id)
    expect(await prisma.progress.count({ where: { userId: a.id } })).toBe(1)
    expect(await prisma.progress.count({ where: { userId: b.id } })).toBe(0)
  })

  it('[AUTH-07] sesión con un email inexistente nunca resuelve a otra cuenta registrada', async () => {
    const other = await createRegisteredUser('otra@example.com')
    jar.set(GUEST_COOKIE, { value: signGuestToken(other.id)! })
    loginAs('borrada@example.com')
    await startWord(W1.id)
    expect(await prisma.progress.count({ where: { userId: other.id } })).toBe(0)
    // Cae al camino de invitado: se crea uno nuevo, sin email.
    const guest = await prisma.user.findUniqueOrThrow({ where: { id: guestFromCookie() } })
    expect(guest.email).toBeNull()
  })

  it('[AUTH-08] el callback de sesión expone points/neuronas/levelId de la base y usa 50 como respaldo', async () => {
    const u = await createRegisteredUser('s@example.com', { points: 4, neuronas: 77, levelId: 2 })
    const session = await authOptions.callbacks!.session!({ session: { user: { email: 's@example.com' } }, user: { id: u.id } } as never)
    expect((session as { user: Record<string, unknown> }).user).toMatchObject({ points: 4, neuronas: 77, levelId: 2 })

    const fallback = await authOptions.callbacks!.session!({ session: { user: {} }, user: { id: 'inexistente' } } as never)
    expect((fallback as { user: Record<string, unknown> }).user).toMatchObject({ points: 0, neuronas: 50, levelId: 1 })
  })

  it('[AUTH-12] getUser/ensureUser no están expuestas como Server Actions', () => {
    for (const mod of [gameActions, syncActions]) {
      expect(mod).not.toHaveProperty('getUser')
      expect(mod).not.toHaveProperty('ensureUser')
      expect(mod).not.toHaveProperty('getCurrentUser')
      expect(mod).not.toHaveProperty('ensureCurrentUser')
    }
  })
})

describe('GAME', () => {
  it('[GAME-07] el acierto registra Progress con guessed, endTime y timeTaken, y suma al totalTime', async () => {
    await startWord(W1.id)
    const id = guestFromCookie()
    await submitGuess(W1.id, W1.word)
    const p = await prisma.progress.findUniqueOrThrow({ where: { userId_wordId: { userId: id, wordId: W1.id } } })
    expect(p.guessed).toBe(true)
    expect(p.endTime).not.toBeNull()
    expect(p.timeTaken).not.toBeNull()
    const u = await prisma.user.findUniqueOrThrow({ where: { id } })
    expect(u.totalTime).toBeCloseTo(p.timeTaken!, 5)
  })

  it('[GAME-08] un nivel sin palabras y sin siguiente nivel devuelve null sin romper', async () => {
    await prisma.level.create({ data: { id: 99, name: 'Vacío' } })
    const u = await createRegisteredUser('v@example.com', { levelId: 99 })
    loginAs('v@example.com')
    expect(await getInitialGameState()).toBeNull()
    expect((await prisma.user.findUniqueOrThrow({ where: { id: u.id } })).levelId).toBe(99)
  })

  it('[GAME-09] reanuda en la primera palabra sin resolver, con sus letras reveladas', async () => {
    const u = await createRegisteredUser('r@example.com')
    await solveWord(u.id, W1.id)
    await prisma.progress.create({ data: { userId: u.id, wordId: W2.id, skipped: true } })
    await prisma.progress.create({ data: { userId: u.id, wordId: W3.id, revealedIndices: [0] } })
    loginAs('r@example.com')
    const state = await getInitialGameState()
    expect(state?.word.wordId).toBe(W3.id)
    expect(state?.word.revealed).toEqual([{ index: 0, letter: 'M' }])
  })

  it('[GAME-10] las palabras salteadas o acertadas no vuelven a salir', async () => {
    await startWord(W1.id)
    const id = guestFromCookie()
    await prisma.progress.create({ data: { userId: id, wordId: W2.id, skipped: true } })
    await solveWord(id, W1.id)
    for (let i = 0; i < 30; i++) {
      expect((await getNextWord(1))?.wordId).toBe(W3.id)
    }
  })
})

describe('HINT', () => {
  it('[HINT-03] sin saldo para la letra no cobra ni revela', async () => {
    await startWord(W1.id)
    const id = guestFromCookie()
    await prisma.user.update({ where: { id }, data: { neuronas: 3 } })
    expect(await requestHint(W1.id, 'letter')).toEqual({ success: false, error: 'Not enough neuronas' })
    expect((await prisma.user.findUniqueOrThrow({ where: { id } })).neuronas).toBe(3)
    const p = await prisma.progress.findUniqueOrThrow({ where: { userId_wordId: { userId: id, wordId: W1.id } } })
    expect(p.revealedIndices).toEqual([])
  })

  it('[HINT-06] pista o salto sobre una palabra de otro nivel se rechazan sin cobrar', async () => {
    await startWord(W1.id)
    const id = guestFromCookie()
    expect(await requestHint(WORDS.L2[0].id, 'letter')).toMatchObject({ success: false })
    expect(await requestHint(WORDS.L2[0].id, 'skip')).toMatchObject({ success: false })
    expect((await prisma.user.findUniqueOrThrow({ where: { id } })).neuronas).toBe(50)
  })
})

describe('PROG', () => {
  it('[PROG-04] en el último nivel no avanza a un nivel inexistente', async () => {
    const u = await createRegisteredUser('fin@example.com', { levelId: 2 })
    for (const w of WORDS.L2) await solveWord(u.id, w.id)
    loginAs('fin@example.com')
    expect(await moveToNextLevel()).toEqual({ success: false, error: 'No hay más niveles.', gameOver: true })
    expect((await prisma.user.findUniqueOrThrow({ where: { id: u.id } })).levelId).toBe(2)
  })

  it('[PROG-07] getInitialGameState auto-avanza si el nivel está completo', async () => {
    const u = await createRegisteredUser('auto@example.com')
    await solveWord(u.id, W1.id)
    await solveWord(u.id, W2.id)
    await prisma.progress.create({ data: { userId: u.id, wordId: W3.id, skipped: true } })
    loginAs('auto@example.com')
    const state = await getInitialGameState()
    expect(state?.levelId).toBe(2)
    expect(state?.word.wordId).toBe(WORDS.L2[0].id)
    expect((await prisma.user.findUniqueOrThrow({ where: { id: u.id } })).levelId).toBe(2)
  })
})

describe('SYNC', () => {
  it('[SYNC-07] cookie que apunta a un invitado ya borrado: no migra nada ni rompe', async () => {
    const ghost = await createGuestUser()
    const token = signGuestToken(ghost.id)!
    await prisma.user.delete({ where: { id: ghost.id } })
    const account = await createRegisteredUser('c@example.com', { points: 2, neuronas: 80 })
    jar.set(GUEST_COOKIE, { value: token })
    loginAs('c@example.com')
    const r = await migrateGuestProgress()
    expect(r).toMatchObject({ success: true, migrated: false })
    expect(jar.has(GUEST_COOKIE)).toBe(false)
    expect(await prisma.user.findUniqueOrThrow({ where: { id: account.id } })).toMatchObject({ points: 2, neuronas: 80 })
  })
})

describe('RANK', () => {
  it('[RANK-02] el ranking global ordena por puntos, luego menor tiempo, luego neuronas', async () => {
    await createRegisteredUser('a@example.com', { username: 'a', points: 5, totalTime: 100, neuronas: 10 })
    await createRegisteredUser('b@example.com', { username: 'b', points: 5, totalTime: 100, neuronas: 90 })
    await createRegisteredUser('c@example.com', { username: 'c', points: 5, totalTime: 50, neuronas: 0 })
    await createRegisteredUser('d@example.com', { username: 'd', points: 9, totalTime: 999, neuronas: 0 })
    expect((await getGlobalRanking()).map((e) => e.username)).toEqual(['d', 'c', 'b', 'a'])
  })

  it('[RANK-04] el ranking por nivel cuenta solo aciertos, no saltos', async () => {
    const u = await createRegisteredUser('n@example.com', { username: 'n' })
    await solveWord(u.id, W1.id, { timeTaken: 10 })
    await prisma.progress.createMany({ data: [W2, W3].map((w) => ({ userId: u.id, wordId: w.id, skipped: true })) })
    const level = await getLevelRanking(1)
    expect(level[0]).toMatchObject({ username: 'n', levelPoints: 1, levelTime: 10 })
  })
})

describe('USER', () => {
  it('[USER-04] dos jugadores piden el mismo alias a la vez: el unique de la base deja uno solo', async () => {
    const a = await createRegisteredUser('ua@example.com')
    const b = await createRegisteredUser('ub@example.com')
    loginAs('ua@example.com')
    const pa = updateUsername('Mismo')
    loginAs('ub@example.com')
    const pb = updateUsername('Mismo')
    const results = await Promise.all([pa, pb])
    expect(results.filter((r) => r.success)).toHaveLength(1)
    const owners = await prisma.user.count({ where: { username: 'Mismo', id: { in: [a.id, b.id] } } })
    expect(owners).toBe(1)
  })

  it('[USER-05] needsUsername: true sin alias (invitado o registrado), false con alias', async () => {
    await createRegisteredUser('sin@example.com')
    loginAs('sin@example.com')
    expect((await getInitialGameState())?.needsUsername).toBe(true)
    await updateUsername('ConNombre')
    expect((await getInitialGameState())?.needsUsername).toBe(false)
  })
})

describe('DATA', () => {
  it('[DATA-04] el saldo inicial de neuronas es 50 también por el default de la base', async () => {
    await prisma.$executeRaw`INSERT INTO "User" (id, name) VALUES ('raw-user', 'raw')`
    const u = await prisma.user.findUniqueOrThrow({ where: { id: 'raw-user' } })
    expect(u.neuronas).toBe(50)
  })

  it('[DATA-03] Progress restringe el borrado del usuario (decisión: se borra explícitamente, sin cascada)', async () => {
    const u = await createGuestUser()
    await solveWord(u.id, W1.id)
    await expect(prisma.user.delete({ where: { id: u.id } })).rejects.toThrow()
    await prisma.progress.deleteMany({ where: { userId: u.id } })
    await prisma.user.delete({ where: { id: u.id } })
    expect(await prisma.user.count()).toBe(0)
  })
})
