'use server'

import prisma from '@/lib/prisma'
import { getCurrentUser } from '@/lib/identity'
import { requireSocialUser, socialErrorFor, type SocialError } from '@/lib/social-auth'
import { generateChallengeCode, isChallengeCode } from '@/lib/challenge-code'
import { buildLetterPool, isCorrectGuess, isValidGuessFormat } from '@/lib/public-word'
import { CHALLENGE_DAILY_LIMIT } from '@/lib/social-rules'

// Desafíos entre amigos (spec 002-social US4). Contrato: specs/002-social/contracts/social-actions.md
// Reglas: el tiempo lo mide Postgres desde que se crea el intento; un intento por jugador;
// estas actions NUNCA escriben User ni Progress (FR-028).

export type PlayerRef = { username: string; image: string | null }

export type ChallengeResult = PlayerRef & {
  solved: boolean
  gaveUp: boolean
  knewWord: boolean
  timeTaken: number | null
  won: boolean
}

export type ChallengeViewer = 'anonymous' | 'username_required' | 'creator' | 'can_play' | 'in_progress' | 'finished'

export type ChallengeView = {
  code: string
  creator: PlayerRef
  length: number
  creatorTime: number
  creatorHints: number
  results: ChallengeResult[]
  viewer: ChallengeViewer
  myResult: ChallengeResult | null
  solution: string | null
}

type AttemptRow = {
  solved: boolean
  gaveUp: boolean
  knewWord: boolean
  timeTaken: number | null
  user: { username: string | null; image: string | null }
}

function toResult(a: AttemptRow, creatorTime: number): ChallengeResult {
  return {
    username: a.user.username ?? 'anónimo',
    image: a.user.image,
    solved: a.solved,
    gaveUp: a.gaveUp,
    knewWord: a.knewWord,
    timeTaken: a.timeTaken,
    won: a.solved && !a.knewWord && a.timeTaken !== null && a.timeTaken < creatorTime,
  }
}

/** Aciertos primero, por menor tiempo; después los que se rindieron (FR-029). */
function sortResults(results: ChallengeResult[]) {
  return [...results].sort((a, b) => {
    if (a.solved !== b.solved) return a.solved ? -1 : 1
    return (a.timeTaken ?? Infinity) - (b.timeTaken ?? Infinity)
  })
}

async function findChallenge(code: unknown) {
  if (!isChallengeCode(code)) return null
  return prisma.challenge.findUnique({
    where: { code },
    include: { word: { select: { word: true, description: true } }, creator: { select: { username: true, image: true } } },
  })
}

// ---------------------------------------------------------------------------

export async function createChallenge(wordId: number): Promise<{ ok: true; code: string; username: string } | { ok: false; error: SocialError | 'not_solved' | 'daily_limit' }> {
  const { user, error } = await requireSocialUser()
  if (error) return { ok: false, error }
  if (!Number.isInteger(wordId)) return { ok: false, error: 'not_solved' }

  const progress = await prisma.progress.findUnique({ where: { userId_wordId: { userId: user.id, wordId } } })
  if (!progress?.guessed) return { ok: false, error: 'not_solved' }

  return prisma.$transaction(async (tx) => {
    await tx.$executeRaw`SELECT pg_advisory_xact_lock(hashtext(${'challenge:' + user.id}))`

    const existing = await tx.challenge.findUnique({ where: { creatorId_wordId: { creatorId: user.id, wordId } }, select: { code: true } })
    if (existing) return { ok: true as const, code: existing.code, username: user.username }

    const today = await tx.challenge.count({ where: { creatorId: user.id, createdAt: { gt: new Date(Date.now() - 24 * 3600 * 1000) } } })
    if (today >= CHALLENGE_DAILY_LIMIT) return { ok: false as const, error: 'daily_limit' as const }

    for (let attempt = 0; attempt < 3; attempt++) {
      const code = generateChallengeCode()
      // ON CONFLICT DO NOTHING: una colisión de código no aborta la transacción.
      const created = await tx.challenge.createMany({
        data: [{
          code,
          creatorId: user.id,
          wordId,
          creatorTime: progress.timeTaken ?? 0,
          creatorHints: progress.revealedIndices?.length ?? 0,
          createdAt: new Date(),
        }],
        skipDuplicates: true,
      })
      if (created.count === 1) return { ok: true as const, code, username: user.username }
    }
    throw new Error('No se pudo generar un código de desafío único')
  })
}

export async function getChallenge(code: string): Promise<ChallengeView | null> {
  const challenge = await findChallenge(code)
  if (!challenge) return null

  const user = await getCurrentUser()
  const attempts = await prisma.challengeAttempt.findMany({
    where: { challengeId: challenge.id },
    select: { userId: true, endTime: true, solved: true, gaveUp: true, knewWord: true, timeTaken: true, user: { select: { username: true, image: true } } },
  })

  const mine = user ? attempts.find((a) => a.userId === user.id) ?? null : null
  let viewer: ChallengeViewer
  const blocked = socialErrorFor(user)
  if (blocked === 'login_required') viewer = 'anonymous'
  else if (user && user.id === challenge.creatorId) viewer = 'creator'
  else if (mine?.endTime) viewer = 'finished'
  else if (mine) viewer = 'in_progress'
  else if (blocked === 'username_required') viewer = 'username_required'
  else viewer = 'can_play'

  const results = sortResults(attempts.filter((a) => a.endTime !== null).map((a) => toResult(a, challenge.creatorTime)))

  return {
    code: challenge.code,
    creator: { username: challenge.creator.username ?? 'anónimo', image: challenge.creator.image },
    length: challenge.word.word.length,
    creatorTime: challenge.creatorTime,
    creatorHints: challenge.creatorHints,
    results,
    viewer,
    myResult: mine?.endTime ? toResult(mine, challenge.creatorTime) : null,
    solution: viewer === 'finished' ? challenge.word.word.toUpperCase() : null,
  }
}

export type StartChallengeResult =
  | { ok: true; word: { description: string; length: number; pool: string[] }; elapsed: number }
  | { ok: false; error: SocialError | 'not_found' | 'own_challenge' | 'finished' }

export async function startChallenge(code: string): Promise<StartChallengeResult> {
  const { user, error } = await requireSocialUser()
  if (error) return { ok: false, error }
  const challenge = await findChallenge(code)
  if (!challenge) return { ok: false, error: 'not_found' }
  if (challenge.creatorId === user.id) return { ok: false, error: 'own_challenge' }

  // "Ya la conocía" se fija al crear el intento: si la había acertado en el juego normal (FR-027).
  const known = await prisma.progress.findUnique({
    where: { userId_wordId: { userId: user.id, wordId: challenge.wordId } },
    select: { guessed: true },
  })
  // Create-only: abrirlo en otra pestaña o volver más tarde no reinicia el tiempo (FR-024).
  await prisma.challengeAttempt.createMany({
    data: [{ challengeId: challenge.id, userId: user.id, startTime: new Date(), knewWord: known?.guessed === true }],
    skipDuplicates: true,
  })

  const rows = await prisma.$queryRaw<{ endTime: Date | null; elapsed: number }[]>`
    SELECT "endTime", GREATEST(0, EXTRACT(EPOCH FROM ((now() AT TIME ZONE 'UTC') - "startTime")))::float8 AS elapsed
    FROM "ChallengeAttempt" WHERE "challengeId" = ${challenge.id} AND "userId" = ${user.id}`
  if (rows[0]?.endTime) return { ok: false, error: 'finished' }

  return {
    ok: true,
    word: {
      description: challenge.word.description,
      length: challenge.word.word.length,
      pool: buildLetterPool(challenge.word.word),
    },
    elapsed: Number(rows[0]?.elapsed ?? 0),
  }
}

export type SubmitChallengeGuessResult =
  | { correct: false }
  | { correct: true; solution: string; timeTaken: number; creatorTime: number; won: boolean; knewWord: boolean }
  | { error: SocialError | 'not_found' | 'not_started' | 'finished' | 'invalid_guess' }

export async function submitChallengeGuess(code: string, guess: string): Promise<SubmitChallengeGuessResult> {
  const { user, error } = await requireSocialUser()
  if (error) return { error }
  const challenge = await findChallenge(code)
  if (!challenge) return { error: 'not_found' }

  const attempt = await prisma.challengeAttempt.findUnique({
    where: { challengeId_userId: { challengeId: challenge.id, userId: user.id } },
    select: { endTime: true },
  })
  if (!attempt) return { error: 'not_started' }
  if (attempt.endTime) return { error: 'finished' }

  if (!isValidGuessFormat(guess, challenge.word.word.length)) return { error: 'invalid_guess' }
  if (!isCorrectGuess(guess, challenge.word.word)) return { correct: false }

  // Cierre condicional: solo el primer acierto fija el tiempo (idempotente ante pedidos repetidos).
  const rows = await prisma.$queryRaw<{ timeTaken: number; knewWord: boolean }[]>`
    UPDATE "ChallengeAttempt"
    SET solved = true,
        "endTime" = (now() AT TIME ZONE 'UTC'),
        "timeTaken" = GREATEST(0, EXTRACT(EPOCH FROM ((now() AT TIME ZONE 'UTC') - "startTime")))::float8
    WHERE "challengeId" = ${challenge.id} AND "userId" = ${user.id} AND "endTime" IS NULL
    RETURNING "timeTaken", "knewWord"`
  if (rows.length === 0) return { error: 'finished' }

  const timeTaken = Number(rows[0].timeTaken)
  const knewWord = rows[0].knewWord
  return {
    correct: true,
    solution: challenge.word.word.toUpperCase(),
    timeTaken,
    creatorTime: challenge.creatorTime,
    knewWord,
    won: !knewWord && timeTaken < challenge.creatorTime,
  }
}

export async function giveUpChallenge(code: string): Promise<{ ok: true; solution: string } | { ok: false; error: SocialError | 'not_found' | 'not_started' | 'finished' }> {
  const { user, error } = await requireSocialUser()
  if (error) return { ok: false, error }
  const challenge = await findChallenge(code)
  if (!challenge) return { ok: false, error: 'not_found' }

  const updated = await prisma.$executeRaw`
    UPDATE "ChallengeAttempt" SET "gaveUp" = true, "endTime" = (now() AT TIME ZONE 'UTC')
    WHERE "challengeId" = ${challenge.id} AND "userId" = ${user.id} AND "endTime" IS NULL`
  if (updated === 0) {
    const exists = await prisma.challengeAttempt.findUnique({
      where: { challengeId_userId: { challengeId: challenge.id, userId: user.id } },
      select: { id: true },
    })
    return { ok: false, error: exists ? 'finished' : 'not_started' }
  }
  return { ok: true, solution: challenge.word.word.toUpperCase() }
}

export type MyChallenges = {
  created: { code: string; length: number; creatorTime: number; participants: number; createdAt: string }[]
  played: { code: string; creator: PlayerRef; result: ChallengeResult; playedAt: string }[]
}

export async function getMyChallenges(): Promise<({ ok: true } & MyChallenges) | { ok: false; error: SocialError }> {
  const { user, error } = await requireSocialUser()
  if (error) return { ok: false, error }

  const [created, played] = await Promise.all([
    prisma.challenge.findMany({
      where: { creatorId: user.id },
      orderBy: { createdAt: 'desc' },
      take: 50,
      select: {
        code: true,
        creatorTime: true,
        createdAt: true,
        word: { select: { word: true } },
        _count: { select: { attempts: { where: { endTime: { not: null } } } } },
      },
    }),
    prisma.challengeAttempt.findMany({
      where: { userId: user.id, endTime: { not: null } },
      orderBy: { startTime: 'desc' },
      take: 50,
      select: {
        startTime: true,
        solved: true,
        gaveUp: true,
        knewWord: true,
        timeTaken: true,
        user: { select: { username: true, image: true } },
        challenge: { select: { code: true, creatorTime: true, creator: { select: { username: true, image: true } } } },
      },
    }),
  ])

  return {
    ok: true,
    created: created.map((c) => ({
      code: c.code,
      length: c.word.word.length,
      creatorTime: c.creatorTime,
      participants: c._count.attempts,
      createdAt: c.createdAt.toISOString(),
    })),
    played: played.map((a) => ({
      code: a.challenge.code,
      creator: { username: a.challenge.creator.username ?? 'anónimo', image: a.challenge.creator.image },
      result: toResult(a, a.challenge.creatorTime),
      playedAt: a.startTime.toISOString(),
    })),
  }
}
