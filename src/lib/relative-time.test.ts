import { describe, it, expect } from 'vitest'
import { formatRelativeTime } from './relative-time'

const now = new Date('2026-11-15T12:00:00Z')
const ago = (s: number) => new Date(now.getTime() - s * 1000)

describe('formatRelativeTime', () => {
  it.each([
    [10, 'recién'],
    [5 * 60, 'hace 5 min'],
    [3 * 3600, 'hace 3 h'],
    [26 * 3600, 'ayer'],
    [5 * 86400, 'hace 5 días'],
    [40 * 86400, 'hace 1 mes'],
    [100 * 86400, 'hace 3 meses'],
    [800 * 86400, 'hace 2 años'],
  ])('%i s → %s', (s, expected) => {
    expect(formatRelativeTime(ago(s), now)).toBe(expected)
  })

  it('acepta ISO y no devuelve negativos para fechas futuras', () => {
    expect(formatRelativeTime(new Date(now.getTime() + 5000).toISOString(), now)).toBe('recién')
  })
})
