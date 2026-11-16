import { describe, it, expect } from 'vitest'
import { execSync } from 'node:child_process'

// INFRA — chequeos sobre el repositorio (tests/CASOS_DE_PRUEBA.md).

const tracked = () => execSync('git ls-files', { encoding: 'utf8' }).split('\n').filter(Boolean)

describe('INFRA', () => {
  it('[INFRA-05] no hay secretos ni bases de datos versionadas', () => {
    const bad = tracked().filter((f) => /(^|\/)\.env($|\.)|\.keystore$|\.jks$|\.db$|\.sqlite$/i.test(f) && !/\.example$/.test(f))
    expect(bad).toEqual([])
  })

  it('[INFRA-06] CODE_REVIEW.md no está versionado y está ignorado', () => {
    expect(tracked()).not.toContain('CODE_REVIEW.md')
    expect(execSync('git check-ignore CODE_REVIEW.md || true', { encoding: 'utf8' }).trim()).toBe('CODE_REVIEW.md')
  })
})
