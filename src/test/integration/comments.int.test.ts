import { describe, it, expect, afterEach } from 'vitest'
import prisma from '@/lib/prisma'
import { getWordComments, postComment, reportComment, deleteComment } from '@/app/actions/comments'
import { getModerationQueue, moderateComment } from '@/app/actions/moderation'
import { startWord } from '@/app/actions/game'
import { as, createSocialUser, createRegisteredUser, solveWord, WORDS } from './harness'

// 002-social US2 — comentarios por palabra (FR-005..FR-013).

const W = WORDS.L1[0].id
const OTHER = WORDS.L1[1].id

async function setup() {
  const ana = await createSocialUser('ana')
  const beto = await createSocialUser('beto')
  await solveWord(ana.id, W)
  return { ana, beto }
}

async function backdateComments(authorId: string, seconds: number) {
  await prisma.$executeRaw`UPDATE "Comment" SET "createdAt" = "createdAt" - make_interval(secs => ${seconds}) WHERE "authorId" = ${authorId}`
}

afterEach(() => {
  delete process.env.ADMIN_EMAILS
})

describe('[SOC-11] visibilidad: solo quien acertó (FR-005)', () => {
  it('sin acierto, salteada, de otra palabra o sin usuario: locked, sin cantidad', async () => {
    const { ana, beto } = await setup()
    await as('ana@example.com', () => postComment(W, 'Muy buena'))

    expect(await as('beto@example.com', () => getWordComments(W))).toEqual({ locked: true })

    await prisma.progress.create({ data: { userId: beto.id, wordId: W, skipped: true } })
    expect(await as('beto@example.com', () => getWordComments(W))).toEqual({ locked: true })

    await solveWord(beto.id, OTHER)
    expect(await as('beto@example.com', () => getWordComments(W))).toEqual({ locked: true })

    expect(await as(null, () => getWordComments(W))).toEqual({ locked: true })
    expect(await as('ana@example.com', () => getWordComments(Number.NaN))).toEqual({ locked: true })
    expect(ana).toBeDefined()
  })

  it('al acertar los ve, del más nuevo al más viejo, sin ids de usuario', async () => {
    const { ana, beto } = await setup()
    await as('ana@example.com', () => postComment(W, 'primero'))
    await backdateComments(ana.id, 60)
    await as('ana@example.com', () => postComment(W, 'segundo'))

    await solveWord(beto.id, W)
    const r = await as('beto@example.com', () => getWordComments(W))
    if (r.locked) throw new Error('esperaba comentarios')
    expect(r.comments.map((c) => c.body)).toEqual(['segundo', 'primero'])
    expect(r.comments[0]).toMatchObject({ author: { username: 'ana' }, mine: false, reportedByMe: false })
    expect(JSON.stringify(r)).not.toContain(ana.id)
    expect(r.canPost).toBe(true)
  })

  it('pagina de a 20 con cursor', async () => {
    const { ana } = await setup()
    await prisma.comment.createMany({
      data: Array.from({ length: 25 }, (_, i) => ({ wordId: W, authorId: ana.id, body: `c${i}`, createdAt: new Date(Date.now() - (25 - i) * 1000) })),
    })
    const p1 = await as('ana@example.com', () => getWordComments(W))
    if (p1.locked) throw new Error()
    expect(p1.comments).toHaveLength(20)
    expect(p1.comments[0].body).toBe('c24')
    const p2 = await as('ana@example.com', () => getWordComments(W, p1.nextCursor!))
    if (p2.locked) throw new Error()
    expect(p2.comments.map((c) => c.body)).toEqual(['c4', 'c3', 'c2', 'c1', 'c0'])
    expect(p2.nextCursor).toBeNull()
  })
})

describe('[SOC-12] publicar (FR-006, FR-007)', () => {
  it('invitado que acertó: lee pero no publica', async () => {
    await setup()
    const { userId } = await (async () => {
      await startWord(W)
      const g = await prisma.user.findFirstOrThrow({ where: { email: null } })
      return { userId: g.id }
    })()
    await solveWord(userId, W)

    const r = await getWordComments(W)
    expect(r).toMatchObject({ locked: false, canPost: false, postBlockedReason: 'login_required' })
    expect(await postComment(W, 'hola')).toEqual({ ok: false, error: 'login_required' })
  })

  it('registrado sin username: username_required', async () => {
    const u = await createRegisteredUser('sin@example.com')
    await solveWord(u.id, W)
    expect(await as('sin@example.com', () => postComment(W, 'hola'))).toEqual({ ok: false, error: 'username_required' })
    expect(await as('sin@example.com', () => getWordComments(W))).toMatchObject({ canPost: false, postBlockedReason: 'username_required' })
  })

  it('no acertada: not_solved', async () => {
    await setup()
    expect(await as('beto@example.com', () => postComment(W, 'hola'))).toEqual({ ok: false, error: 'not_solved' })
  })

  it('cuerpo vacío o > 280: invalid_body; HTML se guarda como texto', async () => {
    await setup()
    expect(await as('ana@example.com', () => postComment(W, '   '))).toEqual({ ok: false, error: 'invalid_body' })
    expect(await as('ana@example.com', () => postComment(W, 'x'.repeat(281)))).toEqual({ ok: false, error: 'invalid_body' })
    const r = await as('ana@example.com', () => postComment(W, '  <b>hola</b>  '))
    expect(r).toMatchObject({ ok: true, comment: { body: '<b>hola</b>', mine: true } })
  })
})

describe('[SOC-13] rate limit (FR-008)', () => {
  it('dos comentarios simultáneos: se publica uno', async () => {
    const { ana } = await setup()
    const results = await as('ana@example.com', () => Promise.all([postComment(W, 'uno'), postComment(W, 'dos'), postComment(W, 'tres')]))
    expect(results.filter((r) => r.ok)).toHaveLength(1)
    expect(results.filter((r) => !r.ok && r.error === 'too_fast')).toHaveLength(2)
    expect(await prisma.comment.count({ where: { authorId: ana.id } })).toBe(1)
  })

  it('pasados 30 s se puede; tope de 20 en 24 h', async () => {
    const { ana } = await setup()
    await as('ana@example.com', () => postComment(W, 'uno'))
    await backdateComments(ana.id, 31)
    expect(await as('ana@example.com', () => postComment(W, 'dos'))).toMatchObject({ ok: true })

    await prisma.comment.createMany({
      data: Array.from({ length: 18 }, (_, i) => ({ wordId: W, authorId: ana.id, body: `x${i}`, createdAt: new Date(Date.now() - 3600_000) })),
    })
    await backdateComments(ana.id, 31)
    expect(await as('ana@example.com', () => postComment(W, 'veintiuno'))).toEqual({ ok: false, error: 'daily_limit' })
  })
})

describe('[SOC-14] reportes y auto-ocultado (FR-010, SC-003)', () => {
  async function commentWithReporters(n: number) {
    const { ana } = await setup()
    const posted = await as('ana@example.com', () => postComment(W, 'reportame'))
    if (!posted.ok) throw new Error()
    const reporters = []
    for (let i = 0; i < n; i++) {
      const u = await createSocialUser(`rep${i}`)
      await solveWord(u.id, W)
      reporters.push(u)
    }
    return { ana, id: posted.comment.id, reporters }
  }

  it('no se puede reportar el propio ni sin haber acertado', async () => {
    const { id } = await commentWithReporters(0)
    expect(await as('ana@example.com', () => reportComment(id))).toEqual({ ok: false, error: 'own_comment' })
    expect(await as('beto@example.com', () => reportComment(id))).toEqual({ ok: false, error: 'not_solved' })
    expect(await as('rep0@example.com', () => reportComment('nope'))).toMatchObject({ ok: false })
  })

  it('reporte repetido o simultáneo del mismo jugador cuenta uno', async () => {
    const { id } = await commentWithReporters(1)
    await as('rep0@example.com', () => Promise.all([reportComment(id), reportComment(id), reportComment(id)]))
    const c = await prisma.comment.findUniqueOrThrow({ where: { id } })
    expect(c.reportCount).toBe(1)
    expect(c.status).toBe('visible')
    const view = await as('rep0@example.com', () => getWordComments(W))
    if (view.locked) throw new Error()
    expect(view.comments[0].reportedByMe).toBe(true)
  })

  it('3 reportes distintos (simultáneos) lo ocultan para todos', async () => {
    const { id } = await commentWithReporters(3)
    const results = await Promise.all([0, 1, 2].map((i) => as(`rep${i}@example.com`, () => reportComment(id))))
    expect(results.every((r) => r.ok)).toBe(true)
    const c = await prisma.comment.findUniqueOrThrow({ where: { id } })
    expect(c).toMatchObject({ reportCount: 3, status: 'hidden_reports' })
    const view = await as('ana@example.com', () => getWordComments(W))
    if (view.locked) throw new Error()
    expect(view.comments).toHaveLength(0)
  })

  it('[SOC-15] restaurado por admin: los reportes viejos no lo vuelven a ocultar (FR-013)', async () => {
    process.env.ADMIN_EMAILS = 'Admin@Example.com'
    await createSocialUser('admin')
    const { id } = await commentWithReporters(5)
    for (const i of [0, 1, 2]) await as(`rep${i}@example.com`, () => reportComment(id))

    expect(await as('admin@example.com', () => moderateComment(id, 'restore'))).toEqual({ ok: true })
    // Los mismos que ya reportaron no suman.
    for (const i of [0, 1, 2]) await as(`rep${i}@example.com`, () => reportComment(id))
    expect(await prisma.comment.findUniqueOrThrow({ where: { id } })).toMatchObject({ status: 'visible', reportCount: 0 })
    // Hacen falta 3 nuevos.
    await as('rep3@example.com', () => reportComment(id))
    await as('rep4@example.com', () => reportComment(id))
    expect((await prisma.comment.findUniqueOrThrow({ where: { id } })).status).toBe('visible')
  })
})

describe('[SOC-16] borrar y moderar (FR-011, FR-012)', () => {
  it('el autor borra el suyo; otro no puede', async () => {
    await setup()
    const posted = await as('ana@example.com', () => postComment(W, 'borrame'))
    if (!posted.ok) throw new Error()
    expect(await as('beto@example.com', () => deleteComment(posted.comment.id))).toEqual({ ok: false, error: 'forbidden' })
    expect(await as('ana@example.com', () => deleteComment(posted.comment.id))).toEqual({ ok: true })
    const view = await as('ana@example.com', () => getWordComments(W))
    if (view.locked) throw new Error()
    expect(view.comments).toHaveLength(0)
    expect(await as('ana@example.com', () => deleteComment(posted.comment.id))).toEqual({ ok: false, error: 'not_found' })
  })

  it('solo el admin ve la cola y modera', async () => {
    process.env.ADMIN_EMAILS = 'admin@example.com, otro@example.com'
    await createSocialUser('admin')
    const { beto } = await setup()
    await solveWord(beto.id, W)
    const posted = await as('ana@example.com', () => postComment(W, 'dudoso'))
    if (!posted.ok) throw new Error()
    await as('beto@example.com', () => reportComment(posted.comment.id))

    expect(await as('beto@example.com', () => getModerationQueue())).toEqual({ ok: false, error: 'forbidden' })
    expect(await as(null, () => getModerationQueue())).toEqual({ ok: false, error: 'forbidden' })
    expect(await as('beto@example.com', () => moderateComment(posted.comment.id, 'hide'))).toEqual({ ok: false, error: 'forbidden' })

    const q = await as('admin@example.com', () => getModerationQueue())
    if (!q.ok) throw new Error()
    expect(q.items).toHaveLength(1)
    expect(q.items[0]).toMatchObject({ body: 'dudoso', reportCount: 1, status: 'visible', word: 'MUDANZA' })

    expect(await as('admin@example.com', () => moderateComment(posted.comment.id, 'hide'))).toEqual({ ok: true })
    expect((await prisma.comment.findUniqueOrThrow({ where: { id: posted.comment.id } })).status).toBe('hidden_admin')
  })
})
