import { render, screen, within } from '@testing-library/react'
import '@testing-library/jest-dom'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import JourneyPage from './page'
import { getMyJourney, type Journey } from '@/app/actions/journey'

vi.mock('@/app/actions/journey', () => ({ getMyJourney: vi.fn() }))
vi.mock('@/components/layout/PageShell', () => ({ default: ({ children }: { children: React.ReactNode }) => <main>{children}</main> }))
vi.mock('next-auth/react', () => ({ signIn: vi.fn() }))

const base: Journey = {
  isGuest: true, username: null, levelId: 2, points: 3, neuronas: 65, rank: null,
  guessed: 3, skipped: 1, inProgress: 1, totalTime: 90, avgTime: 30, bestTime: 10,
  hintsUsed: 3, solvedWithoutHints: 1,
  levels: [
    { levelId: 1, name: 'Nivel 1', totalWords: 3, guessed: 2, skipped: 1, avgTime: 20, hints: 1 },
    { levelId: 2, name: 'Nivel 2', totalWords: 2, guessed: 1, skipped: 0, avgTime: null, hints: 2 },
  ],
  challenges: null,
}

beforeEach(() => vi.clearAllMocks())

describe('[JOUR-04] página Mi recorrido', () => {
  it('invitado: resumen, por nivel, invitación a Google y sin desafíos ni ranking', async () => {
    vi.mocked(getMyJourney).mockResolvedValue({ ok: true, journey: base })
    render(await JourneyPage())
    const resumen = screen.getByRole('region', { name: 'Resumen' })
    expect(within(resumen).getByText('Acertadas').parentElement?.parentElement).toHaveTextContent('3')
    expect(within(resumen).getByText('1:30')).toBeInTheDocument() // tiempo total
    expect(within(resumen).getByText('0:10')).toBeInTheDocument() // mejor tiempo
    expect(screen.getByText('1 de 3 palabras resueltas sin pistas.')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /ENTRAR CON GOOGLE/ })).toBeInTheDocument()
    expect(screen.queryByText('Ranking')).not.toBeInTheDocument()
    expect(screen.queryByRole('region', { name: 'Desafíos' })).not.toBeInTheDocument()

    const niveles = screen.getByRole('region', { name: 'Por nivel' })
    expect(within(niveles).getByText('2/3 acertadas')).toBeInTheDocument()
    expect(within(niveles).getByText(/promedio —/)).toBeInTheDocument() // nivel 2 sin tiempos
    expect(within(niveles).getByText('1 salteada')).toBeInTheDocument()
    expect(within(niveles).getByText(/· 1 pista$/)).toBeInTheDocument()
    expect(within(niveles).getByText('ACTUAL')).toBeInTheDocument()
    expect(within(niveles).getAllByRole('progressbar')).toHaveLength(2)
  })

  it('Google con alias: ranking y desafíos, sin invitación', async () => {
    vi.mocked(getMyJourney).mockResolvedValue({
      ok: true,
      journey: { ...base, isGuest: false, username: 'yo', rank: 4, challenges: { created: 2, played: 3, won: 1 } },
    })
    render(await JourneyPage())
    expect(screen.getByText('#4')).toBeInTheDocument()
    const d = screen.getByRole('region', { name: 'Desafíos' })
    expect(within(d).getByText('Ganados').parentElement?.parentElement).toHaveTextContent('1')
    expect(screen.queryByRole('button', { name: /ENTRAR CON GOOGLE/ })).not.toBeInTheDocument()
  })

  it('sin jugador: invita a empezar', async () => {
    vi.mocked(getMyJourney).mockResolvedValue({ ok: false, error: 'no_player' })
    render(await JourneyPage())
    expect(screen.getByRole('link', { name: 'EMPEZAR A JUGAR' })).toHaveAttribute('href', '/')
  })
})
