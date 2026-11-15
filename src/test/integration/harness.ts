import prisma from '@/lib/prisma'

// Estado compartido entre los mocks de Next/NextAuth (setup.ts) y los tests.

type CookieOptions = Record<string, unknown>
export type StoredCookie = { value: string; options?: CookieOptions }

export const jar = new Map<string, StoredCookie>()
export const harness = {
  /** Sesión de NextAuth simulada; null = sin login. */
  session: null as null | { user: { email: string } },
  /** Simula un Server Component: cookies().set/delete lanzan, como en Next. */
  cookiesReadOnly: false,
}

export const cookieStore = {
  get(name: string) {
    const c = jar.get(name)
    return c ? { name, value: c.value } : undefined
  },
  set(name: string, value: string, options?: CookieOptions) {
    if (harness.cookiesReadOnly) throw new Error('Cookies can only be modified in a Server Action or Route Handler.')
    jar.set(name, { value, options })
  },
  delete(name: string) {
    if (harness.cookiesReadOnly) throw new Error('Cookies can only be modified in a Server Action or Route Handler.')
    jar.delete(name)
  },
}

/** Cambia de "navegador": vacía las cookies y la sesión. */
export function newBrowser() {
  jar.clear()
  harness.session = null
  harness.cookiesReadOnly = false
}

export function loginAs(email: string) {
  harness.session = { user: { email } }
}

/** Palabras fijas para los tests. Nivel 1: 3 palabras; nivel 2: 2 palabras. */
export const WORDS = {
  L1: [
    { id: 1001, word: 'Mudanza', description: 'Baile de la vaca para cambiar de casa', levelId: 1 },
    { id: 1002, word: 'Sencilla', description: 'Coloca sobre sí mismo una montura de caballo', levelId: 1 },
    { id: 1003, word: 'Montañoso', description: 'Con muchas elevaciones', levelId: 1 },
  ],
  L2: [
    { id: 2001, word: 'Manifiesta', description: 'Celebración del cacahuate', levelId: 2 },
    { id: 2002, word: 'Bisoñe', description: 'Peluca parcial', levelId: 2 },
  ],
}

export async function resetDb() {
  await prisma.$executeRawUnsafe(
    'TRUNCATE "Progress", "Account", "Session", "VerificationToken", "User", "Word", "Level", "Setting", "GameMessage" CASCADE'
  )
  await prisma.level.createMany({
    data: [
      { id: 1, name: 'Nivel 1' },
      { id: 2, name: 'Nivel 2' },
    ],
  })
  await prisma.word.createMany({ data: [...WORDS.L1, ...WORDS.L2] })
  await prisma.setting.create({ data: { id: 'costo_pista', value: '5' } })
}

export async function createRegisteredUser(email: string, data: Partial<{ neuronas: number; points: number; levelId: number; totalTime: number; username: string }> = {}) {
  return prisma.user.create({ data: { email, name: email, neuronas: 50, ...data } })
}

export async function createGuestUser(data: Partial<{ neuronas: number; points: number; levelId: number; totalTime: number }> = {}) {
  return prisma.user.create({ data: { name: 'Explorador Anónimo', neuronas: 50, ...data } })
}

export async function getUserById(id: string) {
  return prisma.user.findUniqueOrThrow({ where: { id } })
}
