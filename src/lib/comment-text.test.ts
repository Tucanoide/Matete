import { describe, it, expect } from 'vitest'
import { normalizeCommentBody } from './comment-text'

describe('[SOC-10] normalizeCommentBody', () => {
  it('recorta y conserva el texto', () => {
    expect(normalizeCommentBody('  ¡Qué buena!  ')).toBe('¡Qué buena!')
  })

  it('rechaza vacío, solo espacios y no-strings', () => {
    expect(normalizeCommentBody('')).toBeNull()
    expect(normalizeCommentBody('   \n\n  ')).toBeNull()
    expect(normalizeCommentBody(123)).toBeNull()
    expect(normalizeCommentBody(undefined)).toBeNull()
  })

  it('acepta 280 caracteres y rechaza 281 (cuenta emojis como uno)', () => {
    expect(normalizeCommentBody('a'.repeat(280))).toHaveLength(280)
    expect(normalizeCommentBody('a'.repeat(281))).toBeNull()
    expect(normalizeCommentBody('😀'.repeat(280))).not.toBeNull()
  })

  it('colapsa más de dos saltos de línea y quita caracteres de control', () => {
    expect(normalizeCommentBody('hola\r\n\r\n\r\n\r\nchau')).toBe('hola\n\nchau')
    expect(normalizeCommentBody('a\u0000b\tc')).toBe('abc')
  })

  it('guarda HTML tal cual (se muestra como texto plano)', () => {
    expect(normalizeCommentBody('<script>alert(1)</script>')).toBe('<script>alert(1)</script>')
  })
})
