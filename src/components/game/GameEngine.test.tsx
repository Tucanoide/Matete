import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import '@testing-library/jest-dom'
import GameEngine from './GameEngine'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import { getRandomWordFromLevel, recordWordSolved, useHint, moveToNextLevel } from '@/app/actions/game'

// Mocking Next-Auth
vi.mock('next-auth/react', () => ({
  __esModule: true,
  useSession: vi.fn(() => ({ data: null, status: 'unauthenticated', update: vi.fn() })),
  signOut: vi.fn(),
}))

// Mocking Server Actions
vi.mock('@/app/actions/game', () => {
  return {
    __esModule: true,
    getRandomWordFromLevel: vi.fn(),
    getLevels: vi.fn(),
    getWordsByLevel: vi.fn(),
    recordWordSolved: vi.fn(() => Promise.resolve({ success: true, isLevelComplete: false })),
    useHint: vi.fn(),
    moveToNextLevel: vi.fn(),
    getInitialGameState: vi.fn(),
  };
})

describe('GameEngine Component', () => {
  const initialWord = 'PLATANO'
  const initialDescription = 'Metal precioso negador con cáscara'
  const initialLevel = 1

  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(recordWordSolved).mockResolvedValue({ success: true, isLevelComplete: false } as any)
  })

  it('should render the initial description and level', () => {
    render(
      <GameEngine 
        initialWord={initialWord} 
        initialDescription={initialDescription} 
        initialLevel={initialLevel} 
        initialWordId={1}
      />
    )
    
    expect(screen.getByText(new RegExp(initialDescription, 'i'))).toBeInTheDocument()
    expect(screen.getByText(/Nivel 1/i)).toBeInTheDocument()
  })

  it('should update character boxes when user types', async () => {
    render(
      <GameEngine 
        initialWord={initialWord} 
        initialDescription={initialDescription} 
        initialLevel={initialLevel} 
        initialWordId={1}
      />
    )

    fireEvent.keyDown(window, { key: 'P' })
    fireEvent.keyDown(window, { key: 'L' })

    await waitFor(() => {
      expect(screen.getByText('P')).toBeInTheDocument()
      expect(screen.getByText('L')).toBeInTheDocument()
    })
  })

  it('should show success state and next button when word is correctly guessed', async () => {
    render(
      <GameEngine 
        initialWord={initialWord} 
        initialDescription={initialDescription} 
        initialLevel={initialLevel} 
        initialWordId={1}
      />
    )

    // Type the correct word
    'PLATANO'.split('').forEach(char => {
      fireEvent.keyDown(window, { key: char })
    })

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /SIGUIENTE/i })).toBeInTheDocument()
    })
  })

  it('should fetch new word when next button is clicked', async () => {
    const nextWordData = { 
      word: 'MORALEJA', 
      description: 'Nueva definición', 
      id: 576, 
      levelId: 1,
      type: null,
      difficulty: 'normal',
      help01: null,
      help02: null,
      help03: null,
      dictionary: null,
      dictionaryWord: null,
      premium: 'N'
    }
    vi.mocked(getRandomWordFromLevel).mockResolvedValueOnce(nextWordData as any)

    render(
      <GameEngine 
        initialWord={initialWord} 
        initialDescription={initialDescription} 
        initialLevel={initialLevel} 
        initialWordId={1}
      />
    )

    // Win the game first
    'PLATANO'.split('').forEach(char => {
      fireEvent.keyDown(window, { key: char })
    })

    const nextBtn = await screen.findByRole('button', { name: /SIGUIENTE/i })
    fireEvent.click(nextBtn)

    await waitFor(() => {
      expect(getRandomWordFromLevel).toHaveBeenCalledWith(1)
    })
    
    // Wait for the next word to be rendered
    const nextDescription = await screen.findByText(/Nueva definición/i)
    expect(nextDescription).toBeInTheDocument()
  })

  it('should handle backspace correctly', async () => {
    render(
      <GameEngine 
        initialWord={initialWord} 
        initialDescription={initialDescription} 
        initialLevel={initialLevel} 
        initialWordId={1}
      />
    )

    fireEvent.keyDown(window, { key: 'P' })
    await waitFor(() => expect(screen.getByText('P')).toBeInTheDocument())
    
    fireEvent.keyDown(window, { key: 'Backspace' })
    await waitFor(() => expect(screen.queryByText('P')).not.toBeInTheDocument())
  })

  it('should show LevelUpModal when current level is completed', async () => {
    // Mock recordWordSolved to return level complete
    const levelStats = { solved: 5, total: 5, levelId: 1 };
    vi.mocked(recordWordSolved).mockResolvedValueOnce({
      success: true,
      isLevelComplete: true,
      levelStats,
      updatedUser: {} as any
    });

    render(
      <GameEngine 
        initialWord={initialWord} 
        initialDescription={initialDescription} 
        initialLevel={initialLevel} 
        initialWordId={1}
      />
    )

    // Type the correct word
    'PLATANO'.split('').forEach(char => {
      fireEvent.keyDown(window, { key: char })
    })

    // LevelUpModal appears after a delay of 1500ms
    await waitFor(() => {
      expect(screen.getByText(/NIVEL 1 COMPLETADO/i)).toBeInTheDocument()
    }, { timeout: 10000 })

    expect(screen.getByRole('button', { name: /ACCEDER AL NIVEL 2/i })).toBeInTheDocument()
  })

  it('should call moveToNextLevel and update state when level up modal button is clicked', async () => {
    const nextWordData = { 
      word: 'NUEVA', 
      description: 'Definición nivel 2', 
      id: 200, 
      levelId: 2 
    }
    
    // Mock the chain of events
    vi.mocked(recordWordSolved).mockResolvedValueOnce({
      success: true,
      isLevelComplete: true,
      levelStats: { solved: 5, total: 5, levelId: 1 },
      updatedUser: {} as any
    });
    
    vi.mocked(moveToNextLevel).mockResolvedValueOnce({
      success: true,
      nextLevelId: 2,
      nextWord: nextWordData as any,
      updatedUser: {} as any
    });

    render(
      <GameEngine 
        initialWord={initialWord} 
        initialDescription={initialDescription} 
        initialLevel={initialLevel} 
        initialWordId={1}
      />
    )

    // Complete the level
    'PLATANO'.split('').forEach(char => {
      fireEvent.keyDown(window, { key: char })
    })

    // Wait for modal and click
    const nextBtn = await screen.findByRole('button', { name: /ACCEDER AL NIVEL 2/i }, { timeout: 10000 })
    fireEvent.click(nextBtn)

    // Wait for the UI to update to Level 2
    await waitFor(() => {
      // PROOF of success: The level display must show "Nivel 2"
      expect(screen.getByText(/Nivel 2/i)).toBeInTheDocument()
      // AND the next word description must be visible
      expect(screen.getByText(/Definición nivel 2/i)).toBeInTheDocument()
    }, { timeout: 10000 })
  }, 20000)
})
