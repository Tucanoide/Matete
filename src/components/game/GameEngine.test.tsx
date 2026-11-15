import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import '@testing-library/jest-dom'
import GameEngine from './GameEngine'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import { getNextWord, submitGuess, useHint, moveToNextLevel, startWord } from '@/app/actions/game'
import { toPublicWord } from '@/lib/public-word'

// El GameEngine ya no conoce la solución: recibe una PublicWord y el servidor (submitGuess)
// decide si el intento es correcto. Acá submitGuess se simula con una tabla de soluciones.

const solutions = vi.hoisted(() => new Map<number, string>())

vi.mock('next-auth/react', () => ({
  __esModule: true,
  useSession: vi.fn(() => ({ data: null, status: 'unauthenticated', update: vi.fn(() => Promise.resolve()) })),
  signOut: vi.fn(),
  signIn: vi.fn(),
}))

vi.mock('@/app/actions/game', () => ({
  __esModule: true,
  getNextWord: vi.fn(),
  startWord: vi.fn(() => Promise.resolve({ ok: true })),
  submitGuess: vi.fn(),
  useHint: vi.fn(),
  moveToNextLevel: vi.fn(),
  getInitialGameState: vi.fn(),
  getRandomMessage: vi.fn(() => Promise.resolve('¡Bien hecho!')),
}))

vi.mock('@/app/actions/comments', () => ({
  getWordComments: vi.fn(() => Promise.resolve({ locked: false, comments: [], nextCursor: null, canPost: false, postBlockedReason: 'login_required' })),
  postComment: vi.fn(),
  reportComment: vi.fn(),
  deleteComment: vi.fn(),
}))

vi.mock('@/app/actions/challenges', () => ({
  createChallenge: vi.fn(),
}))

vi.mock('@/lib/sounds', () => ({
  soundManager: {
    play: vi.fn(),
    setEnabled: vi.fn(),
  },
}))

const correct = (solution: string, extra: Record<string, unknown> = {}) => ({
  correct: true as const,
  solution,
  alreadySolved: false,
  neuronas: 60,
  points: 1,
  isLevelComplete: false,
  levelStats: { solved: 1, total: 5, levelId: 1 },
  result: { timeTaken: 42, revealedIndices: [] as number[] },
  ...extra,
})

/** Arma la PublicWord igual que el servidor y registra la solución para el submitGuess simulado. */
function word(id: number, solution: string, description: string) {
  solutions.set(id, solution.toUpperCase())
  return toPublicWord({ id, word: solution, description })
}

describe('GameEngine Component', () => {
  const initialDescription = 'Metal precioso negador con cáscara'
  const initialLevel = 1
  let initialWord: ReturnType<typeof word>
  let user: ReturnType<typeof userEvent.setup>

  beforeEach(() => {
    vi.clearAllMocks()
    solutions.clear()
    initialWord = word(1, 'PLATANO', initialDescription)
    vi.mocked(submitGuess).mockImplementation(async (id: number, guess: string) =>
      solutions.get(id) === guess.toUpperCase() ? correct(guess.toUpperCase()) : { correct: false as const }
    )
    user = userEvent.setup()
  })

  it('should register the word delivery on mount (startWord)', async () => {
    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} />)
    await waitFor(() => expect(startWord).toHaveBeenCalledWith(1))
  })

  it('never receives the solution: PublicWord has no word field', () => {
    expect(JSON.stringify(initialWord)).not.toContain('PLATANO')
  })

  it('should render the initial description and level', () => {
    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} initialNeedsUsername={false} />)

    expect(screen.getByText(new RegExp(initialDescription, 'i'))).toBeInTheDocument()
    expect(screen.getByText(/Nivel 1/i)).toBeInTheDocument()
  })

  it('should update character boxes when user types', async () => {
    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} initialNeedsUsername={false} />)

    screen.getByTestId('game-container').focus()
    fireEvent.keyDown(window, { key: 'P' })
    fireEvent.keyDown(window, { key: 'L' })

    await waitFor(() => {
      expect(screen.getByTestId('word-box-0')).toHaveTextContent('P')
      expect(screen.getByTestId('word-box-1')).toHaveTextContent('L')
    })
  })

  it('should show success state and next button when word is correctly guessed', async () => {
    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} initialNeedsUsername={false} />)

    screen.getByTestId('game-container').focus()
    for (const char of 'PLATANO') {
      await user.keyboard(char)
    }

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /SIGUIENTE/i })).toBeInTheDocument()
    })
    expect(submitGuess).toHaveBeenCalledWith(1, 'PLATANO')
  })

  it('should show the fail message when the server rejects the guess', async () => {
    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} />)

    screen.getByTestId('game-container').focus()
    for (const char of 'PLATANE') {
      fireEvent.keyDown(window, { key: char })
    }

    await waitFor(() => expect(submitGuess).toHaveBeenCalledWith(1, 'PLATANE'))
    expect(await screen.findByText(/ERROR DE TRANSMISIÓN/i)).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: /SIGUIENTE/i })).not.toBeInTheDocument()
  })

  it('should fetch new word when next button is clicked', async () => {
    vi.mocked(getNextWord).mockResolvedValueOnce(word(576, 'MORALEJA', 'Nueva definición'))

    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} initialNeedsUsername={false} />)

    screen.getByTestId('game-container').focus()
    for (const char of 'PLATANO') {
      fireEvent.keyDown(window, { key: char })
    }

    const nextBtn = await screen.findByRole('button', { name: /SIGUIENTE/i })
    await user.click(nextBtn)

    await waitFor(() => {
      expect(getNextWord).toHaveBeenCalledWith(1)
    })

    const nextDescription = await screen.findByText(/Nueva definición/i)
    expect(nextDescription).toBeInTheDocument()
    expect(screen.getByTestId('word-box-7')).toBeInTheDocument() // MORALEJA: 8 casilleros
  })

  it('should handle backspace correctly', async () => {
    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} initialNeedsUsername={false} />)

    fireEvent.keyDown(window, { key: 'P' })
    await waitFor(() => expect(screen.getByTestId('word-box-0')).toHaveTextContent('P'))

    fireEvent.keyDown(window, { key: 'Backspace' })
    await waitFor(() => expect(screen.getByTestId('word-box-0')).toHaveTextContent(''))
  })

  it('should show LevelUpModal when current level is completed', async () => {
    const levelStats = { solved: 5, total: 5, levelId: 1 }
    vi.mocked(submitGuess).mockResolvedValueOnce(correct('PLATANO', { isLevelComplete: true, levelStats }))

    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} initialNeedsUsername={false} />)

    screen.getByTestId('game-container').focus()
    for (const char of 'PLATANO') {
      await user.keyboard(char)
    }

    await waitFor(() => {
      expect(screen.getByText(/NIVEL 1 COMPLETADO/i)).toBeInTheDocument()
    }, { timeout: 10000 })

    expect(screen.getByRole('button', { name: /ACCEDER AL NIVEL 2/i })).toBeInTheDocument()
  })

  it('should call moveToNextLevel and update state when level up modal button is clicked', async () => {
    vi.mocked(submitGuess).mockResolvedValueOnce(
      correct('PLATANO', { isLevelComplete: true, levelStats: { solved: 5, total: 5, levelId: 1 } })
    )
    vi.mocked(moveToNextLevel).mockResolvedValueOnce({
      success: true,
      nextLevelId: 2,
      nextWord: word(2, 'NIVELDOS', 'Definición nivel 2'),
      neuronas: 60,
    })

    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} initialNeedsUsername={false} />)

    screen.getByTestId('game-container').focus()
    for (const char of 'PLATANO') {
      fireEvent.keyDown(window, { key: char })
    }

    const nextLevelBtn = await screen.findByRole('button', { name: /ACCEDER AL NIVEL 2/i }, { timeout: 10000 })
    await user.click(nextLevelBtn)

    await waitFor(() => {
      expect(screen.queryByText(/NIVEL 1 COMPLETADO/i)).not.toBeInTheDocument()
    }, { timeout: 10000 })

    await waitFor(async () => {
      const levelTexts = await screen.findAllByText(/Nivel 2/i)
      expect(levelTexts.length).toBeGreaterThan(0)

      const descTexts = await screen.findAllByText(/Definición nivel 2/i)
      expect(descTexts.length).toBeGreaterThan(0)
    }, { timeout: 15000 })
  }, 30000)

  it('should reset cursor to 0 and allow typing from start after clearing word', async () => {
    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} initialNeedsUsername={false} />)

    screen.getByTestId('game-container').focus()
    fireEvent.keyDown(window, { key: 'P' })
    await waitFor(() => expect(screen.getByTestId('word-box-0')).toHaveTextContent('P'), { timeout: 5000 })

    fireEvent.keyDown(window, { key: 'L' })
    await waitFor(() => expect(screen.getByTestId('word-box-1')).toHaveTextContent('L'), { timeout: 5000 })

    const clearBtn = await screen.findByTitle(/Limpiar palabra/i)
    await user.click(clearBtn)

    screen.getByTestId('game-container').focus()

    fireEvent.keyDown(window, { key: 'M' })

    await waitFor(() => {
      expect(screen.getByTestId('word-box-0')).toHaveTextContent('M')
    }, { timeout: 10000 })
  })

  it('should deduct neuronas and show the revealed letter when buying a letter hint', async () => {
    vi.mocked(useHint).mockResolvedValueOnce({
      success: true,
      remainingNeuronas: 45,
      revealed: { index: 0, letter: 'P' },
    })

    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} initialNeedsUsername={false} initialNeuronas={50} />)

    expect(screen.getByTestId('neuronas-count')).toHaveTextContent('50N')

    const hintBtn = screen.getByTitle('Pedir letra')
    await user.click(hintBtn)

    await waitFor(() => {
      expect(screen.getByTestId('neuronas-count')).toHaveTextContent('45N')
    }, { timeout: 5000 })
    expect(screen.getByTestId('word-box-0')).toHaveTextContent('P')

    expect(useHint).toHaveBeenCalledWith(1, 'letter')
  })

  it('should render letters already revealed by previous hints', () => {
    const withHints = toPublicWord({ id: 1, word: 'PLATANO', description: 'x' }, [0, 3])
    render(<GameEngine initialWord={withHints} initialLevel={1} />)
    expect(screen.getByTestId('word-box-0')).toHaveTextContent('P')
    expect(screen.getByTestId('word-box-3')).toHaveTextContent('T')
    expect(screen.getByTestId('word-box-1')).toHaveTextContent('')
  })

  it('should deduct neuronas when skipping a word', async () => {
    vi.mocked(useHint).mockResolvedValueOnce({
      success: true,
      remainingNeuronas: 35,
      isLevelComplete: false,
    })
    vi.mocked(getNextWord).mockResolvedValueOnce(word(99, 'MORALEJA', 'Nueva palabra'))

    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} initialNeedsUsername={false} initialNeuronas={50} />)

    expect(screen.getByTestId('neuronas-count')).toHaveTextContent('50N')

    const skipBtn = screen.getByTitle('Saltar palabra')
    await user.click(skipBtn)

    await waitFor(() => {
      expect(screen.getByTestId('neuronas-count')).toHaveTextContent('35N')
    }, { timeout: 5000 })

    expect(useHint).toHaveBeenCalledWith(1, 'skip')
    expect(await screen.findByText(/Nueva palabra/i)).toBeInTheDocument()
  })

  it('should trigger next word when Enter is pressed after a correct guess', async () => {
    vi.mocked(getNextWord).mockResolvedValueOnce(word(576, 'MORALEJA', 'Nueva definición'))

    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} initialNeedsUsername={false} />)

    screen.getByTestId('game-container').focus()
    for (const char of 'PLATANO') {
      fireEvent.keyDown(window, { key: char })
    }

    await screen.findByRole('button', { name: /SIGUIENTE/i })

    fireEvent.keyDown(window, { key: 'Enter' })

    await waitFor(() => {
      expect(getNextWord).toHaveBeenCalledWith(1)
    })

    expect(await screen.findByText(/Nueva definición/i)).toBeInTheDocument()
  })

  it('should handle backspace at the end of a full word correctly', async () => {
    render(<GameEngine initialWord={word(1, 'PLATANO', 'Test')} initialLevel={1} initialNeedsUsername={false} />)

    screen.getByTestId('game-container').focus()
    for (const char of 'PLATANE') {
      fireEvent.keyDown(window, { key: char })
    }

    await waitFor(() => expect(screen.getByTestId('word-box-6')).toHaveTextContent('E'))

    fireEvent.keyDown(window, { key: 'Backspace' })

    await waitFor(() => {
      expect(screen.getByTestId('word-box-6')).toHaveTextContent('')
    })
  })

  it('should toggle between Pool Mode and Keyboard Mode', async () => {
    render(<GameEngine initialWord={word(1, 'TEST', 'Test')} initialLevel={1} />)

    const toggleBtn = screen.getByTitle('Cambiar modo de entrada')

    await user.click(toggleBtn)
    expect(screen.getByRole('textbox', { hidden: true })).toBeInTheDocument()

    await user.click(toggleBtn)
    expect(screen.queryByRole('textbox', { hidden: true })).not.toBeInTheDocument()
  })

  it('should render the 14-letter pool sent by the server', () => {
    const w = word(1, 'TEST', 'Test')
    render(<GameEngine initialWord={w} initialLevel={1} />)
    for (const ch of Array.from(new Set(w.pool))) {
      expect(screen.getAllByRole('button', { name: ch }).length).toBeGreaterThan(0)
    }
  })

  it('should disable and enable sound via the toggle button', async () => {
    render(<GameEngine initialWord={word(1, 'TEST', 'Test')} initialLevel={1} />)

    const buttons = screen.getAllByRole('button')
    const soundToggle = buttons.find(b =>
      b.querySelector('svg')?.classList.contains('lucide-volume') ||
      b.querySelector('svg')?.classList.contains('lucide-volume-x') ||
      b.innerHTML.includes('lucide-volume')
    )

    if (soundToggle) {
      await user.click(soundToggle)
    }
  })

  it('should clear all letters when the Clear All button is clicked', async () => {
    render(<GameEngine initialWord={word(1, 'TEST', 'Test')} initialLevel={1} />)

    screen.getByTestId('game-container').focus()
    await user.keyboard('TES')

    await waitFor(() => expect(screen.getByTestId('word-box-2')).toHaveTextContent('S'))

    const clearBtn = screen.getByTitle('Limpiar palabra')
    await user.click(clearBtn)

    await waitFor(() => {
      expect(screen.getByTestId('word-box-0')).toHaveTextContent('')
      expect(screen.getByTestId('word-box-1')).toHaveTextContent('')
      expect(screen.getByTestId('word-box-2')).toHaveTextContent('')
    })
  })

  describe('[SOC-03] panel social tras acertar (002-social)', () => {
    async function solve() {
      render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} />)
      for (const char of 'PLATANO') fireEvent.keyDown(window, { key: char })
      await screen.findByRole('button', { name: /SIGUIENTE/i })
    }

    it('no se muestra antes de acertar', () => {
      render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} />)
      expect(screen.queryByRole('button', { name: /COMPARTIR/i })).not.toBeInTheDocument()
    })

    it('muestra Compartir, Desafiar y Comentarios; el texto usa el resultado del servidor y no la palabra', async () => {
      const writeText = vi.fn(() => Promise.resolve())
      Object.defineProperty(navigator, 'clipboard', { value: { writeText }, configurable: true })
      Object.defineProperty(navigator, 'share', { value: undefined, configurable: true })

      await solve()
      expect(screen.getByRole('button', { name: /INGRESÁ PARA DESAFIAR/i })).toBeInTheDocument()
      expect(screen.getByRole('button', { name: /COMENTARIOS/i })).toBeInTheDocument()

      await user.click(screen.getByRole('button', { name: /COMPARTIR/i }))
      await waitFor(() => expect(writeText).toHaveBeenCalled())
      const text = (writeText.mock.calls[0] as unknown as [string])[0]
      expect(text).toContain('Nivel 1')
      expect(text).toContain('🟩🟩🟩🟩🟩🟩🟩')
      expect(text).toContain('0:42')
      expect(text).not.toMatch(/PLATANO/i)
      expect(await screen.findByText(/¡COPIADO!/)).toBeInTheDocument()
    })

    it('Enter dentro del panel social no pasa a la siguiente palabra', async () => {
      await solve()
      await user.click(screen.getByRole('button', { name: /COMENTARIOS/i }))
      const panelButton = screen.getByRole('button', { name: /OCULTAR COMENTARIOS/i })
      fireEvent.keyDown(panelButton, { key: 'Enter' })
      expect(getNextWord).not.toHaveBeenCalled()
      // Fuera del panel, Enter sigue funcionando.
      fireEvent.keyDown(window, { key: 'Enter' })
      await waitFor(() => expect(getNextWord).toHaveBeenCalled())
    })
  })
})
