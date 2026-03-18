import { describe, it, expect, vi, beforeEach } from 'vitest';
import { migrateGuestProgress } from './sync';
import prisma from '@/lib/prisma';
import { cookies } from 'next/headers';
import { getServerSession } from 'next-auth';

// Mock Dependencies
vi.mock('@/lib/prisma', () => ({
  default: {
    user: {
      findUnique: vi.fn(),
      update: vi.fn(),
      delete: vi.fn(),
    },
    progress: {
      findMany: vi.fn(),
      upsert: vi.fn(),
    },
    $transaction: vi.fn((cb) => cb(prisma)),
  },
}));

vi.mock('next/headers', () => ({
  cookies: vi.fn(() => ({
    get: vi.fn(),
    delete: vi.fn(),
  })),
}));

vi.mock('next-auth', () => ({
  getServerSession: vi.fn(),
}));

describe('Sync Actions - migrateGuestProgress', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const mockGuestId = 'guest-123';
  const mockAuthUserId = 'auth-456';
  const mockAuthUserEmail = 'user@example.com';

  it('should transfer progress and stats from guest to auth user', async () => {
    // 1. Setup mocks
    vi.mocked(getServerSession).mockResolvedValue({ user: { email: mockAuthUserEmail } } as any);
    
    const mockCookieStore = { 
      get: vi.fn().mockReturnValue({ value: mockGuestId }),
      delete: vi.fn()
    };
    vi.mocked(cookies).mockResolvedValue(mockCookieStore as any);

    const mockAuthUser = { id: mockAuthUserId, email: mockAuthUserEmail, points: 10, neuronas: 50, levelId: 1, totalTime: 100, progress: [] };
    const mockGuestUser = { id: mockGuestId, points: 20, neuronas: 100, levelId: 2, totalTime: 200, progress: [] };
    
    vi.mocked(prisma.user.findUnique).mockImplementation(async (args: any) => {
      if (args.where.id === mockGuestId) return mockGuestUser as any;
      if (args.where.email === mockAuthUserEmail) return mockAuthUser as any;
      return null;
    });

    const mockProgress = [
      { wordId: 1, guessed: true, timeTaken: 10, endTime: new Date() }
    ];
    vi.mocked(mockGuestUser.progress as any).push(...mockProgress);
    // Actually, I'll just set it in the declaration

    // 2. Execute
    const result = await migrateGuestProgress();

    // 3. Assertions
    expect(result.success).toBe(true);
    expect(prisma.user.update).toHaveBeenCalledWith(
      expect.objectContaining({
        where: { id: mockAuthUserId },
        data: expect.objectContaining({
          points: { increment: 20 },
          neuronas: { increment: 50 }, // 100 - 50 = 50
          totalTime: { increment: 200 },
          levelId: 2
        })
      })
    );
    expect(prisma.user.delete).toHaveBeenCalledWith({ where: { id: mockGuestId } });
    expect(mockCookieStore.delete).toHaveBeenCalledWith('matete-guest-id');
  });

  it('should return error if no authenticated session is found', async () => {
    vi.mocked(getServerSession).mockResolvedValue(null);
    
    const result = await migrateGuestProgress();

    expect(result.success).toBe(false);
    expect(result.error).toBe('No authenticated session');
  });

  it('should return success but do nothing if no guest cookie is found', async () => {
    vi.mocked(getServerSession).mockResolvedValue({ user: { email: mockAuthUserEmail } } as any);
    vi.mocked(cookies).mockResolvedValue({ get: vi.fn().mockReturnValue(null) } as any);

    const result = await migrateGuestProgress();

    expect(result.success).toBe(true);
    expect(prisma.user.findUnique).not.toHaveBeenCalled();
  });
});
