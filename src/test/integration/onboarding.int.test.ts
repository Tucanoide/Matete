import { describe, it, expect } from 'vitest'
import prisma from '@/lib/prisma'
import {
  getInitialGameState,
  startWord,
  submitGuess,
  checkUsername,
  updateUsername,
  getGlobalRanking,
} from '@/app/actions/game'
import { postComment } from '@/app/actions/comments'
import { followPlayer } from '@/app/actions/follows'
import { createChallenge } from '@/app/actions/challenges'
import { GUEST_COOKIE, verifyGuestToken } from '@/lib/guest-cookie'
import { jar, newBrowser, createSocialUser, WORDS } from './harness'

// INI — Primer ingreso sin login, con nombre de fantasía (tests/CASOS_DE_PRUEBA.md).

const W1 = WORDS.L1[0] // Mudanza

function currentGuestId() {
  const cookie = jar.get(GUEST_COOKIE)
  return cookie ? verifyGuestToken(cookie.value) : null
}

describe('[INI-01] ingreso sin login con nombre de fantasía', () => {
  it('juega como invitado, elige alias, lo conserva al volver y sigue jugando', async () => {
    // 1. Primera visita: la home renderiza sin crear nada (Server Component de solo lectura).
    const first = await getInitialGameState()
    expect(first).toMatchObject({ levelId: 1, neuronas: 50, needsUsername: false })
    expect(first!.word.wordId).toBe(W1.id)
    expect(JSON.stringify(first)).not.toMatch(/mudanza/i)
    expect(await prisma.user.count()).toBe(0)

    // 2. Al montar el juego (startWord) se crea el invitado con cookie firmada y httpOnly.
    expect(await startWord(first!.word.wordId)).toEqual({ ok: true })
    const guestId = currentGuestId()
    expect(guestId).toBeTruthy()
    expect(jar.get(GUEST_COOKIE)!.options).toMatchObject({ httpOnly: true, sameSite: 'lax' })
    const guest = await prisma.user.findUniqueOrThrow({ where: { id: guestId! } })
    expect(guest).toMatchObject({ name: 'Explorador Anónimo', email: null, username: null, neuronas: 50, levelId: 1 })

    // 3. Al recargar, el invitado sin alias recibe el pedido de nombre de fantasía.
    const reload = await getInitialGameState()
    expect(reload?.needsUsername).toBe(true)

    // 4. Elige un alias libre.
    expect(await checkUsername('ZorroAzul')).toEqual({ available: true })
    expect(await updateUsername('ZorroAzul')).toEqual({ success: true })
    const named = await prisma.user.findUniqueOrThrow({ where: { id: guestId! } })
    expect(named).toMatchObject({ username: 'ZorroAzul', email: null })

    // 5. Al volver no se le pide de nuevo y sigue siendo el mismo jugador.
    const back = await getInitialGameState()
    expect(back?.needsUsername).toBe(false)
    expect(currentGuestId()).toBe(guestId)
    expect(await prisma.user.count()).toBe(1)

    // 6. Juega normalmente: el acierto se acredita a su invitado.
    const r = await submitGuess(W1.id, W1.word)
    expect(r).toMatchObject({ correct: true, alreadySolved: false, neuronas: 60, points: 1 })
  })

  it('el alias queda tomado para otros visitantes', async () => {
    await startWord(W1.id)
    await updateUsername('ZorroAzul')

    newBrowser()
    await startWord(W1.id) // checkUsername exige identidad (USER-07)
    expect(await checkUsername('ZorroAzul')).toEqual({ available: false })
  })

  it('con alias pero sin Google: no figura en el ranking ni accede a lo social', async () => {
    await createSocialUser('ana', { points: 1 })
    await startWord(W1.id)
    await updateUsername('ZorroAzul')
    await submitGuess(W1.id, W1.word)

    const ranking = await getGlobalRanking()
    expect(ranking.map((e) => e.username)).toEqual(['ana'])

    expect(await postComment(W1.id, 'hola')).toEqual({ ok: false, error: 'login_required' })
    expect(await followPlayer('ana')).toEqual({ ok: false, error: 'login_required' })
    expect(await createChallenge(W1.id)).toEqual({ ok: false, error: 'login_required' })
  })

  it('el servidor no exige alias para jugar (el pedido lo impone el modal de la UI)', async () => {
    await startWord(W1.id)
    expect((await getInitialGameState())?.needsUsername).toBe(true)
    expect(await submitGuess(W1.id, W1.word)).toMatchObject({ correct: true })
  })
})
