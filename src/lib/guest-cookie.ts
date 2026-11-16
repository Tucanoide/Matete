import { createHmac, timingSafeEqual } from 'node:crypto'

// Credencial de invitado firmada (spec FR-001..FR-004, research R1).
// Formato: v1.<userId>.<expEpochSeconds>.<hmac-sha256 base64url>

export const GUEST_COOKIE = 'matete-guest'
export const LEGACY_GUEST_COOKIE = 'matete-guest-id'
export const GUEST_COOKIE_MAX_AGE = 60 * 60 * 24 * 30 // 30 días

const VERSION = 'v1'

export function guestCookieOptions() {
  return {
    httpOnly: true,
    sameSite: 'lax' as const,
    secure: process.env.NODE_ENV === 'production',
    path: '/',
    maxAge: GUEST_COOKIE_MAX_AGE,
  }
}

function getSecret(): string | null {
  const s = process.env.GUEST_COOKIE_SECRET
  if (!s || s.length < 16) {
    console.error('[guest-cookie] GUEST_COOKIE_SECRET ausente o demasiado corto: las credenciales de invitado se rechazan.')
    return null
  }
  return s
}

function hmac(secret: string, payload: string): string {
  return createHmac('sha256', secret).update(payload).digest('base64url')
}

const nowSeconds = (now: Date) => Math.floor(now.getTime() / 1000)

export function signGuestToken(userId: string, now: Date = new Date()): string | null {
  const secret = getSecret()
  if (!secret) return null
  if (!userId || userId.includes('.')) throw new Error('userId inválido para la credencial de invitado')
  const exp = nowSeconds(now) + GUEST_COOKIE_MAX_AGE
  const payload = `${VERSION}.${userId}.${exp}`
  return `${payload}.${hmac(secret, payload)}`
}

/** Devuelve el userId si el token es auténtico y no venció; si no, null. */
export function verifyGuestToken(token: string | undefined | null, now: Date = new Date()): string | null {
  if (!token) return null
  const parts = token.split('.')
  if (parts.length !== 4) return null
  const [version, userId, expStr, sig] = parts
  if (version !== VERSION || !userId || !/^\d+$/.test(expStr)) return null

  const secret = getSecret()
  if (!secret) return null

  const expected = Buffer.from(hmac(secret, `${version}.${userId}.${expStr}`))
  const given = Buffer.from(sig)
  if (expected.length !== given.length || !timingSafeEqual(expected, given)) return null

  if (Number(expStr) < nowSeconds(now)) return null
  return userId
}

/** ¿Se acepta todavía la cookie vieja sin firmar? Sin la variable, nunca. */
export function isLegacyWindowOpen(now: Date = new Date()): boolean {
  const until = process.env.GUEST_LEGACY_COOKIE_UNTIL
  if (!until) return false
  const t = Date.parse(until)
  if (Number.isNaN(t)) return false
  return now.getTime() < t
}
