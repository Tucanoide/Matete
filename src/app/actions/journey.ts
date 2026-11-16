'use server'

import prisma from '@/lib/prisma'
import { getCurrentUser } from '@/lib/identity'

// Mi recorrido (specs/003-recorrido). Solo lectura y solo agregados: nunca devuelve
// palabras, definiciones ni User.id (constitución 1 y 2). No crea invitados.

export type JourneyLevel = {
  levelId: number
  name: string
  totalWords: number
  guessed: number
  skipped: number
  avgTime: number | null
  hints: number
}

export type Journey = {
  isGuest: boolean
  username: string | null
  levelId: number
  points: number
  neuronas: number
  /** Posición en el ranking global (solo usuarios de Google). */
  rank: number | null
  guessed: number
  skipped: number
  inProgress: number
  /** Suma de los tiempos de las acertadas (desde Progress). */
  totalTime: number
  /** Promedio y mejor tiempo de las acertadas con tiempo registrado (> 0). */
  avgTime: number | null
  bestTime: number | null
  hintsUsed: number
  solvedWithoutHints: number
  levels: JourneyLevel[]
  /** Solo usuarios de Google con alias. */
  challenges: { created: number; played: number; won: number } | null
}

type LevelRow = {
  levelId: number
  name: string
  totalWords: number
  guessed: number
  skipped: number
  inProgress: number
  timeSum: number
  avgTime: number | null
  bestTime: number | null
  hints: number
  noHints: number
}

export async function getMyJourney(): Promise<{ ok: true; journey: Journey } | { ok: false; error: 'no_player' }> {
  const user = await getCurrentUser()
  if (!user) return { ok: false, error: 'no_player' }

  const isGuest = !user.email

  // Una fila por nivel con progreso, más el nivel actual aunque no tenga.
  const rows = await prisma.$queryRaw<LevelRow[]>`
    WITH p AS (
      SELECT w."levelId", p.guessed, p.skipped, p."timeTaken",
             COALESCE(cardinality(p."revealedIndices"), 0) AS hints
      FROM "Progress" p JOIN "Word" w ON w.id = p."wordId"
      WHERE p."userId" = ${user.id}
    ),
    lv AS (
      SELECT DISTINCT "levelId" FROM p
      UNION SELECT ${user.levelId}::int
    )
    SELECT l.id AS "levelId", l.name,
           (SELECT count(*) FROM "Word" w WHERE w."levelId" = l.id)::int AS "totalWords",
           count(p.*) FILTER (WHERE p.guessed)::int AS guessed,
           count(p.*) FILTER (WHERE p.skipped AND NOT p.guessed)::int AS skipped,
           count(p.*) FILTER (WHERE NOT p.guessed AND NOT p.skipped)::int AS "inProgress",
           COALESCE(sum(p."timeTaken") FILTER (WHERE p.guessed AND p."timeTaken" > 0), 0)::float8 AS "timeSum",
           avg(p."timeTaken") FILTER (WHERE p.guessed AND p."timeTaken" > 0)::float8 AS "avgTime",
           min(p."timeTaken") FILTER (WHERE p.guessed AND p."timeTaken" > 0)::float8 AS "bestTime",
           COALESCE(sum(p.hints), 0)::int AS hints,
           count(p.*) FILTER (WHERE p.guessed AND p.hints = 0)::int AS "noHints"
    FROM lv JOIN "Level" l ON l.id = lv."levelId"
    LEFT JOIN p ON p."levelId" = l.id
    GROUP BY l.id, l.name
    ORDER BY l.id`

  const sum = (f: (r: LevelRow) => number) => rows.reduce((n, r) => n + f(r), 0)
  const timed = await prisma.progress.aggregate({
    where: { userId: user.id, guessed: true, timeTaken: { gt: 0 } },
    _avg: { timeTaken: true },
    _min: { timeTaken: true },
  })

  const [rank, challenges] = await Promise.all([
    isGuest ? Promise.resolve(null) : rankOf(user),
    !isGuest && user.username ? challengeStats(user.id) : Promise.resolve(null),
  ])

  return {
    ok: true,
    journey: {
      isGuest,
      username: isGuest ? null : user.username,
      levelId: user.levelId,
      points: user.points,
      neuronas: user.neuronas,
      rank,
      guessed: sum((r) => r.guessed),
      skipped: sum((r) => r.skipped),
      inProgress: sum((r) => r.inProgress),
      totalTime: sum((r) => r.timeSum),
      avgTime: timed._avg.timeTaken ?? null,
      bestTime: timed._min.timeTaken ?? null,
      hintsUsed: sum((r) => r.hints),
      solvedWithoutHints: sum((r) => r.noHints),
      levels: rows.map((r) => ({
        levelId: r.levelId,
        name: r.name,
        totalWords: r.totalWords,
        guessed: r.guessed,
        skipped: r.skipped,
        avgTime: r.avgTime,
        hints: r.hints,
      })),
      challenges,
    },
  }
}

/** 1 + usuarios de Google que van adelante con el orden de getGlobalRanking. */
async function rankOf(user: { id: string; points: number; totalTime: number; neuronas: number }): Promise<number> {
  const [row] = await prisma.$queryRaw<{ ahead: number }[]>`
    SELECT count(*)::int AS ahead FROM "User"
    WHERE email IS NOT NULL AND id <> ${user.id} AND (
      points > ${user.points}
      OR (points = ${user.points} AND "totalTime" < ${user.totalTime})
      OR (points = ${user.points} AND "totalTime" = ${user.totalTime} AND neuronas > ${user.neuronas})
    )`
  return row.ahead + 1
}

/** Misma regla de victoria que challenges.ts: acertó, no la conocía y tardó menos que el creador. */
async function challengeStats(userId: string) {
  const [row] = await prisma.$queryRaw<{ created: number; played: number; won: number }[]>`
    SELECT
      (SELECT count(*) FROM "Challenge" WHERE "creatorId" = ${userId})::int AS created,
      (SELECT count(*) FROM "ChallengeAttempt" WHERE "userId" = ${userId} AND "endTime" IS NOT NULL)::int AS played,
      (SELECT count(*) FROM "ChallengeAttempt" a JOIN "Challenge" c ON c.id = a."challengeId"
        WHERE a."userId" = ${userId} AND a."endTime" IS NOT NULL
          AND a.solved AND NOT a."knewWord" AND a."timeTaken" < c."creatorTime")::int AS won`
  return row
}
