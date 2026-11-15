import { vi, beforeEach, afterAll } from 'vitest'

// Prisma es REAL en estos tests. Solo se simulan las APIs de Next y NextAuth
// que no existen fuera de un request.

vi.mock('next/headers', async () => {
  const h = await import('./harness')
  return { cookies: async () => h.cookieStore }
})

vi.mock('next-auth', async () => {
  const h = await import('./harness')
  return { getServerSession: async () => h.harness.session }
})

beforeEach(async () => {
  const h = await import('./harness')
  h.newBrowser()
  await h.resetDb()
})

afterAll(async () => {
  const { default: prisma } = await import('@/lib/prisma')
  await prisma.$disconnect()
})
