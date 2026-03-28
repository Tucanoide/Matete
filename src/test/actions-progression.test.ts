import { describe, it, expect, vi, beforeEach } from 'vitest';
import { recordWordSolved } from '../app/actions/game';
import prisma from '@/lib/prisma';
import { cookies } from 'next/headers';
import { getServerSession } from 'next-auth';

// Mock Prisma
vi.mock('@/lib/prisma', () => ({
  default: {
    user: {
      findUnique: vi.fn(),
      update: vi.fn(),
    },
    word: {
      findUnique: vi.fn(),
      count: vi.fn(),
    },
    progress: {
      upsert: vi.fn(),
      count: vi.fn(),
    },
    setting: {
        findMany: vi.fn(() => Promise.resolve([])),
    },
  },
}));

// Mock Next.js headers/cookies
vi.mock('next/headers', () => ({
  cookies: vi.fn(),
}));

// Mock Next-Auth
vi.mock('next-auth', () => ({
  getServerSession: vi.fn(),
}));

describe('Game Business Logic - 50 Words Progression', () => {
    const mockUser = { id: 'test-user-id', neuronas: 100, levelId: 1 };
    
    beforeEach(() => {
        vi.clearAllMocks();
        vi.mocked(getServerSession).mockResolvedValue(null);
        vi.mocked(cookies).mockResolvedValue({
            get: vi.fn().mockReturnValue({ value: 'test-user-id' }),
            set: vi.fn(),
        } as any);
        vi.mocked(prisma.user.findUnique).mockResolvedValue(mockUser as any);
    });

    it('should complete the level exactly on the 50th word', async () => {
        const TOTAL_WORDS = 50;
        
        // Setup mocks
        vi.mocked(prisma.word.findUnique).mockResolvedValue({ id: 1, levelId: 1 } as any);
        vi.mocked(prisma.word.count).mockResolvedValue(TOTAL_WORDS);
        
        // Simulate solving 50 words
        for (let i = 1; i <= TOTAL_WORDS; i++) {
            const isLastWord = i === TOTAL_WORDS;
            
            // In each step, the count of processed words in the database increases
            vi.mocked(prisma.progress.count).mockResolvedValue(i);
            vi.mocked(prisma.user.update).mockResolvedValue({ ...mockUser, neuronas: 100 + (i * 10) } as any);
            
            const result = await recordWordSolved(i, 10);
            
            expect(result.success).toBe(true);
            
            if (!isLastWord) {
                expect(result.isLevelComplete).toBe(false);
            } else {
                expect(result.isLevelComplete).toBe(true);
                expect(result.levelStats?.solved).toBe(50);
                expect(result.levelStats?.total).toBe(50);
            }
        }
        
        expect(prisma.progress.upsert).toHaveBeenCalledTimes(50);
        expect(prisma.user.update).toHaveBeenCalledTimes(50);
    });
});
