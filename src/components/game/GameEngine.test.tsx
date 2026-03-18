import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import '@testing-library/jest-dom'
import GameEngine from './GameEngine'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import { getRandomWordFromLevel, recordWordSolved, useHint, moveToNextLevel } from '@/app/actions/game'

// Mocking Next-Auth
vi.mock('next-auth/react', () => ({
  __esModule: true,
  useSession: vi.fn(() => ({ data: null, status: 'unauthenticated', update: vi.fn(() => Promise.resolve()) })),
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
    getRandomMessage: vi.fn(() => Promise.resolve('¡Bien hecho!')),
  };
})

// Mocking Sound Manager
vi.mock('@/lib/sounds', () => ({
  soundManager: {
    play: vi.fn(),
    setEnabled: vi.fn(),
  }
}))

describe('GameEngine Component', () => {
  const initialWord = 'PLATANO'
  const initialDescription = 'Metal precioso negador con cáscara'
  const initialLevel = 1

  let user: ReturnType<typeof userEvent.setup>

  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(recordWordSolved).mockResolvedValue({ success: true, isLevelComplete: false } as any)
    user = userEvent.setup()
  })

  it('should render the initial description and level', () => {
    render(
      <GameEngine 
        initialWord={initialWord} 
        initialDescription={initialDescription} 
        initialLevel={initialLevel} 
        initialWordId={1}
        initialNeedsUsername={false}
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
        initialNeedsUsername={false}
      />
    )

    screen.getByTestId('game-container').focus()
    fireEvent.keyDown(window, { key: 'P' })
    fireEvent.keyDown(window, { key: 'L' })

    await waitFor(() => {
      expect(screen.getByTestId('word-box-0')).toHaveTextContent('P')
      expect(screen.getByTestId('word-box-1')).toHaveTextContent('L')
    })
  })

  it('should show success state and next button when word is correctly guessed', async () => {
    render(
      <GameEngine 
        initialWord={initialWord} 
        initialDescription={initialDescription} 
        initialLevel={initialLevel} 
        initialWordId={1}
        initialNeedsUsername={false}
      />
    )

    // Type the correct word
    screen.getByTestId('game-container').focus()
    for (const char of 'PLATANO') {
      await user.keyboard(char)
    }

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
        initialNeedsUsername={false}
      />
    )

    screen.getByTestId('game-container').focus()
    // Win the game first
    for (const char of 'PLATANO') {
      fireEvent.keyDown(window, { key: char })
    }

    const nextBtn = await screen.findByRole('button', { name: /SIGUIENTE/i })
    await user.click(nextBtn)

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
        initialNeedsUsername={false}
      />
    )

    fireEvent.keyDown(window, { key: 'P' })
    await waitFor(() => expect(screen.getByTestId('word-box-0')).toHaveTextContent('P'))
    
    fireEvent.keyDown(window, { key: 'Backspace' })
    await waitFor(() => expect(screen.getByTestId('word-box-0')).toHaveTextContent(''))
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
        initialNeedsUsername={false}
      />
    )

    // Type the correct word
    screen.getByTestId('game-container').focus()
    for (const char of 'PLATANO') {
      await user.keyboard(char)
    }

    // LevelUpModal appears after a delay
    await waitFor(() => {
      expect(screen.getByText(/NIVEL 1 COMPLETADO/i)).toBeInTheDocument()
    }, { timeout: 10000 })

    expect(screen.getByRole('button', { name: /ACCEDER AL NIVEL 2/i })).toBeInTheDocument()
  })

  it('should call moveToNextLevel and update state when level up modal button is clicked', async () => {
    // Define next word for level 2
    const nextWordData = {
      word: 'NIVEL2',
      id: 2,
      description: 'Definición nivel 2',
      difficulty: 'HARD',
      type: 'WORD',
      levelId: 2
    };
    
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
        initialNeedsUsername={false}
      />
    )

    screen.getByTestId('game-container').focus()
    // Complete the level
    for (const char of 'PLATANO') {
      fireEvent.keyDown(window, { key: char })
    }

    // Wait for the modal and button to appear (handling the delay in GameEngine)
    const nextLevelBtn = await screen.findByRole('button', { name: /ACCEDER AL NIVEL 2/i }, { timeout: 10000 })
    await user.click(nextLevelBtn)
    
    // Wait for the modal to be removed from the DOM (handling AnimatePresence)
    await waitFor(() => {
      expect(screen.queryByText(/NIVEL 1 COMPLETADO/i)).not.toBeInTheDocument()
    }, { timeout: 10000 })

    // Wait for the UI to update to Level 2
    // Wait for the UI to update to Level 2
    await waitFor(async () => {
      // PROOF: The level display must show "Nivel 2"
      const levelTexts = await screen.findAllByText(/Nivel 2/i)
      expect(levelTexts.length).toBeGreaterThan(0)
      
      // AND the next word description must be visible
      const descTexts = await screen.findAllByText(/Definición nivel 2/i)
      expect(descTexts.length).toBeGreaterThan(0)
    }, { timeout: 15000 })
  }, 30000)

  it('should reset cursor to 0 and allow typing from start after clearing word', async () => {
    render(
      <GameEngine 
        initialWord={initialWord} 
        initialDescription={initialDescription} 
        initialLevel={initialLevel} 
        initialWordId={1}
        initialNeedsUsername={false}
      />
    )

    screen.getByTestId('game-container').focus()
    // Type one letter
    fireEvent.keyDown(window, { key: 'P' })
    await waitFor(() => expect(screen.getByTestId('word-box-0')).toHaveTextContent('P'), { timeout: 5000 })
    
    // Type another
    fireEvent.keyDown(window, { key: 'L' })
    await waitFor(() => expect(screen.getByTestId('word-box-1')).toHaveTextContent('L'), { timeout: 5000 })
    
    // Click clear button (Trash2 icon)
    const clearBtn = await screen.findByTitle(/Limpiar palabra/i)
    await user.click(clearBtn)
    
    // Regain focus after clear
    screen.getByTestId('game-container').focus()

    // Typing 'M' should now appear at the first box
    fireEvent.keyDown(window, { key: 'M' })
    
    await waitFor(() => {
      expect(screen.getByTestId('word-box-0')).toHaveTextContent('M')
    }, { timeout: 10000 })
  })

  it('should deduct neuronas when buying a letter hint', async () => {
    vi.mocked(useHint).mockResolvedValueOnce({
      success: true,
      remainingNeuronas: 45,
      revealedIndices: [0], // reveals first letter
    })

    render(
      <GameEngine
        initialWord={initialWord}
        initialDescription={initialDescription}
        initialLevel={initialLevel}
        initialWordId={1}
        initialNeedsUsername={false}
        initialNeuronas={50}
      />
    )

    // Initially should show 50 neuronas
    expect(screen.getByTestId('neuronas-count')).toHaveTextContent('50N')

    // Click the hint (letter) button
    const hintBtn = screen.getByTitle('Pedir letra')
    await user.click(hintBtn)

    // After hint, neuronas should drop to 45
    await waitFor(() => {
      expect(screen.getByTestId('neuronas-count')).toHaveTextContent('45N')
    }, { timeout: 5000 })

    expect(useHint).toHaveBeenCalledWith(1, 'letter')
  })

  it('should deduct neuronas when skipping a word', async () => {
    const nextWordData = {
      word: 'MORALEJA',
      description: 'Nueva palabra',
      id: 99,
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
    vi.mocked(useHint).mockResolvedValueOnce({
      success: true,
      remainingNeuronas: 35,
      revealedIndices: undefined,
    })
    vi.mocked(getRandomWordFromLevel).mockResolvedValueOnce(nextWordData as any)

    render(
      <GameEngine
        initialWord={initialWord}
        initialDescription={initialDescription}
        initialLevel={initialLevel}
        initialWordId={1}
        initialNeedsUsername={false}
        initialNeuronas={50}
      />
    )

    // Initially should show 50 neuronas
    expect(screen.getByTestId('neuronas-count')).toHaveTextContent('50N')

    // Click the skip button
    const skipBtn = screen.getByTitle('Saltar palabra')
    await user.click(skipBtn)

    // After skip, neuronas should drop to 35
    await waitFor(() => {
      expect(screen.getByTestId('neuronas-count')).toHaveTextContent('35N')
    }, { timeout: 5000 })

    expect(useHint).toHaveBeenCalledWith(1, 'skip')
  })

  it('should trigger next word when Enter is pressed after a correct guess', async () => {
    const nextWordData = { 
      word: 'MORALEJA', 
      description: 'Nueva definición', 
      id: 576, 
      levelId: 1
    }
    vi.mocked(getRandomWordFromLevel).mockResolvedValueOnce(nextWordData as any)

    render(
      <GameEngine 
        initialWord={initialWord} 
        initialDescription={initialDescription} 
        initialLevel={initialLevel} 
        initialWordId={1}
        initialNeedsUsername={false}
      />
    )

    // Win the game first by typing correctly
    screen.getByTestId('game-container').focus()
    for (const char of 'PLATANO') {
      fireEvent.keyDown(window, { key: char })
    }

    // Wait for victory state
    await screen.findByRole('button', { name: /SIGUIENTE/i })

    // Press Enter to go to next word
    fireEvent.keyDown(window, { key: 'Enter' })

    await waitFor(() => {
      expect(getRandomWordFromLevel).toHaveBeenCalledWith(1)
    })
    
    // Verify next word is rendered
    expect(await screen.findByText(/Nueva definición/i)).toBeInTheDocument()
  })

  it('should handle backspace at the end of a full word correctly', async () => {
    render(
      <GameEngine 
        initialWord={'PLATANO'} 
        initialDescription={'Test'} 
        initialLevel={1} 
        initialWordId={1}
        initialNeedsUsername={false}
      />
    )

    // Type a full word (wrong last letter)
    screen.getByTestId('game-container').focus()
    for (const char of 'PLATANE') {
       fireEvent.keyDown(window, { key: char })
    }
    
    // Wait for the last letter to appear
    await waitFor(() => expect(screen.getByTestId('word-box-6')).toHaveTextContent('E'))
    
    // Press Backspace. Currently this fails because cursor is at 7 and it doesn't move.
    fireEvent.keyDown(window, { key: 'Backspace' })
    
    await waitFor(() => {
        expect(screen.getByTestId('word-box-6')).toHaveTextContent('')
    })
  })
})
