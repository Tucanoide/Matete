import { render, screen } from '@testing-library/react'
import '@testing-library/jest-dom'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import Home from './page'
import { getInitialGameState, getGameCompletion } from '@/app/actions/game'

// La home es un Server Component async: se resuelve y se renderiza su resultado.
vi.mock('@/app/actions/game', () => ({ getInitialGameState: vi.fn(), getGameCompletion: vi.fn() }))
vi.mock('@/components/layout/Header', () => ({ default: () => <header /> }))
vi.mock('@/components/game/GameEngine', () => ({
  default: (p: { initialCosts?: { letter: number; skip: number } }) => <div data-testid="engine">{JSON.stringify(p.initialCosts)}</div>,
}))

beforeEach(() => vi.clearAllMocks())

describe('Home', () => {
  it('[PROG-09] con todo resuelto muestra el fin del juego, no "Cargando..."', async () => {
    vi.mocked(getInitialGameState).mockResolvedValue(null)
    vi.mocked(getGameCompletion).mockResolvedValue({ completed: true, points: 42 })
    render(await Home())
    expect(screen.getByText(/COMPLETASTE MATETE/i)).toBeInTheDocument()
    expect(screen.getByText('42 puntos')).toBeInTheDocument()
    expect(screen.queryByText(/Cargando/i)).not.toBeInTheDocument()
  })

  it('[PROG-09] base sin palabras: aviso claro en lugar de cargar para siempre', async () => {
    vi.mocked(getInitialGameState).mockResolvedValue(null)
    vi.mocked(getGameCompletion).mockResolvedValue({ completed: false, points: 0 })
    render(await Home())
    expect(screen.getByText(/No hay palabras disponibles/i)).toBeInTheDocument()
  })

  it('[HINT-09] pasa al juego los costos que informa el servidor', async () => {
    vi.mocked(getInitialGameState).mockResolvedValue({
      word: { wordId: 1, description: 'x', length: 3, pool: [], revealed: [] },
      levelId: 1,
      neuronas: 50,
      needsUsername: false,
      costs: { letter: 8, skip: 20 },
    } as never)
    render(await Home())
    expect(screen.getByTestId('engine')).toHaveTextContent('{"letter":8,"skip":20}')
    expect(getGameCompletion).not.toHaveBeenCalled()
  })
})
