import { randomInt } from 'crypto'
import { CHALLENGE_CODE_LENGTH } from '@/lib/social-rules'

const ALPHABET = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'

/** Código aleatorio no adivinable para el link del desafío (~59 bits). */
export function generateChallengeCode(): string {
  let code = ''
  for (let i = 0; i < CHALLENGE_CODE_LENGTH; i++) code += ALPHABET[randomInt(ALPHABET.length)]
  return code
}

export function isChallengeCode(value: unknown): value is string {
  return typeof value === 'string' && new RegExp(`^[A-Za-z0-9]{${CHALLENGE_CODE_LENGTH}}$`).test(value)
}
