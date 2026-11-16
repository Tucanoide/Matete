import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { getCurrentUser } from '@/lib/identity'

// Identidad para funciones sociales. Módulo de servidor, NO 'use server'.
// Constitución 2: lo social es solo para usuarios registrados con Google; además se
// exige username porque es el identificador público (nunca se expone User.id).

export type SocialError = 'login_required' | 'username_required'

type User = NonNullable<Awaited<ReturnType<typeof getCurrentUser>>>
export type SocialUser = User & { email: string; username: string }

export function socialErrorFor(user: User | null): SocialError | null {
  if (!user || !user.email) return 'login_required'
  if (!user.username) return 'username_required'
  return null
}

export async function requireSocialUser(): Promise<{ user: SocialUser; error: null } | { user: null; error: SocialError }> {
  const user = await getCurrentUser()
  const error = socialErrorFor(user)
  if (error) return { user: null, error }
  return { user: user as SocialUser, error: null }
}

export function adminEmails(): string[] {
  return (process.env.ADMIN_EMAILS ?? '')
    .split(',')
    .map((e) => e.trim().toLowerCase())
    .filter(Boolean)
}

/** Admin = sesión de Google cuyo email está en ADMIN_EMAILS. */
export async function isAdmin(): Promise<boolean> {
  const session = await getServerSession(authOptions)
  const email = session?.user?.email?.toLowerCase()
  return !!email && adminEmails().includes(email)
}
