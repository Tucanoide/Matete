import { describe, it, expect } from 'vitest'
import { buildLetterPool, toPublicWord, isValidGuessFormat, isCorrectGuess, POOL_SIZE } from './public-word'

const count = (arr: string[], ch: string) => arr.filter((c) => c === ch).length

describe('[GAME-01] [GAME-13] public-word (FR-006, FR-009)', () => {
  it('el pool tiene 14 letras e incluye todas las de la solución con su multiplicidad', () => {
    for (let n = 0; n < 50; n++) {
      const pool = buildLetterPool('Sencilla')
      expect(pool).toHaveLength(POOL_SIZE)
      for (const ch of 'SENCILLA') {
        expect(count(pool, ch)).toBeGreaterThanOrEqual(count('SENCILLA'.split(''), ch))
      }
    }
  })

  it('soporta ñ', () => {
    const pool = buildLetterPool('Bisoñe')
    expect(pool).toContain('Ñ')
  })

  it('PublicWord no contiene la solución en ningún campo', () => {
    const pw = toPublicWord({ id: 1, word: 'Mudanza', description: 'Baile de la vaca' }, [0, 3])
    expect(Object.keys(pw).sort()).toEqual(['description', 'length', 'pool', 'revealed', 'wordId'])
    expect(JSON.stringify(pw).toUpperCase()).not.toContain('MUDANZA')
    expect(pw.length).toBe(7)
  })

  it('revealed devuelve la letra correcta en cada posición', () => {
    const pw = toPublicWord({ id: 1, word: 'Mudanza', description: 'x' }, [0, 3, 99])
    expect(pw.revealed).toEqual([
      { index: 0, letter: 'M' },
      { index: 3, letter: 'A' },
    ])
  })

  it('valida el formato del intento', () => {
    expect(isValidGuessFormat('MUDANZA', 7)).toBe(true)
    expect(isValidGuessFormat('bisoñe', 6)).toBe(true)
    expect(isValidGuessFormat('MUDANZ', 7)).toBe(false)
    expect(isValidGuessFormat('MUDANZ4', 7)).toBe(false)
    expect(isValidGuessFormat('', 0)).toBe(false)
    expect(isValidGuessFormat(123, 3)).toBe(false)
  })

  it('compara sin distinguir mayúsculas', () => {
    expect(isCorrectGuess('mudanza', 'Mudanza')).toBe(true)
    expect(isCorrectGuess('BISOÑE', 'Bisoñe')).toBe(true)
    expect(isCorrectGuess('MUDANZO', 'Mudanza')).toBe(false)
  })
})
