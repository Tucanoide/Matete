// Textos para compartir (spec 002-social, FR-002). Nunca incluyen la solución ni la definición.

export function formatDuration(seconds: number): string {
  const total = Math.max(0, Math.round(seconds))
  const mins = Math.floor(total / 60)
  const secs = total % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

export type ShareInput = {
  levelId: number
  length: number
  revealedIndices: number[]
  timeTaken: number
  url: string
}

export function buildShareText({ levelId, length, revealedIndices, timeTaken, url }: ShareInput): string {
  const revealed = new Set(revealedIndices.filter((i) => i >= 0 && i < length))
  const squares = Array.from({ length }, (_, i) => (revealed.has(i) ? '🟨' : '🟩')).join('')
  const hints = revealed.size === 0 ? 'sin pistas' : revealed.size === 1 ? '1 pista' : `${revealed.size} pistas`
  return [`Matete 🧠 Nivel ${levelId}`, squares, `⏱️ ${formatDuration(timeTaken)} · 💡 ${hints}`, url].join('\n')
}

export type ChallengeShareInput = {
  username: string
  length: number
  creatorTime: number
  url: string
}

export function buildChallengeShareText({ username, length, creatorTime, url }: ChallengeShareInput): string {
  return [
    `⚡ @${username} te desafía en Matete`,
    `¿Resolvés esta palabra de ${length} letras en menos de ${formatDuration(creatorTime)}?`,
    url,
  ].join('\n')
}
