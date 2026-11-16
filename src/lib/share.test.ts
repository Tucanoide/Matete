import { describe, it, expect } from 'vitest'
import { buildShareText, buildChallengeShareText, formatDuration } from './share'

describe('formatDuration', () => {
  it.each([
    [0, '0:00'],
    [42.4, '0:42'],
    [61, '1:01'],
    [3600, '60:00'],
    [-5, '0:00'],
  ])('%s → %s', (s, expected) => expect(formatDuration(s)).toBe(expected))
})

describe('[SOC-01] buildShareText', () => {
  const base = { levelId: 3, length: 7, revealedIndices: [1], timeTaken: 42, url: 'https://matete.example' }

  it('muestra nivel, casilleros con la pista marcada, tiempo, pistas y link', () => {
    expect(buildShareText(base)).toBe(
      'Matete 🧠 Nivel 3\n🟩🟨🟩🟩🟩🟩🟩\n⏱️ 0:42 · 💡 1 pista\nhttps://matete.example'
    )
  })

  it('sin pistas y con dos pistas', () => {
    expect(buildShareText({ ...base, revealedIndices: [] })).toContain('💡 sin pistas')
    expect(buildShareText({ ...base, revealedIndices: [0, 6] })).toContain('🟨🟩🟩🟩🟩🟩🟨')
    expect(buildShareText({ ...base, revealedIndices: [0, 6] })).toContain('2 pistas')
  })

  it('ignora índices fuera de rango y no contiene letras de la palabra', () => {
    const text = buildShareText({ ...base, revealedIndices: [99, -1] })
    expect(text).toContain('sin pistas')
    // Solo el encabezado fijo, el link y emojis: ninguna otra palabra.
    expect(text.replace('Matete', '').replace('Nivel', '').replace('sin pistas', '').replace(base.url, '')).not.toMatch(/[A-Za-zÑñ]{3,}/)
  })
})

describe('buildChallengeShareText', () => {
  it('incluye desafiante, largo, tiempo y link', () => {
    expect(buildChallengeShareText({ username: 'juan', length: 7, creatorTime: 42, url: 'https://x/desafio/abc' })).toBe(
      '⚡ @juan te desafía en Matete\n¿Resolvés esta palabra de 7 letras en menos de 0:42?\nhttps://x/desafio/abc'
    )
  })
})
