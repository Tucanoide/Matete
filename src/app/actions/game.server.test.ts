import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getGlobalRanking, getLevelRanking } from './game';
import prisma from '@/lib/prisma';

// Mock Prisma
vi.mock('@/lib/prisma', () => ({
  default: {
    user: {
      findMany: vi.fn(),
    },
  },
}));

describe('Game Server Actions - Rankings', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('getGlobalRanking should filter out users without email (guests)', async () => {
    vi.mocked(prisma.user.findMany).mockResolvedValue([]);

    await getGlobalRanking(10);

    expect(prisma.user.findMany).toHaveBeenCalledWith(
      expect.objectContaining({
        where: {
          email: { not: null }
        }
      })
    );
  });

  it('getLevelRanking should filter out users without email (guests)', async () => {
    vi.mocked(prisma.user.findMany).mockResolvedValue([]);

    await getLevelRanking(1, 10);

    expect(prisma.user.findMany).toHaveBeenCalledWith(
      expect.objectContaining({
        where: expect.objectContaining({
          email: { not: null },
          progress: expect.any(Object)
        })
      })
    );
  });
});
