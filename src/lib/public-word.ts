// Vista pública de una palabra: lo único que el navegador recibe antes de resolverla.
// Nunca incluye la solución (spec FR-006). Ver specs/001-hardening/contracts/server-actions.md.

export const POOL_SIZE = 14
const DISTRACTORS = 'AEIOURSTNMPL'

export type RevealedLetter = { index: number; letter: string }

export type PublicWord = {
  wordId: number
  description: string
  length: number
  pool: string[]
  revealed: RevealedLetter[]
}

type WordSource = { id: number; word: string; description: string }

export function normalizeLetters(word: string): string[] {
  return word.toUpperCase().split('')
}

function shuffle<T>(items: T[], random: () => number): T[] {
  const a = [...items]
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(random() * (i + 1))
    ;[a[i], a[j]] = [a[j], a[i]]
  }
  return a
}

/** 14 letras desordenadas: las de la solución + distractores. */
export function buildLetterPool(word: string, random: () => number = Math.random): string[] {
  const letters = normalizeLetters(word)
  const fillers = Math.max(0, POOL_SIZE - letters.length)
  const distractors = Array.from({ length: fillers }, () => DISTRACTORS[Math.floor(random() * DISTRACTORS.length)])
  return shuffle([...letters, ...distractors], random)
}

export function revealedLetters(word: string, revealedIndices: number[]): RevealedLetter[] {
  const letters = normalizeLetters(word)
  return revealedIndices
    .filter((i) => i >= 0 && i < letters.length)
    .map((index) => ({ index, letter: letters[index] }))
}

export function toPublicWord(word: WordSource, revealedIndices: number[] = [], random: () => number = Math.random): PublicWord {
  return {
    wordId: word.id,
    description: word.description,
    length: word.word.length,
    pool: buildLetterPool(word.word, random),
    revealed: revealedLetters(word.word, revealedIndices),
  }
}

/** Formato válido de un intento: solo letras (incluida ñ). */
export function isValidGuessFormat(guess: unknown, length: number): guess is string {
  return typeof guess === 'string' && guess.length === length && /^[A-Za-zÑñ]+$/.test(guess)
}

export function isCorrectGuess(guess: string, solution: string): boolean {
  return guess.toUpperCase() === solution.toUpperCase()
}
