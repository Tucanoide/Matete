import { execSync } from 'node:child_process'

// Corre una vez antes de toda la suite de integración.
// 1. Aborta si DATABASE_URL no apunta a una base *_test (protege producción).
// 2. Aplica las migraciones (no destructivo, idempotente). Esto además valida FR-023:
//    las migraciones del repo levantan el schema completo sobre una base vacía.
export default function globalSetup() {
  const url = process.env.DATABASE_URL
  if (!url) throw new Error('DATABASE_URL no está definida. Correr con `npm run test:integration` (.env.test).')

  const dbName = new URL(url).pathname.replace(/^\//, '')
  if (!dbName.endsWith('_test')) {
    throw new Error(`Abortado: DATABASE_URL apunta a "${dbName}", que no termina en _test.`)
  }
  if (process.env.TEST_DATABASE_URL && process.env.TEST_DATABASE_URL !== url) {
    throw new Error('Abortado: DATABASE_URL y TEST_DATABASE_URL no coinciden.')
  }

  execSync('npx prisma migrate deploy', { stdio: 'pipe', env: process.env })
}
