import { describe, it, expect } from 'vitest'
import prisma from '@/lib/prisma'
import { startWord, getGlobalRanking, getLevelRanking, getInitialGameState } from '@/app/actions/game'
import { GUEST_COOKIE, LEGACY_GUEST_COOKIE, signGuestToken } from '@/lib/guest-cookie'
import { jar, harness, newBrowser, createGuestUser, createRegisteredUser, WORDS } from './harness'

// US1 — Mi cuenta no la puede usar otra persona (FR-001..FR-005)

async function guestCount() {
  return prisma.user.count({ where: { email: null } })
}

describe('US1 identidad', () => {
  it('ensureUser crea un invitado con cookie firmada httpOnly (FR-001, FR-002)', async () => {
    const r = await startWord(WORDS.L1[0].id)
    expect(r).toEqual({ ok: true })
    const cookie = jar.get(GUEST_COOKIE)
    expect(cookie?.value).toMatch(/^v1\./)
    expect(cookie?.options).toMatchObject({ httpOnly: true, sameSite: 'lax', path: '/' })
    expect(await guestCount()).toBe(1)
  })

  it('una cookie alterada se trata como visitante nuevo sin tocar al jugador original (US1-1)', async () => {
    const victim = await createGuestUser({ points: 7 })
    const token = signGuestToken(victim.id)!
    jar.set(GUEST_COOKIE, { value: token.slice(0, -2) + (token.endsWith('A') ? 'BB' : 'AA') })

    await startWord(WORDS.L1[0].id)

    expect(await guestCount()).toBe(2) // se creó un invitado nuevo
    const newToken = jar.get(GUEST_COOKIE)!.value
    expect(newToken).not.toContain(victim.id)
    expect(await prisma.progress.count({ where: { userId: victim.id } })).toBe(0)
  })

  it('una cookie firmada que apunta a una cuenta registrada se rechaza (FR-003, US1-3)', async () => {
    const registered = await createRegisteredUser('victima@example.com')
    jar.set(GUEST_COOKIE, { value: signGuestToken(registered.id)! })

    await startWord(WORDS.L1[0].id)

    expect(await prisma.progress.count({ where: { userId: registered.id } })).toBe(0)
    expect(await guestCount()).toBe(1)
  })

  it('cookie legacy de invitado dentro de la ventana: se acepta y se reemplaza por una firmada (FR-004, US1-4)', async () => {
    process.env.GUEST_LEGACY_COOKIE_UNTIL = '2099-12-31'
    const guest = await createGuestUser({ points: 3 })
    jar.set(LEGACY_GUEST_COOKIE, { value: guest.id })

    await startWord(WORDS.L1[0].id)

    expect(await guestCount()).toBe(1)
    expect(await prisma.progress.count({ where: { userId: guest.id } })).toBe(1)
    expect(jar.has(LEGACY_GUEST_COOKIE)).toBe(false)
    expect(jar.get(GUEST_COOKIE)?.value).toMatch(/^v1\./)
  })

  it('cookie legacy fuera de la ventana: se rechaza (FR-004)', async () => {
    process.env.GUEST_LEGACY_COOKIE_UNTIL = '2000-01-01'
    try {
      const guest = await createGuestUser()
      jar.set(LEGACY_GUEST_COOKIE, { value: guest.id })
      await startWord(WORDS.L1[0].id)
      expect(await prisma.progress.count({ where: { userId: guest.id } })).toBe(0)
      expect(await guestCount()).toBe(2)
    } finally {
      process.env.GUEST_LEGACY_COOKIE_UNTIL = '2099-12-31'
    }
  })

  it('cookie legacy que apunta a una cuenta registrada: se rechaza siempre (US1-5)', async () => {
    process.env.GUEST_LEGACY_COOKIE_UNTIL = '2099-12-31'
    const registered = await createRegisteredUser('otra@example.com')
    jar.set(LEGACY_GUEST_COOKIE, { value: registered.id })

    await startWord(WORDS.L1[0].id)

    expect(await prisma.progress.count({ where: { userId: registered.id } })).toBe(0)
  })

  it('en un Server Component la cookie legacy se acepta sin romper el render, y se reemite en la siguiente action', async () => {
    process.env.GUEST_LEGACY_COOKIE_UNTIL = '2099-12-31'
    const guest = await createGuestUser()
    jar.set(LEGACY_GUEST_COOKIE, { value: guest.id })

    harness.cookiesReadOnly = true
    const state = await getInitialGameState()
    expect(state?.word.wordId).toBe(WORDS.L1[0].id)
    expect(jar.has(LEGACY_GUEST_COOKIE)).toBe(true)

    harness.cookiesReadOnly = false
    await startWord(WORDS.L1[0].id)
    expect(jar.has(LEGACY_GUEST_COOKIE)).toBe(false)
    expect(jar.get(GUEST_COOKIE)?.value).toMatch(/^v1\./)
  })

  it('los rankings no exponen ids internos (FR-005, US1-2)', async () => {
    const a = await createRegisteredUser('a@example.com', { points: 5, username: 'ana' })
    await prisma.progress.create({ data: { userId: a.id, wordId: WORDS.L1[0].id, guessed: true, timeTaken: 12 } })

    const global = await getGlobalRanking()
    const level = await getLevelRanking(1)
    expect(global.length).toBe(1)
    expect(level.length).toBe(1)
    for (const entry of [...global, ...level]) {
      expect(entry).not.toHaveProperty('id')
    }
    expect(JSON.stringify({ global, level })).not.toContain(a.id)
  })

  it('el ranking global excluye invitados', async () => {
    await createGuestUser({ points: 99 })
    await createRegisteredUser('r@example.com', { points: 1 })
    const global = await getGlobalRanking()
    expect(global).toHaveLength(1)
    expect(global[0].points).toBe(1)
  })

  it('cambiar de navegador no comparte identidad', async () => {
    await startWord(WORDS.L1[0].id)
    newBrowser()
    await startWord(WORDS.L1[0].id)
    expect(await guestCount()).toBe(2)
  })
})
