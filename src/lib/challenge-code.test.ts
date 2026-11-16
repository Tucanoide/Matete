import { describe, it, expect } from 'vitest'
import { generateChallengeCode, isChallengeCode } from './challenge-code'

describe('[SOC-30] código de desafío', () => {
  it('genera 10 caracteres base62 válidos y distintos', () => {
    const codes = new Set(Array.from({ length: 1000 }, generateChallengeCode))
    expect(codes.size).toBe(1000)
    codes.forEach((c) => expect(isChallengeCode(c)).toBe(true))
  })

  it('rechaza formatos inválidos', () => {
    for (const bad of ['', 'abc', 'abcdefghijk', 'abcde-ghij', "abc'--ghij", null, 12345]) {
      expect(isChallengeCode(bad)).toBe(false)
    }
  })
})
