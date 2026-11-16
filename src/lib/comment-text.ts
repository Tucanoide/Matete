import { COMMENT_MAX_LENGTH } from '@/lib/social-rules'

/**
 * Normaliza el texto de un comentario (FR-007): recorta, unifica saltos de línea,
 * colapsa más de dos saltos seguidos y quita caracteres de control.
 * Devuelve null si queda vacío o supera el máximo.
 */
export function normalizeCommentBody(raw: unknown): string | null {
  if (typeof raw !== 'string') return null
  const body = raw
    .replace(/\r\n?/g, '\n')
    .replace(/[\u0000-\u0009\u000B-\u001F\u007F]/g, '')
    .replace(/\n{3,}/g, '\n\n')
    .trim()
  if (body.length === 0 || Array.from(body).length > COMMENT_MAX_LENGTH) return null
  return body
}
