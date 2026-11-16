import { describe, it, expect } from 'vitest'
import prisma from '@/lib/prisma'
import { followPlayer, unfollowPlayer, searchPlayers, getFriendsRanking } from '@/app/actions/follows'
import { getGlobalRanking, getLevelRanking } from '@/app/actions/game'
import { as, createSocialUser, createRegisteredUser, createGuestUser, solveWord, WORDS } from './harness'

// 002-social US3 — seguir jugadores y ranking de amigos (FR-014..FR-019).

const ANA = 'ana@example.com'

describe('[SOC-20] seguir y dejar de seguir (FR-014, FR-015)', () => {
  it('sigue por username (sin distinguir mayúsculas), idempotente, y deja de seguir', async () => {
    const ana = await createSocialUser('ana')
    const beto = await createSocialUser('Beto')
    expect(await as(ANA, () => followPlayer('beto'))).toEqual({ ok: true })
    expect(await as(ANA, () => followPlayer('BETO'))).toEqual({ ok: true })
    expect(await prisma.follow.count({ where: { followerId: ana.id, followeeId: beto.id } })).toBe(1)

    expect(await as(ANA, () => unfollowPlayer('beto'))).toEqual({ ok: true })
    expect(await prisma.follow.count()).toBe(0)
    expect(await as(ANA, () => unfollowPlayer('beto'))).toEqual({ ok: true })
  })

  it('rechaza: sin sesión, sin username, a sí mismo, inexistente, invitado o registrado sin username', async () => {
    await createSocialUser('ana')
    await createRegisteredUser('sin@example.com')
    await createGuestUser()
    expect(await as(null, () => followPlayer('ana'))).toEqual({ ok: false, error: 'login_required' })
    expect(await as('sin@example.com', () => followPlayer('ana'))).toEqual({ ok: false, error: 'username_required' })
    expect(await as(ANA, () => followPlayer('ana'))).toEqual({ ok: false, error: 'self' })
    expect(await as(ANA, () => followPlayer('nadie'))).toEqual({ ok: false, error: 'not_found' })
    expect(await as(ANA, () => followPlayer(''))).toEqual({ ok: false, error: 'not_found' })
    // Un invitado no tiene username; aunque se le pusiera uno, no es seguible sin Google.
    const guest = await prisma.user.findFirstOrThrow({ where: { email: null } })
    await prisma.user.update({ where: { id: guest.id }, data: { username: 'fantasma' } })
    expect(await as(ANA, () => followPlayer('fantasma'))).toEqual({ ok: false, error: 'not_found' })
  })

  it('tope de 500 seguidos, también con pedidos simultáneos', async () => {
    const ana = await createSocialUser('ana')
    await prisma.user.createMany({
      data: Array.from({ length: 501 }, (_, i) => ({ email: `u${i}@example.com`, username: `u${i}`, name: `u${i}` })),
    })
    const ids = (await prisma.user.findMany({ where: { username: { startsWith: 'u' } }, select: { id: true, username: true } }))
    const first = ids.filter((u) => u.username !== 'u499' && u.username !== 'u500').slice(0, 499)
    await prisma.follow.createMany({ data: first.map((u) => ({ followerId: ana.id, followeeId: u.id })) })

    const res = await as(ANA, () => Promise.all([followPlayer('u499'), followPlayer('u500'), followPlayer('u499')]))
    expect(await prisma.follow.count({ where: { followerId: ana.id } })).toBe(500)
    expect(res.some((r) => !r.ok && r.error === 'limit')).toBe(true)
  })
})

describe('[SOC-21] búsqueda por prefijo (FR-016)', () => {
  it('prefijo ≥ 2, sin mayúsculas, excluye al propio, invitados y sin Google; marca seguidos', async () => {
    await createSocialUser('ana')
    await createSocialUser('martin')
    await createSocialUser('Marta')
    await createSocialUser('mario_x')
    await createRegisteredUser('nouser@example.com')
    await as(ANA, () => followPlayer('marta'))

    expect(await as(ANA, () => searchPlayers('m'))).toEqual({ ok: false, error: 'too_short' })
    const r = await as(ANA, () => searchPlayers('@MAR'))
    if (!r.ok) throw new Error()
    expect(r.players.map((p) => [p.username, p.isFollowed])).toEqual([
      ['mario_x', false],
      ['Marta', true],
      ['martin', false],
    ])
    expect(Object.keys(r.players[0]).sort()).toEqual(['image', 'isFollowed', 'username'])

    // "_" y "%" son literales, no comodines.
    const lit = await as(ANA, () => searchPlayers('ma_'))
    if (!lit.ok) throw new Error()
    expect(lit.players).toHaveLength(0)
    const pct = await as(ANA, () => searchPlayers('%a'))
    if (!pct.ok) throw new Error()
    expect(pct.players).toHaveLength(0)
  })
})

describe('[SOC-22] ranking de amigos y marcas en rankings (FR-017..FR-019)', () => {
  it('muestra al jugador y sus seguidos ordenados por puntos y tiempo', async () => {
    await createSocialUser('ana', { points: 5, totalTime: 100 })
    await createSocialUser('beto', { points: 9, totalTime: 300 })
    await createSocialUser('caro', { points: 5, totalTime: 50 })
    await createSocialUser('dani', { points: 99 }) // no seguido
    await as(ANA, () => followPlayer('beto'))
    await as(ANA, () => followPlayer('caro'))

    const r = await as(ANA, () => getFriendsRanking())
    if (!r.ok) throw new Error()
    expect(r.entries.map((e) => [e.username, e.isMe])).toEqual([
      ['beto', false],
      ['caro', false],
      ['ana', true],
    ])
    expect(JSON.stringify(r)).not.toMatch(/"id"/)

    await as(ANA, () => unfollowPlayer('beto'))
    const r2 = await as(ANA, () => getFriendsRanking())
    if (!r2.ok) throw new Error()
    expect(r2.entries.map((e) => e.username)).toEqual(['caro', 'ana'])
    expect(await as(null, () => getFriendsRanking())).toEqual({ ok: false, error: 'login_required' })
  })

  it('rankings global y de nivel marcan isMe/isFollowed y siguen sin id', async () => {
    const ana = await createSocialUser('ana', { points: 1 })
    const beto = await createSocialUser('beto', { points: 2 })
    await solveWord(ana.id, WORDS.L1[0].id)
    await solveWord(beto.id, WORDS.L1[0].id)
    await as(ANA, () => followPlayer('beto'))

    const global = await as(ANA, () => getGlobalRanking())
    expect(global.map((e) => [e.username, e.isMe, e.isFollowed])).toEqual([
      ['beto', false, true],
      ['ana', true, false],
    ])
    const level = await as(ANA, () => getLevelRanking(1))
    expect(level.find((e) => e.username === 'beto')).toMatchObject({ isFollowed: true, isMe: false })
    for (const e of [...global, ...level]) expect(e).not.toHaveProperty('id')

    const anon = await as(null, () => getGlobalRanking())
    expect(anon.every((e) => !e.isMe && !e.isFollowed)).toBe(true)
  })
})
