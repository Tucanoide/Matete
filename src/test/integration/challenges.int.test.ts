import { describe, it, expect } from 'vitest'
import prisma from '@/lib/prisma'
import {
  createChallenge,
  getChallenge,
  startChallenge,
  submitChallengeGuess,
  giveUpChallenge,
  getMyChallenges,
  getChallengeableWords,
} from '@/app/actions/challenges'
import { as, createSocialUser, createRegisteredUser, solveWord, WORDS } from './harness'

// 002-social US4 — desafíos entre amigos (FR-020..FR-030).

const W = WORDS.L1[0] // Mudanza (7)
const ANA = 'ana@example.com'
const BETO = 'beto@example.com'

async function setup(creatorTime = 42) {
  const ana = await createSocialUser('ana')
  const beto = await createSocialUser('beto', { neuronas: 77, points: 3, totalTime: 12, levelId: 1 })
  await solveWord(ana.id, W.id, { timeTaken: creatorTime, revealedIndices: [2] })
  const created = await as(ANA, () => createChallenge(W.id))
  if (!created.ok) throw new Error(created.error)
  return { ana, beto, code: created.code }
}

/** Corre el reloj del intento hacia atrás (simula tiempo transcurrido en el servidor). */
async function elapse(userId: string, seconds: number) {
  await prisma.$executeRaw`UPDATE "ChallengeAttempt" SET "startTime" = "startTime" - make_interval(secs => ${seconds}) WHERE "userId" = ${userId}`
}

describe('[SOC-31] crear (FR-020, FR-021)', () => {
  it('guarda tiempo y pistas del servidor; repetir devuelve el mismo código', async () => {
    const { code } = await setup(42)
    const c = await prisma.challenge.findUniqueOrThrow({ where: { code } })
    expect(c).toMatchObject({ creatorTime: 42, creatorHints: 1 })
    expect(await as(ANA, () => createChallenge(W.id))).toEqual({ ok: true, code, username: 'ana' })
    expect(await prisma.challenge.count()).toBe(1)
  })

  it('creaciones simultáneas de la misma palabra: un solo desafío', async () => {
    const ana = await createSocialUser('ana')
    await solveWord(ana.id, W.id)
    const res = await as(ANA, () => Promise.all([createChallenge(W.id), createChallenge(W.id), createChallenge(W.id)]))
    const codes = new Set(res.map((r) => (r.ok ? r.code : r.error)))
    expect(codes.size).toBe(1)
    expect(await prisma.challenge.count()).toBe(1)
  })

  it('rechaza palabra no acertada (salteada o en juego), sin sesión o sin username', async () => {
    const ana = await createSocialUser('ana')
    await createRegisteredUser('sin@example.com')
    await prisma.progress.create({ data: { userId: ana.id, wordId: W.id, skipped: true } })
    expect(await as(ANA, () => createChallenge(W.id))).toEqual({ ok: false, error: 'not_solved' })
    expect(await as(ANA, () => createChallenge(WORDS.L1[1].id))).toEqual({ ok: false, error: 'not_solved' })
    expect(await as(null, () => createChallenge(W.id))).toEqual({ ok: false, error: 'login_required' })
    expect(await as('sin@example.com', () => createChallenge(W.id))).toEqual({ ok: false, error: 'username_required' })
  })

  it('tope de 20 desafíos en 24 h', async () => {
    const ana = await createSocialUser('ana')
    const words = await prisma.word.findMany()
    await prisma.word.createMany({ data: Array.from({ length: 20 }, (_, i) => ({ id: 5000 + i, word: 'Prueba', description: 'x', levelId: 1 })) })
    for (let i = 0; i < 20; i++) await prisma.challenge.create({ data: { code: `seed${i}`.padEnd(10, 'x'), creatorId: ana.id, wordId: 5000 + i, creatorTime: 1, creatorHints: 0 } })
    await solveWord(ana.id, words[0].id)
    expect(await as(ANA, () => createChallenge(words[0].id))).toEqual({ ok: false, error: 'daily_limit' })
  })
})

describe('[SOC-32] página pública (FR-022, FR-023, SC-002)', () => {
  it('cualquiera ve desafiante, largo y tiempo, sin definición ni solución', async () => {
    const { ana, code } = await setup()
    const anon = await as(null, () => getChallenge(code))
    expect(anon).toMatchObject({ code, creator: { username: 'ana' }, length: 7, creatorTime: 42, creatorHints: 1, viewer: 'anonymous', solution: null, results: [] })
    const json = JSON.stringify(anon)
    expect(json.toLowerCase()).not.toContain(W.word.toLowerCase())
    expect(json).not.toContain(W.description)
    expect(json).not.toContain(ana.id)

    expect((await as(ANA, () => getChallenge(code)))?.viewer).toBe('creator')
    expect((await as(BETO, () => getChallenge(code)))?.viewer).toBe('can_play')
    await createRegisteredUser('sin@example.com')
    expect((await as('sin@example.com', () => getChallenge(code)))?.viewer).toBe('username_required')
  })

  it('código inexistente o mal formado: null', async () => {
    expect(await getChallenge('noexiste00')).toBeNull()
    expect(await getChallenge("x' OR 1=1")).toBeNull()
  })
})

describe('[SOC-33] jugar (FR-024..FR-029, SC-004)', () => {
  it('B gana con tiempo medido en el servidor y lo ven ambos', async () => {
    const { beto, code } = await setup(42)
    const start = await as(BETO, () => startChallenge(code))
    if (!start.ok) throw new Error(start.error)
    expect(start.word).toMatchObject({ description: W.description, length: 7 })
    expect(start.word.pool).toHaveLength(14)
    expect((await as(BETO, () => getChallenge(code)))?.viewer).toBe('in_progress')

    await elapse(beto.id, 30)
    expect(await as(BETO, () => submitChallengeGuess(code, 'MUDANZO'))).toEqual({ correct: false })
    const r = await as(BETO, () => submitChallengeGuess(code, 'mudanza'))
    if (!('correct' in r) || !r.correct) throw new Error('esperaba acierto')
    expect(r.timeTaken).toBeGreaterThanOrEqual(30)
    expect(r.timeTaken).toBeLessThan(42)
    expect(r).toMatchObject({ solution: 'MUDANZA', won: true, knewWord: false, creatorTime: 42 })

    const view = await as(ANA, () => getChallenge(code))
    expect(view?.results).toHaveLength(1)
    expect(view?.results[0]).toMatchObject({ username: 'beto', solved: true, won: true })
    const mine = await as(BETO, () => getChallenge(code))
    expect(mine).toMatchObject({ viewer: 'finished', solution: 'MUDANZA', myResult: { won: true } })
  })

  it('el tiempo corre desde la primera apertura; reabrir no lo reinicia', async () => {
    const { beto, code } = await setup()
    await as(BETO, () => startChallenge(code))
    await elapse(beto.id, 100)
    const again = await as(BETO, () => startChallenge(code))
    if (!again.ok) throw new Error()
    expect(again.elapsed).toBeGreaterThanOrEqual(100)
    expect(await prisma.challengeAttempt.count()).toBe(1)
    const r = await as(BETO, () => submitChallengeGuess(code, W.word))
    if (!('correct' in r) || !r.correct) throw new Error()
    expect(r.timeTaken).toBeGreaterThanOrEqual(100)
    expect(r.won).toBe(false)
  })

  it('un solo intento: después de terminar no se puede volver a jugar ni reacreditar', async () => {
    const { code } = await setup()
    await as(BETO, () => startChallenge(code))
    const results = await as(BETO, () => Promise.all([submitChallengeGuess(code, W.word), submitChallengeGuess(code, W.word)]))
    expect(results.filter((r) => 'correct' in r && r.correct)).toHaveLength(1)
    expect(await as(BETO, () => startChallenge(code))).toEqual({ ok: false, error: 'finished' })
    expect(await as(BETO, () => submitChallengeGuess(code, W.word))).toEqual({ error: 'finished' })
    expect(await as(BETO, () => giveUpChallenge(code))).toEqual({ ok: false, error: 'finished' })
  })

  it('el creador no juega; sin empezar no se puede adivinar; sin sesión tampoco', async () => {
    const { code } = await setup()
    expect(await as(ANA, () => startChallenge(code))).toEqual({ ok: false, error: 'own_challenge' })
    expect(await as(BETO, () => submitChallengeGuess(code, W.word))).toEqual({ error: 'not_started' })
    expect(await as(null, () => startChallenge(code))).toEqual({ ok: false, error: 'login_required' })
    expect(await as(BETO, () => startChallenge('noexiste00'))).toEqual({ ok: false, error: 'not_found' })
  })

  it('formato inválido no cuenta como intento', async () => {
    const { code } = await setup()
    await as(BETO, () => startChallenge(code))
    expect(await as(BETO, () => submitChallengeGuess(code, 'abc'))).toEqual({ error: 'invalid_guess' })
    expect(await as(BETO, () => submitChallengeGuess(code, 'mud4nza'))).toEqual({ error: 'invalid_guess' })
    expect((await as(BETO, () => getChallenge(code)))?.viewer).toBe('in_progress')
  })

  it('rendirse muestra la palabra y queda como no resuelto', async () => {
    const { code } = await setup()
    expect(await as(BETO, () => giveUpChallenge(code))).toEqual({ ok: false, error: 'not_started' })
    await as(BETO, () => startChallenge(code))
    expect(await as(BETO, () => giveUpChallenge(code))).toEqual({ ok: true, solution: 'MUDANZA' })
    const view = await as(BETO, () => getChallenge(code))
    expect(view?.myResult).toMatchObject({ solved: false, gaveUp: true, won: false, timeTaken: null })
  })

  it('[SOC-34] "ya la conocía": juega pero no gana (FR-027)', async () => {
    const { beto, code } = await setup(42)
    await solveWord(beto.id, W.id)
    await as(BETO, () => startChallenge(code))
    const r = await as(BETO, () => submitChallengeGuess(code, W.word))
    if (!('correct' in r) || !r.correct) throw new Error()
    expect(r).toMatchObject({ knewWord: true, won: false })
  })

  it('orden de resultados: aciertos por tiempo, después rendidos', async () => {
    const { code } = await setup(42)
    const players = ['caro', 'dani', 'eli']
    for (const p of players) await createSocialUser(p)
    for (const [p, secs] of [['caro', 50], ['dani', 10]] as const) {
      await as(`${p}@example.com`, () => startChallenge(code))
      const u = await prisma.user.findFirstOrThrow({ where: { username: p } })
      await elapse(u.id, secs)
      await as(`${p}@example.com`, () => submitChallengeGuess(code, W.word))
    }
    await as('eli@example.com', () => startChallenge(code))
    await as('eli@example.com', () => giveUpChallenge(code))
    await as(BETO, () => startChallenge(code)) // en curso: no aparece

    const view = await getChallenge(code)
    expect(view?.results.map((r) => [r.username, r.won])).toEqual([
      ['dani', true],
      ['caro', false],
      ['eli', false],
    ])
  })
})

describe('[SOC-35] sin efecto en la economía ni el progreso (FR-028, SC-005)', () => {
  it('jugar y rendirse no cambian neuronas, puntos, tiempo, nivel ni Progress', async () => {
    const { beto, code } = await setup()
    const before = await prisma.user.findUniqueOrThrow({ where: { id: beto.id } })
    const progressBefore = await prisma.progress.findMany({ where: { userId: beto.id } })

    await as(BETO, () => startChallenge(code))
    await as(BETO, () => submitChallengeGuess(code, 'XXXXXXX'))
    await as(BETO, () => submitChallengeGuess(code, W.word))

    const after = await prisma.user.findUniqueOrThrow({ where: { id: beto.id } })
    expect(after).toEqual(before)
    expect(await prisma.progress.findMany({ where: { userId: beto.id } })).toEqual(progressBefore)
  })
})

describe('[SOC-36] mis desafíos (FR-030)', () => {
  it('lista creados con participantes y jugados con resultado', async () => {
    const { code } = await setup()
    await as(BETO, () => startChallenge(code))
    await as(BETO, () => submitChallengeGuess(code, W.word))

    const mineAna = await as(ANA, () => getMyChallenges())
    if (!mineAna.ok) throw new Error()
    expect(mineAna.created).toEqual([expect.objectContaining({ code, length: 7, creatorTime: 42, participants: 1 })])
    expect(mineAna.played).toEqual([])

    const mineBeto = await as(BETO, () => getMyChallenges())
    if (!mineBeto.ok) throw new Error()
    expect(mineBeto.played).toEqual([expect.objectContaining({ code, creator: { username: 'ana', image: null }, result: expect.objectContaining({ solved: true }) })])
    expect(JSON.stringify(mineBeto)).not.toMatch(/"(id|userId|creatorId)"/)
    expect(await as(null, () => getMyChallenges())).toEqual({ ok: false, error: 'login_required' })
  })
})

describe('[SOC-38] palabras para crear un desafío desde "Mis desafíos"', () => {
  it('lista solo las acertadas por el jugador, con su tiempo y si ya tienen desafío', async () => {
    const ana = await createSocialUser('ana')
    const beto = await createSocialUser('beto')
    await solveWord(ana.id, WORDS.L1[0].id, { timeTaken: 42 })
    await solveWord(ana.id, WORDS.L1[1].id, { timeTaken: 10 })
    await prisma.progress.create({ data: { userId: ana.id, wordId: WORDS.L1[2].id, skipped: true } })
    await solveWord(beto.id, WORDS.L2[0].id)
    await as(ANA, () => createChallenge(WORDS.L1[0].id))

    const r = await as(ANA, () => getChallengeableWords())
    if (!r.ok) throw new Error(r.error)
    expect(r.words.map((w) => w.wordId).sort()).toEqual([WORDS.L1[0].id, WORDS.L1[1].id])
    expect(r.words.find((w) => w.wordId === WORDS.L1[0].id)).toEqual({
      wordId: WORDS.L1[0].id, word: 'MUDANZA', length: 7, timeTaken: 42, challenged: true,
    })
    expect(r.words.find((w) => w.wordId === WORDS.L1[1].id)?.challenged).toBe(false)
    expect(JSON.stringify(r)).not.toMatch(/userId|"id"/)
  })

  it('exige Google y alias', async () => {
    expect(await getChallengeableWords()).toEqual({ ok: false, error: 'login_required' })
    await createRegisteredUser('sin@example.com')
    expect(await as('sin@example.com', () => getChallengeableWords())).toEqual({ ok: false, error: 'username_required' })
  })
})
