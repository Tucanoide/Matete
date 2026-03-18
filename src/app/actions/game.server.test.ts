import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getGlobalRanking, getLevelRanking, recordWordSolved, useHint, moveToNextLevel } from './game';
import prisma from '@/lib/prisma';
import { cookies } from 'next/headers';
import { getServerSession } from 'next-auth';

// Mock Prisma
vi.mock('@/lib/prisma', () => ({
  default: {
    user: {
      findUnique: vi.fn(),
      findMany: vi.fn(),
      update: vi.fn(),
      create: vi.fn(),
    },
    word: {
      findUnique: vi.fn(),
      count: vi.fn(),
      findFirst: vi.fn(),
    },
    progress: {
      upsert: vi.fn(),
      count: vi.fn(),
      findUnique: vi.fn(),
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

describe('Game Server Actions', () => {
  const mockUser = { id: 'user-1', neuronas: 50, levelId: 1 };

  beforeEach(() => {
    vi.clearAllMocks();
    
    // Auth mocks
    vi.mocked(getServerSession).mockResolvedValue(null);
    vi.mocked(cookies).mockResolvedValue({
      get: vi.fn().mockReturnValue({ value: 'user-1' }),
      set: vi.fn(),
    } as any);

    // Prisma mocks
    vi.mocked(prisma.user.findUnique).mockResolvedValue(mockUser as any);
    vi.mocked(prisma.user.create).mockResolvedValue(mockUser as any);
  });

  describe('Rankings', () => {
    it('getGlobalRanking should filter out users without email (guests)', async () => {
      vi.mocked(prisma.user.findMany).mockResolvedValue([]);
      await getGlobalRanking(10);
      expect(prisma.user.findMany).toHaveBeenCalledWith(
        expect.objectContaining({ where: { email: { not: null } } })
      );
    });
  });

  describe('Game Progress', () => {
    it('recordWordSolved should update neuronas and record progress', async () => {
      vi.mocked(prisma.word.findUnique).mockResolvedValue({ id: 1, levelId: 1 } as any);
      vi.mocked(prisma.user.update).mockResolvedValue({ ...mockUser, neuronas: 60 } as any);
      vi.mocked(prisma.word.count).mockResolvedValue(10);
      vi.mocked(prisma.progress.count).mockResolvedValue(5);

      const result = await recordWordSolved(1, 30);

      expect(result.success).toBe(true);
      expect(prisma.progress.upsert).toHaveBeenCalled();
      expect(prisma.user.update).toHaveBeenCalledWith(
        expect.objectContaining({
          data: expect.objectContaining({ neuronas: { increment: 10 } })
        })
      );
    });

    it('moveToNextLevel should increment user level', async () => {
      vi.mocked(prisma.user.update).mockResolvedValue({ ...mockUser, levelId: 2 } as any);
      vi.mocked(prisma.word.findFirst).mockResolvedValue({ id: 2, word: 'TEST' } as any);

      const result = await moveToNextLevel();

      expect(result.success).toBe(true);
      expect(result.nextLevelId).toBe(2);
      expect(prisma.user.update).toHaveBeenCalledWith(
        expect.objectContaining({
          data: { levelId: 2 }
        })
      );
    });
  });

  describe('Hints', () => {
    it('useHint (letter) should deduct neuronas and return revealed indices', async () => {
      vi.mocked(prisma.progress.findUnique).mockResolvedValue({ revealedIndices: [] } as any);
      vi.mocked(prisma.word.findUnique).mockResolvedValue({ word: 'HOLA' } as any);
      vi.mocked(prisma.user.update).mockResolvedValue({ ...mockUser, neuronas: 45 } as any);

      const result = await useHint(1, 'letter');

      expect(result.success).toBe(true);
      expect(result.remainingNeuronas).toBe(45);
      expect(prisma.user.update).toHaveBeenCalledWith(
        expect.objectContaining({
           data: { neuronas: { decrement: 5 } }
        })
      );
    });

    it('useHint (letter) should fail if limit of 2 is reached', async () => {
      vi.mocked(prisma.progress.findUnique).mockResolvedValue({ revealedIndices: [0, 1] } as any);

      const result = await useHint(1, 'letter');

      expect(result.success).toBe(false);
      expect(result.error).toContain('límite de 2 pistas');
    });

    it('useHint (skip) should deduct 15 neuronas', async () => {
        vi.mocked(prisma.user.update).mockResolvedValue({ ...mockUser, neuronas: 35 } as any);
        
        const result = await useHint(1, 'skip');
        
        expect(result.success).toBe(true);
        expect(prisma.user.update).toHaveBeenCalledWith(
            expect.objectContaining({
                data: { neuronas: { decrement: 15 } }
            })
        );
    });
  });
});
