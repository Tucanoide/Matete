import { render, screen, waitFor, act, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import GameEngine from '../components/game/GameEngine';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import * as actions from '../app/actions/game';

// Mock Server Actions
vi.mock('../app/actions/game', () => ({
  getRandomWordFromLevel: vi.fn(),
  recordWordSolved: vi.fn(),
  useHint: vi.fn(),
  moveToNextLevel: vi.fn(),
  getRandomMessage: vi.fn(() => Promise.resolve('¡Excelente!')),
}));

// Mock Sound Manager
vi.mock('../lib/sounds', () => ({
  soundManager: {
    play: vi.fn(),
    setEnabled: vi.fn(),
  },
}));

// Mock Next-Auth
vi.mock('next-auth/react', () => ({
  useSession: vi.fn(() => ({ 
    data: { user: { name: 'Test Player', neuronas: 100 } }, 
    update: vi.fn() 
  })),
  signOut: vi.fn(),
}));

// Mock Framer Motion to avoid animation delays in tests
vi.mock('framer-motion', async () => {
  const actual = await vi.importActual('framer-motion') as any;
  return {
    ...actual,
    motion: {
      ...actual.motion,
      div: ({ children, ...props }: any) => <div {...props}>{children}</div>,
      button: ({ children, ...props }: any) => <button {...props}>{children}</button>,
    },
    AnimatePresence: ({ children }: any) => <>{children}</>,
  };
});

describe('Progression Smoke Test - 50 Words', () => {
  const initialWord = 'TEST';
  const initialDescription = 'Prueba inicial';
  const initialLevel = 1;
  const initialWordId = 100;

  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(actions.recordWordSolved).mockResolvedValue({
      success: true,
      isLevelComplete: false,
      levelStats: { solved: 1, total: 50, levelId: 1 },
      updatedUser: { id: 'u1', neuronas: 110 } as any
    });

    vi.mocked(actions.getRandomWordFromLevel).mockResolvedValue({
      id: 101,
      word: 'NEXT',
      description: 'Siguiente palabra de prueba',
      levelId: 1
    } as any);
  });

  it('should pass to level-up state after 50 words are solved', async () => {
    const user = userEvent.setup();
    
    render(
      <GameEngine 
        initialWord={initialWord} 
        initialDescription={initialDescription} 
        initialLevel={initialLevel} 
        initialWordId={initialWordId}
      />
    );

    for (let i = 1; i <= 50; i++) {
      const isLastWord = i === 50;
      
      // Mock the solve response
      vi.mocked(actions.recordWordSolved).mockResolvedValueOnce({
        success: true,
        isLevelComplete: isLastWord,
        levelStats: { solved: i, total: 50, levelId: 1 },
        updatedUser: { id: 'u1', neuronas: 100 + i*10 } as any
      });

      // Type the correct word
      const wordToType = (i === 1) ? initialWord : 'NEXT';
      await user.keyboard(wordToType);

      // WAIT for the action to be triggered and completed
      await waitFor(() => expect(actions.recordWordSolved).toHaveBeenCalledTimes(i), { timeout: 2000 });

      if (!isLastWord) {
        // Setup next word mock for the NEXT iteration
        vi.mocked(actions.getRandomWordFromLevel).mockResolvedValueOnce({
          id: 100 + i + 1,
          word: 'NEXT',
          description: 'Desc ' + (i+1),
          levelId: 1
        } as any);

        // Find "SIGUIENTE" button and click it to move to next word
        const nextBtn = await screen.findByText(/SIGUIENTE/i);
        await user.click(nextBtn);
        
        // Wait for the button to disappear
        await waitFor(() => expect(screen.queryByText(/SIGUIENTE/i)).not.toBeInTheDocument());
      }
    }

    // Finally verify the level complete modal appears
    await waitFor(() => {
      expect(screen.getByText(/¡NIVEL 1 COMPLETADO!/i)).toBeInTheDocument();
    }, { timeout: 10000 });
    
    expect(actions.recordWordSolved).toHaveBeenCalledTimes(50);
  }, 120000); // 2 minutes for 50 words simulation
});
