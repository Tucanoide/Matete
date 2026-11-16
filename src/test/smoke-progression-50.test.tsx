import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import GameEngine from '../components/game/GameEngine';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import * as actions from '../app/actions/game';
import { toPublicWord } from '../lib/public-word';

// Mock Server Actions
vi.mock('../app/actions/game', () => ({
  getNextWord: vi.fn(),
  startWord: vi.fn(() => Promise.resolve({ ok: true })),
  submitGuess: vi.fn(),
  requestHint: vi.fn(),
  moveToNextLevel: vi.fn(),
  getRandomMessage: vi.fn(() => Promise.resolve('¡Excelente!')),
}));

vi.mock('../app/actions/comments', () => ({
  getWordComments: vi.fn(() => Promise.resolve({ locked: false, comments: [], nextCursor: null, canPost: false, postBlockedReason: 'login_required' })),
  postComment: vi.fn(),
  reportComment: vi.fn(),
  deleteComment: vi.fn(),
}))

vi.mock('../app/actions/challenges', () => ({
  createChallenge: vi.fn(),
}))

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
  signIn: vi.fn(),
}));

// Mock Framer Motion to avoid animation delays in tests
vi.mock('framer-motion', async () => {
  const actual = await vi.importActual<typeof import('framer-motion')>('framer-motion');
  return {
    ...actual,
    motion: {
      ...actual.motion,
      div: ({ children, ...props }: React.HTMLAttributes<HTMLDivElement>) => <div {...props}>{children}</div>,
      button: ({ children, ...props }: React.ButtonHTMLAttributes<HTMLButtonElement>) => <button {...props}>{children}</button>,
    },
    AnimatePresence: ({ children }: { children?: React.ReactNode }) => <>{children}</>,
  };
});

const solved = (i: number, isLevelComplete: boolean) => ({
  correct: true as const,
  solution: i === 1 ? 'TEST' : 'NEXT',
  alreadySolved: false,
  neuronas: 100 + i * 10,
  points: i,
  isLevelComplete,
  levelStats: { solved: i, total: 50, levelId: 1 },
  result: { timeTaken: 10, revealedIndices: [] as number[] },
});

describe('Progression Smoke Test - 50 Words', () => {
  const initialWord = toPublicWord({ id: 100, word: 'TEST', description: 'Prueba inicial' });
  const initialLevel = 1;

  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(actions.getNextWord).mockResolvedValue(
      toPublicWord({ id: 101, word: 'NEXT', description: 'Siguiente palabra de prueba' })
    );
  });

  it('[GAME-11] [PROG-01] should pass to level-up state after 50 words are solved', async () => {
    const user = userEvent.setup();

    render(<GameEngine initialWord={initialWord} initialLevel={initialLevel} />);

    for (let i = 1; i <= 50; i++) {
      const isLastWord = i === 50;

      // El servidor valida y acredita
      vi.mocked(actions.submitGuess).mockResolvedValueOnce(solved(i, isLastWord));

      const wordToType = (i === 1) ? 'TEST' : 'NEXT';
      await user.keyboard(wordToType);

      await waitFor(() => expect(actions.submitGuess).toHaveBeenCalledTimes(i), { timeout: 2000 });

      if (!isLastWord) {
        vi.mocked(actions.getNextWord).mockResolvedValueOnce(
          toPublicWord({ id: 100 + i + 1, word: 'NEXT', description: 'Desc ' + (i + 1) })
        );

        const nextBtn = await screen.findByText(/SIGUIENTE/i);
        await user.click(nextBtn);

        await waitFor(() => expect(screen.queryByText(/SIGUIENTE/i)).not.toBeInTheDocument());
      }
    }

    await waitFor(() => {
      expect(screen.getByText(/¡NIVEL 1 COMPLETADO!/i)).toBeInTheDocument();
    }, { timeout: 10000 });

    expect(actions.submitGuess).toHaveBeenCalledTimes(50);
  }, 120000);
});
