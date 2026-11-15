import prisma from '@/lib/prisma'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { cookies } from 'next/headers'
import {
  GUEST_COOKIE,
  LEGACY_GUEST_COOKIE,
  guestCookieOptions,
  isLegacyWindowOpen,
  signGuestToken,
  verifyGuestToken,
} from '@/lib/guest-cookie'

// Resolución de identidad. Módulo solo de servidor: NO es un archivo 'use server',
// así que estas funciones no quedan expuestas como Server Actions al navegador
// (antes getUser/ensureUser lo estaban y devolvían el usuario completo con su id).

export type GuestCookieRead = { userId: string; legacy: boolean }

/** Lee la credencial de invitado: firmada, o la vieja si la ventana de transición sigue abierta. */
export async function readGuestCookie(): Promise<GuestCookieRead | null> {
  const store = await cookies()
  const signed = verifyGuestToken(store.get(GUEST_COOKIE)?.value)
  if (signed) return { userId: signed, legacy: false }

  if (isLegacyWindowOpen()) {
    const raw = store.get(LEGACY_GUEST_COOKIE)?.value
    if (raw) return { userId: raw, legacy: true }
  }
  return null
}

/** Emite la cookie firmada. Lanza si no hay secreto o si se llama fuera de una Server Action. */
export async function issueGuestCookie(userId: string) {
  const token = signGuestToken(userId)
  if (!token) throw new Error('Guest sessions unavailable: GUEST_COOKIE_SECRET is not configured')
  const store = await cookies()
  store.set(GUEST_COOKIE, token, guestCookieOptions())
  store.delete(LEGACY_GUEST_COOKIE)
}

export async function clearGuestCookies() {
  const store = await cookies()
  store.delete(GUEST_COOKIE)
  store.delete(LEGACY_GUEST_COOKIE)
}

/** Usuario actual: sesión de Google o invitado con credencial válida. Nunca una cuenta registrada vía cookie. */
export async function getCurrentUser() {
  const session = await getServerSession(authOptions)
  if (session?.user?.email) {
    const user = await prisma.user.findUnique({ where: { email: session.user.email } })
    if (user) return user
  }

  const guest = await readGuestCookie()
  if (!guest) return null

  const user = await prisma.user.findFirst({ where: { id: guest.userId, email: null } })
  if (!user) return null

  if (guest.legacy) {
    // Reemisión best-effort: en un Server Component cookies().set lanza; se concreta
    // en la primera Server Action (startWord se llama al montar el juego).
    try {
      await issueGuestCookie(user.id)
    } catch {
      /* se reintenta en la próxima action */
    }
  }
  return user
}

/** Igual que getCurrentUser pero crea un invitado si no hay nadie. Solo desde Server Actions. */
export async function ensureCurrentUser() {
  const user = await getCurrentUser()
  if (user) return user

  const guest = await prisma.user.create({
    data: { name: 'Explorador Anónimo', neuronas: 50, levelId: 1 },
  })
  try {
    await issueGuestCookie(guest.id)
  } catch (error) {
    await prisma.user.delete({ where: { id: guest.id } })
    throw error
  }
  return guest
}
