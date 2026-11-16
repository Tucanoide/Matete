import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import '@testing-library/jest-dom'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import ChallengePlay from './ChallengePlay'
import { startChallenge, submitChallengeGuess, giveUpChallenge, type ChallengeView } from '@/app/actions/challenges'
import { signIn } from 'next-auth/react'

const refresh = vi.fn()
vi.mock('next/navigation', () => ({ useRouter: () => ({ refresh }) }))
vi.mock('next-auth/react', () => ({ signIn: vi.fn() }))
vi.mock('@/app/actions/game', () => ({ checkUsername: vi.fn(), updateUsername: vi.fn() }))
vi.mock('@/app/actions/challenges', () => ({
  startChallenge: vi.fn(),
  submitChallengeGuess: vi.fn(),
  giveUpChallenge: vi.fn(),
}))

const base: ChallengeView = {
  code: 'abcdefghij',
  creator: { username: 'ana', image: null },
  length: 4,
  creatorTime: 42,
  creatorHints: 1,
  results: [],
  viewer: 'can_play',
  myResult: null,
  solution: null,
}

beforeEach(() => vi.clearAllMocks())

describe('[SOC-37] ChallengePlay', () => {
  it('anónimo: muestra el desafío sin definición y ofrece ingresar volviendo al mismo link', async () => {
    render(<ChallengePlay view={{ ...base, viewer: 'anonymous' }} />)
    expect(screen.getByText(/@ana te desafía/)).toBeInTheDocument()
    expect(screen.getByText('0:42')).toBeInTheDocument()
    await userEvent.click(screen.getByRole('button', { name: /INGRESÁ CON GOOGLE/ }))
    expect(signIn).toHaveBeenCalledWith('google', { callbackUrl: '/desafio/abcdefghij' })
    expect(startChallenge).not.toHaveBeenCalled()
  })

  it('creador: no puede jugar, solo compartir', () => {
    render(<ChallengePlay view={{ ...base, viewer: 'creator' }} />)
    expect(screen.queryByRole('button', { name: /ACEPTAR/ })).not.toBeInTheDocument()
    expect(screen.getByRole('button', { name: /COMPARTIR DESAFÍO/ })).toBeInTheDocument()
  })

  it('acepta, escribe con el pool y con teclado, y muestra la victoria', async () => {
    vi.mocked(startChallenge).mockResolvedValue({ ok: true, word: { description: 'Fruta tropical', length: 4, pool: ['C', 'O', 'C', 'O', 'X'] }, elapsed: 0 })
    vi.mocked(submitChallengeGuess).mockResolvedValue({ correct: true, solution: 'COCO', timeTaken: 20, creatorTime: 42, won: true, knewWord: false })
    render(<ChallengePlay view={base} />)
    await userEvent.click(screen.getByRole('button', { name: /ACEPTAR DESAFÍO/ }))
    expect(await screen.findByText(/Fruta tropical/)).toBeInTheDocument()

    await userEvent.click(screen.getAllByRole('button', { name: 'C' })[0])
    await userEvent.click(screen.getAllByRole('button', { name: 'O' })[0])
    fireEvent.keyDown(window, { key: 'c' })
    fireEvent.keyDown(window, { key: 'o' })

    await waitFor(() => expect(submitChallengeGuess).toHaveBeenCalledWith('abcdefghij', 'COCO'))
    expect(await screen.findByText(/¡Le ganaste a @ana!/)).toBeInTheDocument()
    expect(screen.getByText('COCO')).toBeInTheDocument()
    expect(refresh).toHaveBeenCalled()
    // Las fichas usadas quedan deshabilitadas: no hubo pistas ni saltos que ofrecer.
    expect(screen.queryByText(/LETRA/)).not.toBeInTheDocument()
  })

  it('intento incorrecto avisa y permite seguir; borrar libera la ficha', async () => {
    vi.mocked(startChallenge).mockResolvedValue({ ok: true, word: { description: 'x', length: 2, pool: ['A', 'B'] }, elapsed: 5 })
    vi.mocked(submitChallengeGuess).mockResolvedValue({ correct: false })
    render(<ChallengePlay view={{ ...base, length: 2, viewer: 'in_progress' }} />)
    await userEvent.click(screen.getByRole('button', { name: /CONTINUAR/ }))
    await screen.findByText('0:05')
    await userEvent.click(screen.getByRole('button', { name: 'B' }))
    await userEvent.click(screen.getByRole('button', { name: 'A' }))
    expect(await screen.findByText(/No es esa/)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'A' })).toBeDisabled()
    await userEvent.click(screen.getByRole('button', { name: 'BORRAR' }))
    expect(screen.getByRole('button', { name: 'A' })).toBeEnabled()
    expect(screen.getByRole('button', { name: 'B' })).toBeDisabled()
  })

  it('rendirse muestra la palabra', async () => {
    vi.mocked(startChallenge).mockResolvedValue({ ok: true, word: { description: 'x', length: 4, pool: ['A'] }, elapsed: 0 })
    vi.mocked(giveUpChallenge).mockResolvedValue({ ok: true, solution: 'COCO' })
    render(<ChallengePlay view={base} />)
    await userEvent.click(screen.getByRole('button', { name: /ACEPTAR DESAFÍO/ }))
    await userEvent.click(await screen.findByRole('button', { name: /ME RINDO/ }))
    expect(await screen.findByText(/Te rendiste/)).toBeInTheDocument()
    expect(screen.getByText('COCO')).toBeInTheDocument()
  })

  it('terminado: muestra su resultado y la tabla', () => {
    render(
      <ChallengePlay
        view={{
          ...base,
          viewer: 'finished',
          solution: 'COCO',
          myResult: { username: 'beto', image: null, solved: true, gaveUp: false, knewWord: true, timeTaken: 10, won: false },
          results: [{ username: 'beto', image: null, solved: true, gaveUp: false, knewWord: true, timeTaken: 10, won: false }],
        }}
      />
    )
    expect(screen.getByText(/ya la conocías/)).toBeInTheDocument()
    expect(screen.getByText('@beto')).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: /ACEPTAR/ })).not.toBeInTheDocument()
  })
})
