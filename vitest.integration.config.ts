import { defineConfig } from 'vitest/config'
import path from 'path'

// Tests de integración: server actions contra Postgres real (constitución, principio 6).
// Correr con `npm run test:integration` (carga .env.test). Requiere una base *_test.
export default defineConfig({
  test: {
    environment: 'node',
    include: ['src/test/integration/**/*.int.test.ts'],
    setupFiles: ['./src/test/integration/setup.ts'],
    globalSetup: ['./src/test/integration/global-setup.ts'],
    fileParallelism: false,
    globals: true,
    testTimeout: 20000,
    hookTimeout: 60000,
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
})
