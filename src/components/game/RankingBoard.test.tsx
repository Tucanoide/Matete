import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import '@testing-library/jest-dom'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import RankingBoard from './RankingBoard'
import { getGlobalRanking } from '@/app/actions/game'
import { getFriendsRanking } from '@/app/actions/follows'
import { useSession } from 'next-auth/react'

vi.mock('next-auth/react', () => ({ useSession: vi.fn(), signIn: vi.fn() }))
vi.mock('@/app/actions/game', () => ({ getGlobalRanking: vi.fn(), getLevelRanking: vi.fn(() => Promise.resolve([])) }))
vi.mock('@/app/actions/follows', () => ({
  getFriendsRanking: vi.fn(),
  searchPlayers: vi.fn(() => Promise.resolve({ ok: true, players: [] })),
  followPlayer: vi.fn(() => Promise.resolve({ ok: true })),
  unfollowPlayer: vi.fn(() => Promise.resolve({ ok: true })),
}))

const entry = (extra: Record<string, unknown>) => ({ name: null, username: null, image: null, points: 1, totalTime: 10, neuronas: 50, isMe: false, isFollowed: false, ...extra })
const asSession = (status: 'authenticated' | 'unauthenticated') =>
  vi.mocked(useSession).mockReturnValue({ data: status === 'authenticated' ? { user: {} } : null, status } as never)

beforeEach(() => {
  vi.clearAllMocks()
  asSession('unauthenticated')
})

describe('RankingBoard', () => {
  it('[RANK-05] ranking vacío muestra el mensaje sin errores', async () => {
    vi.mocked(getGlobalRanking).mockResolvedValue([])
    render(<RankingBoard onClose={() => {}} currentLevel={1} />)
    expect(await screen.findByText('NO HAY DATOS EN ESTE SECTOR')).toBeInTheDocument()
  })

  it('[RANK-06] datos incompletos: muestra "Anónimo" y el ícono por defecto', async () => {
    vi.mocked(getGlobalRanking).mockResolvedValue([entry({})] as never)
    render(<RankingBoard onClose={() => {}} currentLevel={1} />)
    expect(await screen.findByText('Anónimo')).toBeInTheDocument()
    expect(screen.queryByRole('img')).not.toBeInTheDocument()
  })

  it('[SOC-23] con sesión: botón Seguir en otros jugadores con alias, no en uno mismo ni sin alias', async () => {
    asSession('authenticated')
    vi.mocked(getGlobalRanking).mockResolvedValue([
      entry({ username: 'ana', isFollowed: true }),
      entry({ username: 'yo', isMe: true }),
      entry({ name: 'Sin Alias' }),
      entry({ username: 'beto' }),
    ] as never)
    render(<RankingBoard onClose={() => {}} currentLevel={1} />)
    expect(await screen.findByRole('button', { name: 'Dejar de seguir a ana' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Seguir a beto' })).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: /seguir a yo/i })).not.toBeInTheDocument()
    expect(screen.getAllByRole('button', { name: /seguir a/i })).toHaveLength(2)
  })

  it('[SOC-23] sin sesión: no hay botones Seguir y la pestaña Amigos invita a ingresar', async () => {
    vi.mocked(getGlobalRanking).mockResolvedValue([entry({ username: 'ana' })] as never)
    vi.mocked(getFriendsRanking).mockResolvedValue({ ok: false, error: 'login_required' })
    render(<RankingBoard onClose={() => {}} currentLevel={1} />)
    await screen.findByText('ana')
    expect(screen.queryByRole('button', { name: /seguir a/i })).not.toBeInTheDocument()
    await userEvent.click(screen.getByRole('button', { name: 'Amigos' }))
    expect(await screen.findByRole('button', { name: 'INGRESAR CON GOOGLE' })).toBeInTheDocument()
  })

  it('[SOC-23] pestaña Amigos: lista al jugador y sus seguidos con su fila marcada', async () => {
    asSession('authenticated')
    vi.mocked(getGlobalRanking).mockResolvedValue([])
    vi.mocked(getFriendsRanking).mockResolvedValue({
      ok: true,
      entries: [
        { username: 'ana', image: null, points: 9, totalTime: 30, isMe: false },
        { username: 'yo', image: null, points: 2, totalTime: 60, isMe: true },
      ],
    })
    render(<RankingBoard onClose={() => {}} currentLevel={1} />)
    await userEvent.click(screen.getByRole('button', { name: 'Amigos' }))
    expect(await screen.findByText('@ana')).toBeInTheDocument()
    expect(screen.getByText('(VOS)')).toBeInTheDocument()
    await waitFor(() => expect(screen.getAllByRole('button', { name: /seguir a/i })).toHaveLength(1))
  })

  it('[RANK-07] una fila sin tiempo muestra 0:00, no NaN', async () => {
    vi.mocked(getGlobalRanking).mockResolvedValue([entry({ username: 'ana', totalTime: undefined })] as never)
    render(<RankingBoard onClose={() => {}} currentLevel={1} />)
    expect(await screen.findByText('ana')).toBeInTheDocument()
    expect(document.body.textContent).not.toMatch(/NaN/)
  })
})
