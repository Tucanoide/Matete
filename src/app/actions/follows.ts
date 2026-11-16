'use server'

import prisma from '@/lib/prisma'
import { requireSocialUser, type SocialError } from '@/lib/social-auth'
import { FOLLOW_LIMIT, PLAYER_SEARCH_MAX_RESULTS, PLAYER_SEARCH_MIN } from '@/lib/social-rules'

// Seguir jugadores y ranking de amigos (spec 002-social US3). Se identifica a otros
// jugadores por username; nunca se recibe ni se devuelve User.id (FR-019).

export type PlayerRef = { username: string; image: string | null }

/** Jugador seguible: registrado con Google y con username. */
async function findFollowable(username: unknown) {
  if (typeof username !== 'string' || !username.trim()) return null
  return prisma.user.findFirst({
    where: { username: { equals: username.trim(), mode: 'insensitive' }, email: { not: null } },
    select: { id: true },
  })
}

export async function followPlayer(username: string): Promise<{ ok: true } | { ok: false; error: SocialError | 'not_found' | 'self' | 'limit' }> {
  const { user, error } = await requireSocialUser()
  if (error) return { ok: false, error }

  const target = await findFollowable(username)
  if (!target) return { ok: false, error: 'not_found' }
  if (target.id === user.id) return { ok: false, error: 'self' }

  return prisma.$transaction(async (tx) => {
    await tx.$executeRaw`SELECT pg_advisory_xact_lock(hashtext(${'follow:' + user.id}))`
    const already = await tx.follow.findUnique({
      where: { followerId_followeeId: { followerId: user.id, followeeId: target.id } },
      select: { followerId: true },
    })
    if (already) return { ok: true as const }
    const count = await tx.follow.count({ where: { followerId: user.id } })
    if (count >= FOLLOW_LIMIT) return { ok: false as const, error: 'limit' as const }
    await tx.follow.create({ data: { followerId: user.id, followeeId: target.id } })
    return { ok: true as const }
  })
}

export async function unfollowPlayer(username: string): Promise<{ ok: true } | { ok: false; error: SocialError }> {
  const { user, error } = await requireSocialUser()
  if (error) return { ok: false, error }
  const target = await findFollowable(username)
  if (target) await prisma.follow.deleteMany({ where: { followerId: user.id, followeeId: target.id } })
  return { ok: true }
}

function escapeLike(value: string) {
  return value.replace(/[\\%_]/g, (c) => `\\${c}`)
}

export async function searchPlayers(
  prefix: string
): Promise<{ ok: true; players: (PlayerRef & { isFollowed: boolean })[] } | { ok: false; error: SocialError | 'too_short' }> {
  const { user, error } = await requireSocialUser()
  if (error) return { ok: false, error }
  const q = typeof prefix === 'string' ? prefix.trim().replace(/^@/, '') : ''
  if (q.length < PLAYER_SEARCH_MIN) return { ok: false, error: 'too_short' }

  const rows = await prisma.$queryRaw<{ username: string; image: string | null; isFollowed: boolean }[]>`
    SELECT u.username, u.image,
           EXISTS (SELECT 1 FROM "Follow" f WHERE f."followerId" = ${user.id} AND f."followeeId" = u.id) AS "isFollowed"
    FROM "User" u
    WHERE u.email IS NOT NULL AND u.username IS NOT NULL AND u.id <> ${user.id}
      AND u.username ILIKE ${escapeLike(q.slice(0, 15)) + '%'}
    ORDER BY lower(u.username)
    LIMIT ${PLAYER_SEARCH_MAX_RESULTS}`
  return { ok: true, players: rows }
}

export type FriendsRankingEntry = PlayerRef & { points: number; totalTime: number; isMe: boolean }

export async function getFriendsRanking(): Promise<{ ok: true; entries: FriendsRankingEntry[] } | { ok: false; error: SocialError }> {
  const { user, error } = await requireSocialUser()
  if (error) return { ok: false, error }

  const rows = await prisma.user.findMany({
    where: { OR: [{ id: user.id }, { followers: { some: { followerId: user.id } } }] },
    orderBy: [{ points: 'desc' }, { totalTime: 'asc' }, { username: 'asc' }],
    take: FOLLOW_LIMIT + 1,
    select: { id: true, username: true, image: true, points: true, totalTime: true },
  })
  return {
    ok: true,
    entries: rows.map((r) => ({
      username: r.username ?? 'anónimo',
      image: r.image,
      points: r.points,
      totalTime: r.totalTime,
      isMe: r.id === user.id,
    })),
  }
}
