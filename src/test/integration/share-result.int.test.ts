import { describe, it, expect } from 'vitest'
import prisma from '@/lib/prisma'
import { startWord, submitGuess, requestHint } from '@/app/actions/game'
import { GUEST_COOKIE, verifyGuestToken } from '@/lib/guest-cookie'
import { jar, WORDS } from './harness'

// 002-social US1 — el texto para compartir usa lo que registró el servidor (FR-003).

const W1 = WORDS.L1[0] // Mudanza

describe('[SOC-02] submitGuess devuelve el resultado registrado por el servidor', () => {
  it('incluye tiempo de servidor y letras reveladas por pista', async () => {
    await startWord(W1.id)
    const userId = verifyGuestToken(jar.get(GUEST_COOKIE)!.value)!
    const hint = await requestHint(W1.id, 'letter')
    expect(hint.success).toBe(true)

    // Simula 12 s desde la entrega, sin depender del reloj del cliente.
    await prisma.$executeRaw`UPDATE "Progress" SET "startTime" = "startTime" - interval '12 seconds' WHERE "userId" = ${userId}`

    const r = await submitGuess(W1.id, W1.word)
    if (!('correct' in r) || !r.correct) throw new Error('esperaba acierto')
    const p = await prisma.progress.findUniqueOrThrow({ where: { userId_wordId: { userId, wordId: W1.id } } })

    expect(r.result.timeTaken).toBeGreaterThanOrEqual(12)
    expect(r.result.timeTaken).toBeCloseTo(p.timeTaken!, 5)
    expect(r.result.revealedIndices).toEqual(p.revealedIndices)
    expect(r.result.revealedIndices).toHaveLength(1)
  })

  it('con alreadySolved devuelve el mismo resultado original', async () => {
    await startWord(W1.id)
    const first = await submitGuess(W1.id, W1.word)
    const again = await submitGuess(W1.id, W1.word.toLowerCase())
    if (!('correct' in first) || !first.correct || !('correct' in again) || !again.correct) throw new Error('esperaba acierto')
    expect(again.alreadySolved).toBe(true)
    expect(again.result).toEqual(first.result)
  })
})
