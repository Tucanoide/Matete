import { describe, it, expect } from 'vitest'
import prisma from '@/lib/prisma'
import {
  startWord,
  submitGuess,
  requestHint,
  getNextWord,
  getInitialGameState,
  moveToNextLevel,
} from '@/app/actions/game'
import { GUEST_COOKIE, verifyGuestToken } from '@/lib/guest-cookie'
import { jar, WORDS, getUserById } from './harness'

// US2 — Los puntos reflejan lo que realmente jugué (FR-006..FR-017)

const W1 = WORDS.L1[0] // Mudanza
const W2 = WORDS.L1[1] // Sencilla

async function currentUserId() {
  return verifyGuestToken(jar.get(GUEST_COOKIE)!.value)!
}

async function startAsGuest(wordId = W1.id) {
  await startWord(wordId)
  return currentUserId()
}

describe('US2 entrega y validación', () => {
  it('[GAME-12] startWord registra la entrega una sola vez y conserva el primer startTime (FR-015)', async () => {
    const userId = await startAsGuest()
    const first = await prisma.progress.findUniqueOrThrow({ where: { userId_wordId: { userId, wordId: W1.id } } })
    await new Promise((r) => setTimeout(r, 20))
    await startWord(W1.id)
    const again = await prisma.progress.findUniqueOrThrow({ where: { userId_wordId: { userId, wordId: W1.id } } })
    expect(again.startTime.getTime()).toBe(first.startTime.getTime())
    expect(await prisma.progress.count({ where: { userId } })).toBe(1)
  })

  it('[GAME-01] getInitialGameState y getNextWord devuelven PublicWord sin la solución (FR-006, SC-002)', async () => {
    const initial = await getInitialGameState()
    expect(initial).not.toBeNull()
    expect(JSON.stringify(initial).toUpperCase()).not.toContain(W1.word.toUpperCase())
    expect(initial!.word).toMatchObject({ wordId: W1.id, length: W1.word.length })
    expect(initial!.word.pool).toHaveLength(14)

    await startAsGuest()
    const next = await getNextWord(1)
    const solution = WORDS.L1.find((w) => w.id === next!.wordId)!.word.toUpperCase()
    expect(JSON.stringify(next).toUpperCase()).not.toContain(solution)
  })

  it('[GAME-02] [GAME-03] un acierto acredita +10 neuronas y +1 punto una sola vez aunque se repita 100 veces (FR-011, SC-003)', async () => {
    const userId = await startAsGuest()
    const first = await submitGuess(W1.id, 'mudanza')
    expect(first).toMatchObject({ correct: true, alreadySolved: false, solution: 'MUDANZA', neuronas: 60, points: 1 })

    for (let i = 0; i < 99; i++) {
      const r = await submitGuess(W1.id, 'MUDANZA')
      expect(r).toMatchObject({ correct: true, alreadySolved: true })
    }
    const u = await getUserById(userId)
    expect(u.neuronas).toBe(60)
    expect(u.points).toBe(1)
  })

  it('[GAME-04] aciertos concurrentes de la misma palabra acreditan una sola vez', async () => {
    const userId = await startAsGuest()
    const results = await Promise.all(Array.from({ length: 10 }, () => submitGuess(W1.id, 'MUDANZA')))
    expect(results.filter((r) => 'correct' in r && r.correct && !r.alreadySolved)).toHaveLength(1)
    const u = await getUserById(userId)
    expect(u.points).toBe(1)
    expect(u.neuronas).toBe(60)
  })

  it('[GAME-02] un intento incorrecto no cambia nada y no revela letras (FR-007, US2-3)', async () => {
    const userId = await startAsGuest()
    const r = await submitGuess(W1.id, 'MUDANZO')
    expect(r).toEqual({ correct: false })
    const u = await getUserById(userId)
    expect(u.points).toBe(0)
    expect(u.neuronas).toBe(50)
  })

  it('[GAME-13] rechaza intentos con formato inválido', async () => {
    await startAsGuest()
    expect(await submitGuess(W1.id, 'MUDANZ')).toEqual({ error: 'invalid_guess' })
    expect(await submitGuess(W1.id, 'MUD4NZA')).toEqual({ error: 'invalid_guess' })
    expect(await submitGuess(W1.id, '' as string)).toEqual({ error: 'invalid_guess' })
  })

  it('[GAME-06] rechaza palabras de otro nivel, inexistentes o salteadas (not_in_play)', async () => {
    await startAsGuest()
    expect(await submitGuess(WORDS.L2[0].id, 'MANIFIESTA')).toEqual({ error: 'not_in_play' })
    expect(await submitGuess(999999, 'XXXX')).toEqual({ error: 'not_in_play' })
    await requestHint(W2.id, 'skip').catch(() => undefined)
    // con 50 neuronas el salto (15) es posible
    expect(await submitGuess(W2.id, 'SENCILLA')).toEqual({ error: 'not_in_play' })
  })

  it('[GAME-05] el tiempo lo mide el servidor desde la entrega (FR-016, US2-5)', async () => {
    const userId = await startAsGuest()
    // Simular que la palabra se entregó hace 42 segundos.
    await prisma.progress.update({
      where: { userId_wordId: { userId, wordId: W1.id } },
      data: { startTime: new Date(Date.now() - 42_000) },
    })
    await submitGuess(W1.id, 'MUDANZA')
    const p = await prisma.progress.findUniqueOrThrow({ where: { userId_wordId: { userId, wordId: W1.id } } })
    expect(p.timeTaken!).toBeGreaterThanOrEqual(41)
    expect(p.timeTaken!).toBeLessThan(50)
    const u = await getUserById(userId)
    expect(u.totalTime).toBeCloseTo(p.timeTaken!, 3)
  })
})

describe('US2 economía', () => {
  it('[HINT-01] pista de letra devuelve letra y posición y cobra el costo configurado (FR-009)', async () => {
    const userId = await startAsGuest()
    const r = await requestHint(W1.id, 'letter')
    expect(r.success).toBe(true)
    if (!r.success || !('revealed' in r)) throw new Error('esperaba revealed')
    expect('MUDANZA'[r.revealed.index]).toBe(r.revealed.letter)
    expect(r.remainingNeuronas).toBe(45)
    expect((await getUserById(userId)).neuronas).toBe(45)
  })

  it('[HINT-02] máximo 2 pistas por palabra; la tercera se rechaza sin cobrar', async () => {
    const userId = await startAsGuest()
    await requestHint(W1.id, 'letter')
    await requestHint(W1.id, 'letter')
    const third = await requestHint(W1.id, 'letter')
    expect(third).toMatchObject({ success: false })
    expect((await getUserById(userId)).neuronas).toBe(40)
  })

  it('[HINT-04] dos pistas simultáneas con saldo para una sola: se cobra una y el saldo no queda negativo (FR-012, US2-6)', async () => {
    const userId = await startAsGuest()
    await prisma.user.update({ where: { id: userId }, data: { neuronas: 5 } })

    // Sobre la misma palabra y sobre palabras distintas.
    const results = await Promise.all([requestHint(W1.id, 'letter'), requestHint(W1.id, 'letter'), requestHint(W2.id, 'letter')])
    expect(results.filter((r) => r.success)).toHaveLength(1)
    const u = await getUserById(userId)
    expect(u.neuronas).toBe(0)
    const revealedTotal = (await prisma.progress.findMany({ where: { userId } })).reduce((n, p) => n + p.revealedIndices.length, 0)
    expect(revealedTotal).toBe(1)
  })

  it('[HINT-08] saltos simultáneos de la misma palabra cobran una sola vez', async () => {
    const userId = await startAsGuest()
    const results = await Promise.all([requestHint(W1.id, 'skip'), requestHint(W1.id, 'skip'), requestHint(W1.id, 'skip')])
    expect(results.filter((r) => r.success)).toHaveLength(1)
    expect((await getUserById(userId)).neuronas).toBe(35)
  })

  it('[HINT-05] pista o salto sobre una palabra acertada se rechazan sin cobrar (FR-013)', async () => {
    const userId = await startAsGuest()
    await submitGuess(W1.id, 'MUDANZA')
    expect(await requestHint(W1.id, 'letter')).toMatchObject({ success: false })
    expect(await requestHint(W1.id, 'skip')).toMatchObject({ success: false })
    expect((await getUserById(userId)).neuronas).toBe(60)
  })

  it('[HINT-03] sin saldo no se puede saltar', async () => {
    const userId = await startAsGuest()
    await prisma.user.update({ where: { id: userId }, data: { neuronas: 10 } })
    expect(await requestHint(W1.id, 'skip')).toEqual({ success: false, error: 'Not enough neuronas' })
    expect((await getUserById(userId)).neuronas).toBe(10)
  })
})

describe('US2 niveles', () => {
  it('[PROG-03] [PROG-08] moveToNextLevel rechaza con el nivel incompleto y avanza cuando está completo', async () => {
    const userId = await startAsGuest()
    expect(await moveToNextLevel()).toMatchObject({ success: false })

    await submitGuess(W1.id, 'MUDANZA')
    await submitGuess(W2.id, 'SENCILLA')
    const last = await submitGuess(WORDS.L1[2].id, 'montañoso')
    expect(last).toMatchObject({ correct: true, isLevelComplete: true, levelStats: { solved: 3, total: 3, levelId: 1 } })

    const moved = await moveToNextLevel()
    expect(moved).toMatchObject({ success: true, nextLevelId: 2 })
    if (!moved.success) throw new Error()
    expect(moved.nextWord?.wordId).toBe(WORDS.L2[0].id)
    expect(JSON.stringify(moved).toUpperCase()).not.toContain('MANIFIESTA')
    expect((await getUserById(userId)).levelId).toBe(2)
  })

  it('[GAME-11] [PROG-01] un nivel de 50 palabras se completa exactamente en la 50ª (reemplaza el test con Prisma mockeado)', async () => {
    const userId = await startAsGuest()
    await prisma.level.create({ data: { id: 9, name: 'Nivel 9' } })
    const words = Array.from({ length: 50 }, (_, i) => ({ id: 9000 + i, word: `PALABRA${String.fromCharCode(65 + (i % 26))}`, description: `d${i}`, levelId: 9 }))
    await prisma.word.createMany({ data: words })
    await prisma.user.update({ where: { id: userId }, data: { levelId: 9 } })

    for (let i = 0; i < 50; i++) {
      const r = await submitGuess(words[i].id, words[i].word)
      expect(r).toMatchObject({ correct: true, isLevelComplete: i === 49, levelStats: { solved: i + 1, total: 50 } })
    }
    const u = await getUserById(userId)
    expect(u.points).toBe(50)
    expect(u.neuronas).toBe(50 + 50 * 10)
  })

  it('[PROG-02] el salto de la última palabra completa el nivel', async () => {
    await startAsGuest()
    await submitGuess(W1.id, 'MUDANZA')
    await submitGuess(W2.id, 'SENCILLA')
    const r = await requestHint(WORDS.L1[2].id, 'skip')
    expect(r).toMatchObject({ success: true, isLevelComplete: true })
  })

  it('[GAME-08] getInitialGameState con todos los niveles completos devuelve null en lugar de romper (B-6)', async () => {
    const userId = await startAsGuest()
    await prisma.user.update({ where: { id: userId }, data: { levelId: 2 } })
    for (const w of [...WORDS.L1, ...WORDS.L2]) {
      await prisma.progress.upsert({
        where: { userId_wordId: { userId, wordId: w.id } },
        update: { guessed: true },
        create: { userId, wordId: w.id, guessed: true },
      })
    }
    expect(await getInitialGameState()).toBeNull()
  })
})
