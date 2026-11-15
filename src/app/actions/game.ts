'use server'

import prisma from '@/lib/prisma'
import type { Prisma } from '@prisma/client'
import { ensureCurrentUser, getCurrentUser } from '@/lib/identity'
import { isCorrectGuess, isValidGuessFormat, toPublicWord, type PublicWord, type RevealedLetter } from '@/lib/public-word'

// Server Actions del juego. Contrato: specs/001-hardening/contracts/server-actions.md
// Reglas (constitución): el servidor decide todo lo que tiene valor; nunca se devuelve
// User.id ni la solución de una palabra no resuelta.

type Db = typeof prisma | Prisma.TransactionClient
type User = NonNullable<Awaited<ReturnType<typeof getCurrentUser>>>

const SKIP_COST = 15
const REWARD_NEURONAS = 10
const MAX_LETTER_HINTS = 2

export type LevelStats = { solved: number; total: number; levelId: number }

class ActionError extends Error {}

// ---------------------------------------------------------------------------
// Helpers internos (no exportados: no son Server Actions)
// ---------------------------------------------------------------------------

async function getLetterCost(): Promise<number> {
  const setting = await prisma.setting.findUnique({ where: { id: 'costo_pista' } })
  const parsed = parseInt(setting?.value ?? '', 10)
  return Number.isFinite(parsed) && parsed >= 0 ? parsed : 5
}

async function getLevelStats(db: Db, userId: string, levelId: number): Promise<LevelStats> {
  const [total, solved] = await Promise.all([
    db.word.count({ where: { levelId } }),
    db.progress.count({
      where: { userId, word: { levelId }, OR: [{ guessed: true }, { skipped: true }] },
    }),
  ])
  return { solved, total, levelId }
}

/** La palabra está en juego si es del nivel del jugador y no está acertada ni salteada. */
async function findWordInPlay(db: Db, user: User, wordId: number) {
  if (!Number.isInteger(wordId)) return null
  const word = await db.word.findUnique({ where: { id: wordId } })
  if (!word || word.levelId !== user.levelId) return null
  const progress = await db.progress.findUnique({ where: { userId_wordId: { userId: user.id, wordId } } })
  if (progress?.guessed || progress?.skipped) return null
  return { word, progress }
}

/** Registra la entrega de una palabra (create-only: conserva el primer startTime). */
async function registerDelivery(db: Db, userId: string, wordId: number) {
  await db.progress.createMany({
    data: [{ userId, wordId, startTime: new Date() }],
    skipDuplicates: true,
  })
}

async function publicWordFor(db: Db, userId: string, word: { id: number; word: string; description: string }): Promise<PublicWord> {
  const progress = await db.progress.findUnique({
    where: { userId_wordId: { userId, wordId: word.id } },
    select: { revealedIndices: true },
  })
  return toPublicWord(word, progress?.revealedIndices ?? [])
}

async function doneWordIds(db: Db, userId: string): Promise<number[]> {
  const rows = await db.progress.findMany({
    where: { userId, OR: [{ guessed: true }, { skipped: true }] },
    select: { wordId: true },
  })
  return rows.map((r) => r.wordId)
}

// ---------------------------------------------------------------------------
// Entrega de palabras
// ---------------------------------------------------------------------------

export async function getInitialGameState(): Promise<{
  word: PublicWord
  levelId: number
  neuronas: number
  needsUsername: boolean
} | null> {
  const user = await getCurrentUser()

  if (!user) {
    const firstLevel = await prisma.level.findFirst({ orderBy: { id: 'asc' } })
    if (!firstLevel) return null
    const firstWord = await prisma.word.findFirst({ where: { levelId: firstLevel.id }, orderBy: { id: 'asc' } })
    if (!firstWord) return null
    return { word: toPublicWord(firstWord), levelId: firstLevel.id, neuronas: 50, needsUsername: false }
  }

  const done = new Set(await doneWordIds(prisma, user.id))
  let levelId = user.levelId
  let words = await prisma.word.findMany({ where: { levelId }, orderBy: { id: 'asc' } })
  let next = words.find((w) => !done.has(w.id))

  // Nivel completo al reingresar: avanzar al siguiente nivel con palabras.
  if (!next) {
    const nextLevel = await prisma.level.findFirst({ where: { id: { gt: levelId } }, orderBy: { id: 'asc' } })
    if (!nextLevel) return null // todos los niveles completos
    levelId = nextLevel.id
    await prisma.user.update({ where: { id: user.id }, data: { levelId } })
    words = await prisma.word.findMany({ where: { levelId }, orderBy: { id: 'asc' } })
    next = words.find((w) => !done.has(w.id))
    if (!next) return null
  }

  await registerDelivery(prisma, user.id, next.id)
  return {
    word: await publicWordFor(prisma, user.id, next),
    levelId,
    neuronas: user.neuronas,
    needsUsername: !user.username,
  }
}

/** Lo llama el GameEngine al montar: crea el invitado/cookie si hace falta y registra la entrega. */
export async function startWord(wordId: number): Promise<{ ok: true } | { ok: false; error: 'not_in_play' }> {
  const user = await ensureCurrentUser()
  const inPlay = await findWordInPlay(prisma, user, wordId)
  if (!inPlay) return { ok: false, error: 'not_in_play' }
  await registerDelivery(prisma, user.id, wordId)
  return { ok: true }
}

/** Palabra al azar en juego del nivel del jugador. `levelId` se ignora si no es el del jugador. */
export async function getNextWord(_levelId?: number): Promise<PublicWord | null> {
  const user = await ensureCurrentUser()
  const done = await doneWordIds(prisma, user.id)
  const available = await prisma.word.findMany({
    where: { levelId: user.levelId, id: { notIn: done } },
  })
  if (available.length === 0) return null

  const word = available[Math.floor(Math.random() * available.length)]
  await registerDelivery(prisma, user.id, word.id)
  return publicWordFor(prisma, user.id, word)
}

export async function moveToNextLevel(): Promise<
  | { success: true; nextLevelId: number; nextWord: PublicWord | null; neuronas: number }
  | { success: false; error: string }
> {
  const user = await ensureCurrentUser()

  const stats = await getLevelStats(prisma, user.id, user.levelId)
  if (stats.solved < stats.total) return { success: false, error: 'El nivel actual no está completo.' }

  const nextLevel = await prisma.level.findFirst({ where: { id: { gt: user.levelId } }, orderBy: { id: 'asc' } })
  if (!nextLevel) return { success: false, error: 'No hay más niveles.' }

  const updated = await prisma.user.update({ where: { id: user.id }, data: { levelId: nextLevel.id } })

  const done = await doneWordIds(prisma, user.id)
  const word = await prisma.word.findFirst({
    where: { levelId: nextLevel.id, id: { notIn: done } },
    orderBy: { id: 'asc' },
  })
  if (word) await registerDelivery(prisma, user.id, word.id)

  return {
    success: true,
    nextLevelId: nextLevel.id,
    nextWord: word ? await publicWordFor(prisma, user.id, word) : null,
    neuronas: updated.neuronas,
  }
}

// ---------------------------------------------------------------------------
// Validación del intento y acreditación (S-2, S-3, S-4)
// ---------------------------------------------------------------------------

export type SubmitGuessResult =
  | { correct: false }
  | {
      correct: true
      solution: string
      alreadySolved: boolean
      neuronas: number
      points: number
      isLevelComplete: boolean
      levelStats: LevelStats
    }
  | { error: 'not_in_play' | 'invalid_guess' }

export async function submitGuess(wordId: number, guess: string): Promise<SubmitGuessResult> {
  const user = await ensureCurrentUser()

  const word = Number.isInteger(wordId) ? await prisma.word.findUnique({ where: { id: wordId } }) : null
  if (!word || word.levelId !== user.levelId) return { error: 'not_in_play' }

  const progress = await prisma.progress.findUnique({ where: { userId_wordId: { userId: user.id, wordId } } })
  if (progress?.skipped && !progress.guessed) return { error: 'not_in_play' }

  if (!isValidGuessFormat(guess, word.word.length)) return { error: 'invalid_guess' }
  if (!isCorrectGuess(guess, word.word)) return { correct: false }

  const result = await prisma.$transaction(async (tx) => {
    await registerDelivery(tx, user.id, wordId)

    // Transición guessed=false → true una sola vez; el tiempo lo mide el servidor.
    // startTime/endTime son timestamp sin zona escritos en UTC por Prisma.
    const rows = await tx.$queryRaw<{ timeTaken: number }[]>`
      UPDATE "Progress"
      SET guessed = true,
          "endTime" = (now() AT TIME ZONE 'UTC'),
          "timeTaken" = GREATEST(0, EXTRACT(EPOCH FROM ((now() AT TIME ZONE 'UTC') - "startTime")))::float8
      WHERE "userId" = ${user.id} AND "wordId" = ${wordId} AND guessed = false AND skipped = false
      RETURNING "timeTaken"`

    if (rows.length === 0) {
      const current = await tx.user.findUniqueOrThrow({ where: { id: user.id } })
      return { credited: false, user: current }
    }

    const updated = await tx.user.update({
      where: { id: user.id },
      data: {
        neuronas: { increment: REWARD_NEURONAS },
        points: { increment: 1 },
        totalTime: { increment: Number(rows[0].timeTaken) },
      },
    })
    return { credited: true, user: updated }
  })

  const stats = await getLevelStats(prisma, user.id, word.levelId)
  return {
    correct: true,
    solution: word.word.toUpperCase(),
    alreadySolved: !result.credited,
    neuronas: result.user.neuronas,
    points: result.user.points,
    isLevelComplete: stats.solved >= stats.total,
    levelStats: stats,
  }
}

// ---------------------------------------------------------------------------
// Pistas y saltos (S-5, FR-012, FR-013)
// ---------------------------------------------------------------------------

export type UseHintResult =
  | { success: true; remainingNeuronas: number; revealed: RevealedLetter }
  | { success: true; remainingNeuronas: number; isLevelComplete: boolean; levelStats?: LevelStats }
  | { success: false; error: string }

const NOT_ENOUGH = 'Not enough neuronas'
const NOT_IN_PLAY = 'La palabra ya no está en juego.'

async function charge(tx: Prisma.TransactionClient, userId: string, cost: number) {
  const charged = await tx.user.updateMany({
    where: { id: userId, neuronas: { gte: cost } },
    data: { neuronas: { decrement: cost } },
  })
  if (charged.count === 0) throw new ActionError(NOT_ENOUGH)
  const u = await tx.user.findUniqueOrThrow({ where: { id: userId }, select: { neuronas: true } })
  return u.neuronas
}

export async function useHint(wordId: number, type: 'letter' | 'skip'): Promise<UseHintResult> {
  const user = await ensureCurrentUser()
  if (type !== 'letter' && type !== 'skip') return { success: false, error: 'Tipo de pista inválido.' }

  const inPlay = await findWordInPlay(prisma, user, wordId)
  if (!inPlay) return { success: false, error: NOT_IN_PLAY }

  const cost = type === 'letter' ? await getLetterCost() : SKIP_COST
  if (user.neuronas < cost) return { success: false, error: NOT_ENOUGH }

  try {
    if (type === 'letter') {
      const current = inPlay.progress?.revealedIndices ?? []
      if (current.length >= MAX_LETTER_HINTS) {
        return { success: false, error: 'Has alcanzado el límite de 2 pistas para esta palabra.' }
      }
      const available = Array.from({ length: inPlay.word.word.length }, (_, i) => i).filter((i) => !current.includes(i))
      if (available.length === 0) return { success: false, error: 'No hay más letras para revelar.' }

      const index = available[Math.floor(Math.random() * available.length)]
      const next = [...current, index]

      const remaining = await prisma.$transaction(async (tx) => {
        await registerDelivery(tx, user.id, wordId)
        // Optimista: solo aplica si nadie cambió las letras reveladas desde que las leímos.
        const updated = await tx.$executeRaw`
          UPDATE "Progress" SET "revealedIndices" = ${next}::int[]
          WHERE "userId" = ${user.id} AND "wordId" = ${wordId}
            AND guessed = false AND skipped = false
            AND COALESCE("revealedIndices", '{}'::int[]) = ${current}::int[]`
        if (updated === 0) throw new ActionError('Otra pista se procesó al mismo tiempo. Probá de nuevo.')
        return charge(tx, user.id, cost)
      })

      return {
        success: true,
        remainingNeuronas: remaining,
        revealed: { index, letter: inPlay.word.word[index].toUpperCase() },
      }
    }

    // skip
    const remaining = await prisma.$transaction(async (tx) => {
      await registerDelivery(tx, user.id, wordId)
      const skipped = await tx.progress.updateMany({
        where: { userId: user.id, wordId, guessed: false, skipped: false },
        data: { skipped: true },
      })
      if (skipped.count === 0) throw new ActionError(NOT_IN_PLAY)
      return charge(tx, user.id, cost)
    })

    const stats = await getLevelStats(prisma, user.id, inPlay.word.levelId)
    return {
      success: true,
      remainingNeuronas: remaining,
      isLevelComplete: stats.solved >= stats.total,
      levelStats: stats,
    }
  } catch (error) {
    if (error instanceof ActionError) return { success: false, error: error.message }
    throw error
  }
}

// ---------------------------------------------------------------------------
// Rankings (sin User.id: FR-005)
// ---------------------------------------------------------------------------

export type RankingEntry = {
  name: string | null
  username: string | null
  image: string | null
  points: number
  totalTime: number
  neuronas?: number
}

export async function getGlobalRanking(limit: number = 10): Promise<RankingEntry[]> {
  return prisma.user.findMany({
    where: { email: { not: null } },
    orderBy: [{ points: 'desc' }, { totalTime: 'asc' }, { neuronas: 'desc' }],
    take: Math.min(Math.max(1, limit), 50),
    select: { name: true, username: true, image: true, points: true, totalTime: true, neuronas: true },
  })
}

export async function getLevelRanking(levelId: number, limit: number = 10) {
  const usersWithProgress = await prisma.user.findMany({
    where: { email: { not: null }, progress: { some: { word: { levelId } } } },
    include: { progress: { where: { word: { levelId }, guessed: true } } },
    take: limit,
  })

  return usersWithProgress
    .map((user) => ({
      name: user.name,
      username: user.username,
      image: user.image,
      levelPoints: user.progress.length,
      levelTime: user.progress.reduce((acc, p) => acc + (p.timeTaken || 0), 0),
    }))
    .sort((a, b) => b.levelPoints - a.levelPoints || a.levelTime - b.levelTime)
    .slice(0, limit)
}

// ---------------------------------------------------------------------------
// Configuración, mensajes y username (sin cambios funcionales)
// ---------------------------------------------------------------------------

export async function getGameSettings() {
  const settings = await prisma.setting.findMany()
  return Object.fromEntries(settings.map((s) => [s.id, s.value]))
}

export async function getRandomMessage(type: 'success' | 'fail') {
  const messages = await prisma.gameMessage.findMany({ where: { type } })
  if (messages.length === 0) return type === 'success' ? '¡Correcto!' : '¡Error!'
  return messages[Math.floor(Math.random() * messages.length)].content
}

export async function getLevels() {
  return prisma.level.findMany({ orderBy: { id: 'asc' } })
}

export async function checkUsername(username: string) {
  const existingUser = await prisma.user.findUnique({ where: { username } })
  return { available: !existingUser }
}

export async function updateUsername(username: string) {
  const user = await getCurrentUser()
  if (!user) return { success: false, error: 'Unauthorized' }

  try {
    await prisma.user.update({ where: { id: user.id }, data: { username } })
    return { success: true }
  } catch {
    return { success: false, error: 'Error updating username' }
  }
}
