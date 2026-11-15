'use server'

import prisma from '@/lib/prisma'
import { getCurrentUser } from '@/lib/identity'
import { requireSocialUser, socialErrorFor, type SocialError } from '@/lib/social-auth'
import { normalizeCommentBody } from '@/lib/comment-text'
import {
  COMMENTS_PAGE_SIZE,
  COMMENT_DAILY_LIMIT,
  COMMENT_MIN_INTERVAL_SECONDS,
  COMMENT_REPORTS_TO_HIDE,
} from '@/lib/social-rules'

// Comentarios por palabra (spec 002-social US2). Contrato: specs/002-social/contracts/social-actions.md
// Regla central (FR-005): solo quien acertó la palabra ve sus comentarios, ni siquiera la cantidad.

export type PlayerRef = { username: string; image: string | null }

export type CommentView = {
  id: string
  body: string
  createdAt: string
  author: PlayerRef
  mine: boolean
  reportedByMe: boolean
}

type CommentRow = {
  id: string
  body: string
  createdAt: Date
  authorId: string
  author: { username: string | null; image: string | null }
  reports?: { reporterId: string }[]
}

function toView(row: CommentRow, viewerId: string | null): CommentView {
  return {
    id: row.id,
    body: row.body,
    createdAt: row.createdAt.toISOString(),
    author: { username: row.author.username ?? 'anónimo', image: row.author.image },
    mine: viewerId !== null && row.authorId === viewerId,
    reportedByMe: (row.reports?.length ?? 0) > 0,
  }
}

async function hasSolved(userId: string, wordId: number): Promise<boolean> {
  if (!Number.isInteger(wordId)) return false
  const p = await prisma.progress.findUnique({
    where: { userId_wordId: { userId, wordId } },
    select: { guessed: true },
  })
  return p?.guessed === true
}

export type GetWordCommentsResult =
  | { locked: true }
  | {
      locked: false
      comments: CommentView[]
      nextCursor: string | null
      canPost: boolean
      postBlockedReason?: SocialError
    }

export async function getWordComments(wordId: number, cursor?: string): Promise<GetWordCommentsResult> {
  const user = await getCurrentUser()
  if (!user || !(await hasSolved(user.id, wordId))) return { locked: true }

  const rows = await prisma.comment.findMany({
    where: { wordId, status: 'visible' },
    orderBy: [{ createdAt: 'desc' }, { id: 'desc' }],
    take: COMMENTS_PAGE_SIZE + 1,
    ...(typeof cursor === 'string' && cursor ? { cursor: { id: cursor }, skip: 1 } : {}),
    select: {
      id: true,
      body: true,
      createdAt: true,
      authorId: true,
      author: { select: { username: true, image: true } },
      reports: { where: { reporterId: user.id }, select: { reporterId: true } },
    },
  })

  const page = rows.slice(0, COMMENTS_PAGE_SIZE)
  const blocked = socialErrorFor(user)
  return {
    locked: false,
    comments: page.map((r) => toView(r, user.id)),
    nextCursor: rows.length > COMMENTS_PAGE_SIZE ? page[page.length - 1].id : null,
    canPost: blocked === null,
    ...(blocked ? { postBlockedReason: blocked } : {}),
  }
}

export type PostCommentResult =
  | { ok: true; comment: CommentView }
  | { ok: false; error: SocialError | 'not_solved' | 'invalid_body' | 'too_fast' | 'daily_limit' }

export async function postComment(wordId: number, body: string): Promise<PostCommentResult> {
  const { user, error } = await requireSocialUser()
  if (error) return { ok: false, error }
  if (!(await hasSolved(user.id, wordId))) return { ok: false, error: 'not_solved' }

  const text = normalizeCommentBody(body)
  if (!text) return { ok: false, error: 'invalid_body' }

  return prisma.$transaction(async (tx) => {
    // Serializa los pedidos de este usuario: dos comentarios simultáneos no pasan juntos el límite (FR-008).
    await tx.$executeRaw`SELECT pg_advisory_xact_lock(hashtext(${'comment:' + user.id}))`

    const now = Date.now()
    const [recent, today] = await Promise.all([
      tx.comment.count({ where: { authorId: user.id, createdAt: { gt: new Date(now - COMMENT_MIN_INTERVAL_SECONDS * 1000) } } }),
      tx.comment.count({ where: { authorId: user.id, createdAt: { gt: new Date(now - 24 * 3600 * 1000) } } }),
    ])
    if (recent > 0) return { ok: false as const, error: 'too_fast' as const }
    if (today >= COMMENT_DAILY_LIMIT) return { ok: false as const, error: 'daily_limit' as const }

    const row = await tx.comment.create({
      data: { wordId, authorId: user.id, body: text, createdAt: new Date() },
      select: { id: true, body: true, createdAt: true, authorId: true, author: { select: { username: true, image: true } } },
    })
    return { ok: true as const, comment: toView(row, user.id) }
  })
}

export type ReportCommentResult =
  | { ok: true; hidden: boolean }
  | { ok: false; error: SocialError | 'not_found' | 'own_comment' | 'not_solved' }

export async function reportComment(commentId: string): Promise<ReportCommentResult> {
  const { user, error } = await requireSocialUser()
  if (error) return { ok: false, error }
  if (typeof commentId !== 'string' || !commentId) return { ok: false, error: 'not_found' }

  const comment = await prisma.comment.findUnique({ where: { id: commentId }, select: { authorId: true, wordId: true, status: true } })
  if (!comment || comment.status === 'deleted') return { ok: false, error: 'not_found' }
  if (comment.authorId === user.id) return { ok: false, error: 'own_comment' }
  if (!(await hasSolved(user.id, comment.wordId))) return { ok: false, error: 'not_solved' }

  return prisma.$transaction(async (tx) => {
    const inserted = await tx.commentReport.createMany({
      data: [{ commentId, reporterId: user.id }],
      skipDuplicates: true,
    })
    if (inserted.count === 0) {
      const current = await tx.comment.findUniqueOrThrow({ where: { id: commentId }, select: { status: true } })
      return { ok: true as const, hidden: current.status !== 'visible' }
    }
    // Suma y oculta en el mismo UPDATE: con reportes simultáneos, el que llega a 3 oculta (FR-010).
    const rows = await tx.$queryRaw<{ status: string }[]>`
      UPDATE "Comment"
      SET "reportCount" = "reportCount" + 1,
          status = CASE WHEN status = 'visible' AND "reportCount" + 1 >= ${COMMENT_REPORTS_TO_HIDE}
                        THEN 'hidden_reports' ELSE status END
      WHERE id = ${commentId}
      RETURNING status`
    return { ok: true as const, hidden: rows[0]?.status !== 'visible' }
  })
}

export async function deleteComment(commentId: string): Promise<{ ok: true } | { ok: false; error: 'login_required' | 'not_found' | 'forbidden' }> {
  const user = await getCurrentUser()
  if (!user || !user.email) return { ok: false, error: 'login_required' }
  if (typeof commentId !== 'string' || !commentId) return { ok: false, error: 'not_found' }

  const comment = await prisma.comment.findUnique({ where: { id: commentId }, select: { authorId: true, status: true } })
  if (!comment || comment.status === 'deleted') return { ok: false, error: 'not_found' }
  if (comment.authorId !== user.id) return { ok: false, error: 'forbidden' }

  await prisma.comment.update({ where: { id: commentId }, data: { status: 'deleted' } })
  return { ok: true }
}
