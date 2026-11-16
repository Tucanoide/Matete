import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { signGuestToken, verifyGuestToken, isLegacyWindowOpen, GUEST_COOKIE_MAX_AGE } from './guest-cookie'
import { guestCookieOptions } from './guest-cookie'

const SECRET = 'a'.repeat(48)

describe('[AUTH-10] [AUTH-11] guest-cookie (FR-001, FR-004)', () => {
  const env = { ...process.env }

  beforeEach(() => {
    process.env.GUEST_COOKIE_SECRET = SECRET
    delete process.env.GUEST_LEGACY_COOKIE_UNTIL
    vi.spyOn(console, 'error').mockImplementation(() => {})
  })
  afterEach(() => {
    process.env = { ...env }
    vi.restoreAllMocks()
  })

  it('verifica un token propio', () => {
    const t = signGuestToken('cuser123')!
    expect(verifyGuestToken(t)).toBe('cuser123')
  })

  it('rechaza cualquier carácter alterado', () => {
    const t = signGuestToken('cuser123')!
    for (let i = 0; i < t.length; i++) {
      const c = t[i] === 'x' ? 'y' : 'x'
      const altered = t.slice(0, i) + c + t.slice(i + 1)
      expect(verifyGuestToken(altered)).toBeNull()
    }
  })

  it('rechaza un token que apunta a otro usuario reutilizando la firma', () => {
    const [v, , exp, sig] = signGuestToken('cuserA')!.split('.')
    expect(verifyGuestToken(`${v}.cuserB.${exp}.${sig}`)).toBeNull()
  })

  it('rechaza un token firmado con otra clave', () => {
    const t = signGuestToken('cuser123')!
    process.env.GUEST_COOKIE_SECRET = 'b'.repeat(48)
    expect(verifyGuestToken(t)).toBeNull()
  })

  it('rechaza un token vencido', () => {
    const issued = new Date('2026-01-01T00:00:00Z')
    const t = signGuestToken('cuser123', issued)!
    const justBefore = new Date(issued.getTime() + (GUEST_COOKIE_MAX_AGE - 60) * 1000)
    const after = new Date(issued.getTime() + (GUEST_COOKIE_MAX_AGE + 60) * 1000)
    expect(verifyGuestToken(t, justBefore)).toBe('cuser123')
    expect(verifyGuestToken(t, after)).toBeNull()
  })

  it('rechaza formatos inválidos y el id en claro de la cookie vieja', () => {
    for (const bad of ['', 'cuser123', 'v1.a.b', 'v2.cuser.123.sig', 'v1.cuser.abc.sig', null, undefined]) {
      expect(verifyGuestToken(bad as string)).toBeNull()
    }
  })

  it('falla cerrado sin secreto', () => {
    const t = signGuestToken('cuser123')!
    delete process.env.GUEST_COOKIE_SECRET
    expect(verifyGuestToken(t)).toBeNull()
    expect(signGuestToken('cuser123')).toBeNull()
  })

  it('ventana legacy: abierta antes de la fecha, cerrada después, cerrada sin variable', () => {
    expect(isLegacyWindowOpen()).toBe(false)
    process.env.GUEST_LEGACY_COOKIE_UNTIL = '2026-12-15'
    expect(isLegacyWindowOpen(new Date('2026-12-01'))).toBe(true)
    expect(isLegacyWindowOpen(new Date('2026-12-16'))).toBe(false)
    process.env.GUEST_LEGACY_COOKIE_UNTIL = 'no-es-fecha'
    expect(isLegacyWindowOpen(new Date('2026-12-01'))).toBe(false)
  })

  it('[AUTH-04] opciones de la cookie: httpOnly, sameSite lax y secure solo en producción', () => {
    const env = process.env as Record<string, string | undefined>
    const prev = env.NODE_ENV
    env.NODE_ENV = 'production'
    expect(guestCookieOptions()).toMatchObject({ httpOnly: true, sameSite: 'lax', secure: true })
    env.NODE_ENV = 'test'
    expect(guestCookieOptions()).toMatchObject({ httpOnly: true, sameSite: 'lax', secure: false })
    env.NODE_ENV = prev
  })
})
