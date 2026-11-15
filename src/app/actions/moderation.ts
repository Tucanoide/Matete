'use server'

import prisma from '@/lib/prisma'
import { isAdmin } from '@/lib/social-auth'

// Moderación de comentarios (spec 002-social FR-012, FR-013). Solo emails de ADMIN_EMAILS.

export type ModerationItem = {
  id: string
  body: string
  createdAt: string
  author: { username: string; image: string | null }
  status: string
  reportCount: number
  totalReports: number
  word: string
}

export async function getModerationQueue(): Promise<{ ok: true; items: ModerationItem[] } | { ok: false; error: 'forbidden' }> {
  if (!(await isAdmin())) return { ok: false, error: 'forbidden' }

  const rows = await prisma.comment.findMany({
    where: {
      status: { not: 'deleted' },
      OR: [{ status: { in: ['hidden_reports', 'hidden_admin'] } }, { reportCount: { gt: 0 } }],
    },
    orderBy: [{ reportCount: 'desc' }, { createdAt: 'desc' }],
    take: 200,
    select: {
      id: true,
      body: true,
      createdAt: true,
      status: true,
      reportCount: true,
      author: { select: { username: true, image: true } },
      word: { select: { word: true } },
      _count: { select: { reports: true } },
    },
  })

  return {
    ok: true,
    items: rows.map((r) => ({
      id: r.id,
      body: r.body,
      createdAt: r.createdAt.toISOString(),
      author: { username: r.author.username ?? 'anónimo', image: r.author.image },
      status: r.status,
      reportCount: r.reportCount,
      totalReports: r._count.reports,
      word: r.word.word.toUpperCase(),
    })),
  }
}

export async function moderateComment(
  commentId: string,
  action: 'hide' | 'restore'
): Promise<{ ok: true } | { ok: false; error: 'forbidden' | 'not_found' }> {
  if (!(await isAdmin())) return { ok: false, error: 'forbidden' }
  if (typeof commentId !== 'string' || (action !== 'hide' && action !== 'restore')) return { ok: false, error: 'not_found' }

  // Restaurar pone el contador en 0: los reportes viejos no lo vuelven a ocultar (FR-013),
  // y como las filas de CommentReport quedan, esas mismas personas no pueden reportarlo de nuevo.
  const updated = await prisma.comment.updateMany({
    where: { id: commentId, status: { not: 'deleted' } },
    data: action === 'hide' ? { status: 'hidden_admin' } : { status: 'visible', reportCount: 0 },
  })
  if (updated.count === 0) return { ok: false, error: 'not_found' }
  return { ok: true }
}
