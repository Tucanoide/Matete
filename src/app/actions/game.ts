'use server'

import prisma from '@/lib/prisma'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { cookies } from 'next/headers'

const GUEST_COOKIE_NAME = 'matete-guest-id'

export async function getUser() {
  const session = await getServerSession(authOptions)
  
  if (session?.user?.email) {
    const user = await prisma.user.findUnique({
      where: { email: session.user.email }
    })
    if (user) return user
  }

  const cookieStore = await cookies()
  const guestId = cookieStore.get(GUEST_COOKIE_NAME)?.value

  if (guestId) {
    const user = await prisma.user.findUnique({
      where: { id: guestId }
    })
    if (user) return user
  }

  return null;
}

export async function ensureUser() {
  let user = await getUser();
  if (user) return user;

  const newUser = await prisma.user.create({
    data: {
      name: 'Explorador Anónimo',
      neuronas: 50,
      levelId: 1
    }
  })

  const cookieStore = await cookies()
  cookieStore.set(GUEST_COOKIE_NAME, newUser.id, {
    maxAge: 60 * 60 * 24 * 30,
    path: '/'
  })

  return newUser
}

export async function recordWordSolved(wordId: number, timeTaken: number) {
  const user = await ensureUser()
  if (!user) return { success: false, error: 'User not found' }

  // Fetch the word to know its level
  const solvedWord = await prisma.word.findUnique({
    where: { id: wordId },
    select: { levelId: true }
  })
  if (!solvedWord) return { success: false, error: 'Word not found' }

  // 1. Create progress record
  await prisma.progress.upsert({
    where: {
      userId_wordId: {
        userId: user.id,
        wordId: wordId
      }
    },
    update: {
      guessed: true,
      timeTaken,
      endTime: new Date()
    },
    create: {
      userId: user.id,
      wordId,
      guessed: true,
      timeTaken,
      endTime: new Date()
    }
  })

  // 2. Update user neuronas and points
  const updatedUser = await prisma.user.update({
    where: { id: user.id },
    data: {
      neuronas: { increment: 10 },
      points: { increment: 1 },
      totalTime: { increment: timeTaken }
    }
  })

  // 3. Check level completion
  const totalWordsInLevel = await prisma.word.count({
    where: { levelId: solvedWord.levelId }
  })

  const solvedWordsInLevel = await prisma.progress.count({
    where: {
      userId: user.id,
      word: { levelId: solvedWord.levelId },
      guessed: true
    }
  })

  const isLevelComplete = solvedWordsInLevel >= totalWordsInLevel

  return { 
    success: true, 
    updatedUser, 
    isLevelComplete,
    levelStats: {
      solved: solvedWordsInLevel,
      total: totalWordsInLevel,
      levelId: solvedWord.levelId
    }
  }
}

export async function getGameSettings() {
  const settings = await prisma.setting.findMany();
  return Object.fromEntries(settings.map(s => [s.id, s.value]));
}

export async function getRandomMessage(type: 'success' | 'fail') {
  const messages = await prisma.gameMessage.findMany({
    where: { type }
  });
  if (messages.length === 0) return type === 'success' ? '¡Correcto!' : '¡Error!';
  return messages[Math.floor(Math.random() * messages.length)].content;
}

export async function useHint(wordId: number, type: 'letter' | 'skip') {
  const user = await ensureUser()
  if (!user) return { success: false, error: 'User not found' }

  // Get dynamic costs
  const settings = await getGameSettings();
  const costLetter = parseInt(settings['costo_pista'] || '5');
  const costSkip = 15; // default skip cost
  
  const cost = type === 'letter' ? costLetter : costSkip;
  
  if (user.neuronas < cost) return { success: false, error: 'Not enough neuronas' }

  // 0. If it's a letter hint, check the 2-hint limit and handle revealed indices
  let revealedIndices: number[] = [];
  if (type === 'letter') {
    const progress = await prisma.progress.findUnique({
      where: { userId_wordId: { userId: user.id, wordId } },
      select: { revealedIndices: true }
    });
    
    const currentIndices = progress?.revealedIndices || [];
    if (currentIndices.length >= 2) {
      return { success: false, error: 'Has alcanzado el límite de 2 pistas para esta palabra.' };
    }

    const word = await prisma.word.findUnique({
      where: { id: wordId },
      select: { word: true }
    });
    if (!word) return { success: false, error: 'Word not found' };

    // Find a random position not yet revealed
    const availablePositions = Array.from({ length: word.word.length }, (_, i) => i)
      .filter(i => !currentIndices.includes(i));
    
    if (availablePositions.length === 0) return { success: false, error: 'No hay más letras para revelar.' };

    const randomIndex = availablePositions[Math.floor(Math.random() * availablePositions.length)];
    revealedIndices = [...currentIndices, randomIndex];

    // Update progress with new index
    await prisma.progress.upsert({
      where: { userId_wordId: { userId: user.id, wordId } },
      update: { revealedIndices },
      create: { userId: user.id, wordId, revealedIndices }
    });
  }

  // 1. Deduct neuronas
  const updatedUser = await prisma.user.update({
    where: { id: user.id },
    data: {
      neuronas: { decrement: cost }
    }
  })

  // 2. If it's a skip, mark as skipped
  if (type === 'skip') {
    await prisma.progress.upsert({
      where: {
        userId_wordId: {
          userId: user.id,
          wordId: wordId
        }
      },
      update: { skipped: true },
      create: {
        userId: user.id,
        wordId,
        skipped: true
      }
    })
  }

  return { 
    success: true, 
    remainingNeuronas: updatedUser.neuronas,
    revealedIndices: type === 'letter' ? revealedIndices : undefined 
  }
}

export async function getGlobalRanking(limit: number = 10) {
  return await prisma.user.findMany({
    where: {
      email: { not: null }
    },
    orderBy: [
      { points: 'desc' },
      { totalTime: 'asc' },
      { neuronas: 'desc' }
    ],
    take: limit,
    select: {
      id: true,
      name: true,
      username: true,
      image: true,
      points: true,
      totalTime: true,
      neuronas: true
    }
  })
}

export async function getLevelRanking(levelId: number, limit: number = 10) {
  // This is slightly more complex as it depends on Progress per level
  // For now, we'll return users who have solved words in this level, 
  // ordered by how many words they solved in this level and total time for those words.
  const usersWithProgress = await prisma.user.findMany({
    where: {
      email: { not: null },
      progress: {
        some: {
          word: { levelId }
        }
      }
    },
    include: {
      progress: {
        where: {
          word: { levelId },
          guessed: true
        }
      }
    },
    take: limit
  })

  // Sort manually to keep it simple since Prisma doesn't support complex aggregations in orderBy directly easily
  return usersWithProgress
    .map(user => ({
      id: user.id,
      name: user.name,
      username: user.username,
      image: user.image,
      levelPoints: user.progress.length,
      levelTime: user.progress.reduce((acc, p) => acc + (p.timeTaken || 0), 0)
    }))
    .sort((a, b) => b.levelPoints - a.levelPoints || a.levelTime - b.levelTime)
    .slice(0, limit)
}

export async function getRandomWordFromLevel(levelId: number) {
  const user = await getUser()
  
  if (!user) {
    const words = await prisma.word.findMany({
      where: { levelId }
    })
    if (words.length === 0) return null
    return words[Math.floor(Math.random() * words.length)]
  }

  // Find words already solved or skipped by this user
  const userProgress = await prisma.progress.findMany({
    where: { 
      userId: user.id,
      OR: [
        { guessed: true },
        { skipped: true }
      ]
    },
    select: { wordId: true }
  })

  const solvedWordIds = new Set(userProgress.map(p => p.wordId))

  // Find all words in this level NOT in the solved set
  const availableWords = await prisma.word.findMany({
    where: { 
      levelId,
      id: { notIn: Array.from(solvedWordIds) }
    }
  })

  if (availableWords.length === 0) {
    // If all words in this level are solved, just return a random one from the level 
    // (or we could return null to trigger level up logic in the caller)
    const allWords = await prisma.word.findMany({ where: { levelId } })
    return allWords[Math.floor(Math.random() * allWords.length)]
  }

  return availableWords[Math.floor(Math.random() * availableWords.length)]
}

export async function moveToNextLevel() {
  const user = await getUser()
  if (!user) return { success: false, error: 'User not found' }

  const nextLevelId = user.levelId + 1

  // Update user's level
  const updatedUser = await prisma.user.update({
    where: { id: user.id },
    data: { levelId: nextLevelId }
  })

  // Get first word of next level
  const nextWord = await prisma.word.findFirst({
    where: { levelId: nextLevelId },
    orderBy: { id: 'asc' }
  })

  return { 
    success: true, 
    nextLevelId, 
    nextWord,
    updatedUser 
  }
}

export async function getLevels() {
  return await prisma.level.findMany({
    orderBy: { id: 'asc' }
  })
}

export async function getWordsByLevel(levelId: number) {
  return await prisma.word.findMany({
    where: { levelId },
    orderBy: { id: 'asc' }
  })
}

export async function getInitialGameState() {
  const user = await getUser()
  
  // If no user, return first level and first word as defaults
  if (!user) {
    const firstLevel = await prisma.level.findFirst({ orderBy: { id: 'asc' } });
    const firstWord = await prisma.word.findFirst({ 
      where: { levelId: firstLevel?.id }, 
      orderBy: { id: 'asc' } 
    });
    return {
      word: firstWord,
      level: firstLevel,
      revealedIndices: [],
      needsUsername: false
    }
  }

  // Check if user needs a username
  const needsUsername = !user.username;

  // Get current level
  const currentLevel = await prisma.level.findUnique({
    where: { id: user.levelId }
  })

  if (!currentLevel) return null

  // Get words for this level
  const words = await prisma.word.findMany({
    where: { levelId: user.levelId },
    orderBy: { id: 'asc' }
  })

  // Find the first word not yet solved or skipped
  const userProgress = await prisma.progress.findMany({
    where: { userId: user.id },
    select: { wordId: true, guessed: true, skipped: true, revealedIndices: true }
  })

  const solvedWordIds = new Set(
    userProgress
      .filter(p => p.guessed || p.skipped)
      .map(p => p.wordId)
  )

  let initialWord = words.find(w => !solvedWordIds.has(w.id))

  // If all words in this level are solved, move to next level
  if (!initialWord) {
    const nextLevel = await prisma.level.findFirst({
      where: { id: { gt: user.levelId } },
      orderBy: { id: 'asc' }
    })

    if (nextLevel) {
      // Update user to next level and get its first word
      await prisma.user.update({
        where: { id: user.id },
        data: { levelId: nextLevel.id }
      })
      
      const nextWords = await prisma.word.findMany({
        where: { levelId: nextLevel.id },
        orderBy: { id: 'asc' }
      })
      
      return {
        word: nextWords[0],
        level: nextLevel,
        revealedIndices: [],
        needsUsername
      }
    } else {
      // All levels completed? Return the last word of the last level or something
      const lastWord = words[words.length - 1];
      const lastProgress = userProgress.find(p => p.wordId === lastWord.id);
      return {
        word: lastWord,
        level: currentLevel,
        revealedIndices: lastProgress?.revealedIndices || [],
        needsUsername
      }
    }
  }

  const currentWordProgress = userProgress.find(p => p.wordId === initialWord!.id);

  return {
    word: initialWord,
    level: currentLevel,
    revealedIndices: currentWordProgress?.revealedIndices || [],
    needsUsername
  }
}

export async function checkUsername(username: string) {
  const existingUser = await prisma.user.findUnique({
    where: { username }
  });
  return { available: !existingUser };
}

export async function updateUsername(username: string) {
  const user = await getUser();
  if (!user) return { success: false, error: 'Unauthorized' };

  try {
    await prisma.user.update({
      where: { id: user.id },
      data: { username }
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: 'Error updating username' };
  }
}
