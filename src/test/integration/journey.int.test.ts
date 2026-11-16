import { describe, it, expect } from 'vitest'
import prisma from '@/lib/prisma'
import { getMyJourney } from '@/app/actions/journey'
import { startWord } from '@/app/actions/game'
import { GUEST_COOKIE, verifyGuestToken } from '@/lib/guest-cookie'
import { as, jar, createRegisteredUser, createSocialUser, solveWord, WORDS } from './harness'

// 003-recorrido — Mi recorrido (specs/003-recorrido/spec.md).

const [A, B, C] = WORDS.L1
const [D] = WORDS.L2

async function guest() {
  await startWord(A.id)
  return verifyGuestToken(jar.get(GUEST_COOKIE)!.value)!
}

async function journey() {
  const r = await getMyJourney()
  if (!r.ok) throw new Error(r.error)
  return r.journey
}

describe('[JOUR-01] resumen y avance por nivel', () => {
  it('cuenta acertadas, salteadas, en curso, tiempos y pistas desde Progress', async () => {
    const id = await guest() // A queda "en curso" hasta que se resuelve abajo
    await solveWord(id, A.id, { timeTaken: 30, revealedIndices: [1] })
    await solveWord(id, B.id, { timeTaken: 10 })
    await prisma.progress.create({ data: { userId: id, wordId: C.id, skipped: true } })
    await solveWord(id, D.id, { timeTaken: 50, revealedIndices: [0, 2] })
    await prisma.progress.create({ data: { userId: id, wordId: WORDS.L2[1].id } }) // en curso
    await prisma.user.update({ where: { id }, data: { levelId: 2, points: 3, neuronas: 65 } })

    const j = await journey()
    expect(j).toMatchObject({
      isGuest: true, username: null, levelId: 2, points: 3, neuronas: 65, rank: null,
      guessed: 3, skipped: 1, inProgress: 1,
      totalTime: 90, avgTime: 30, bestTime: 10,
      hintsUsed: 3, solvedWithoutHints: 1,
      challenges: null,
    })
    expect(j.levels).toEqual([
      { levelId: 1, name: 'Nivel 1', totalWords: 3, guessed: 2, skipped: 1, avgTime: 20, hints: 1 },
      { levelId: 2, name: 'Nivel 2', totalWords: 2, guessed: 1, skipped: 0, avgTime: 50, hints: 2 },
    ])
  })

  it('no devuelve palabras, definiciones ni ids', async () => {
    const id = await guest()
    await solveWord(id, A.id, { timeTaken: 30 })
    const text = JSON.stringify(await getMyJourney())
    for (const w of [...WORDS.L1, ...WORDS.L2]) {
      expect(text.toLowerCase()).not.toContain(w.word.toLowerCase())
      expect(text).not.toContain(w.description)
    }
    expect(text).not.toContain(id)
    expect(text).not.toMatch(/"(id|userId|wordId)"/)
  })

  it('aciertos sin tiempo cuentan pero no entran en promedio ni mejor tiempo; sin aciertos no divide por cero', async () => {
    const id = await guest()
    expect(await journey()).toMatchObject({ guessed: 0, avgTime: null, bestTime: null, totalTime: 0 })
    await solveWord(id, A.id, { timeTaken: 0 })
    const j = await journey()
    expect(j).toMatchObject({ guessed: 1, avgTime: null, bestTime: null })
    expect(j.levels[0]).toMatchObject({ levelId: 1, guessed: 1, avgTime: null })
  })

  it('el nivel actual aparece aunque no tenga progreso', async () => {
    const id = await guest()
    await prisma.progress.deleteMany({ where: { userId: id } })
    await prisma.user.update({ where: { id }, data: { levelId: 2 } })
    expect((await journey()).levels).toEqual([
      { levelId: 2, name: 'Nivel 2', totalWords: 2, guessed: 0, skipped: 0, avgTime: null, hints: 0 },
    ])
  })

  it('sin cookie ni sesión: no_player y no crea usuarios ni cookies', async () => {
    expect(await getMyJourney()).toEqual({ ok: false, error: 'no_player' })
    expect(await prisma.user.count()).toBe(0)
    expect(jar.size).toBe(0)
  })
})

describe('[JOUR-02] posición en el ranking (solo Google)', () => {
  it('usa el mismo orden que el ranking global', async () => {
    await createSocialUser('primero', { points: 9 })
    await createSocialUser('empate_rapido', { points: 5, totalTime: 10 })
    const me = await createSocialUser('yo', { points: 5, totalTime: 20 })
    await createSocialUser('ultimo', { points: 1 })
    await prisma.user.create({ data: { name: 'Explorador Anónimo', points: 99 } }) // invitado: no cuenta
    await solveWord(me.id, A.id)

    const j = await as('yo@example.com', journey)
    expect(j).toMatchObject({ isGuest: false, username: 'yo', rank: 3 })
  })
})

describe('[JOUR-03] desafíos', () => {
  it('creados, jugados (terminados) y ganados con la regla de los desafíos', async () => {
    const yo = await createSocialUser('yo')
    const otro = await createSocialUser('otro')
    const mk = (creatorId: string, wordId: number, code: string, creatorTime: number) =>
      prisma.challenge.create({ data: { creatorId, wordId, code, creatorTime, creatorHints: 0 } })

    await mk(yo.id, A.id, 'AAAAAAAA', 30)
    await mk(yo.id, B.id, 'BBBBBBBB', 30)
    const c1 = await mk(otro.id, A.id, 'CCCCCCCC', 60)
    const c2 = await mk(otro.id, B.id, 'DDDDDDDD', 60)
    const c3 = await mk(otro.id, C.id, 'EEEEEEEE', 60)
    const c4 = await mk(otro.id, D.id, 'FFFFFFFF', 60)
    const attempt = (challengeId: string, data: object) =>
      prisma.challengeAttempt.create({ data: { challengeId, userId: yo.id, startTime: new Date(), ...data } })

    await attempt(c1.id, { endTime: new Date(), solved: true, timeTaken: 20 }) // ganó
    await attempt(c2.id, { endTime: new Date(), solved: true, timeTaken: 20, knewWord: true }) // ya la conocía
    await attempt(c3.id, { endTime: new Date(), solved: true, timeTaken: 90 }) // más lento
    await attempt(c4.id, {}) // sin terminar: no cuenta

    const j = await as('yo@example.com', journey)
    expect(j.challenges).toEqual({ created: 2, played: 3, won: 1 })
  })

  it('null para invitados y para cuentas de Google sin alias', async () => {
    await guest()
    expect((await journey()).challenges).toBeNull()

    const sin = await createRegisteredUser('sin@example.com')
    await solveWord(sin.id, A.id)
    const j = await as('sin@example.com', journey)
    expect(j).toMatchObject({ isGuest: false, challenges: null })
    expect(j.rank).toBe(1)
  })
})
