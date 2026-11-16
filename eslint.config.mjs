import nextVitals from 'eslint-config-next/core-web-vitals'
import nextTs from 'eslint-config-next/typescript'

// INFRA-03: `next lint` se quitó en Next 16; ESLint corre directo con la config de Next.
const config = [
  ...nextVitals,
  ...nextTs,
  {
    rules: {
      // Prefijo _ = parámetro que se ignora a propósito (p. ej. getNextWord(_levelId)).
      '@typescript-eslint/no-unused-vars': ['warn', { argsIgnorePattern: '^_', varsIgnorePattern: '^_' }],
    },
  },
  {
    ignores: ['.next/**', 'node_modules/**', 'Matete B4A/**', 'generated/**', 'coverage/**', 'next-env.d.ts'],
  },
]

export default config
