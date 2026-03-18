'use client';

import React, { useState, useEffect, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Trophy, Clock, Brain, User, X, Lightbulb, SkipForward, Timer as TimerIcon, Volume2, VolumeX, Keyboard, ChevronRight, Trash2, LayoutGrid } from 'lucide-react';
import { useSession, signOut } from 'next-auth/react';
import { getRandomWordFromLevel, recordWordSolved, useHint, moveToNextLevel, getRandomMessage } from '@/app/actions/game';
import { getRandomSuccessMessage, getRandomFailMessage } from '@/lib/messages';
import { soundManager } from '@/lib/sounds';
import VictoryParticles from './VictoryParticles';
import LevelUpModal from './LevelUpModal';

interface GameEngineProps {
  initialWord: string;
  initialDescription: string;
  initialLevel: number;
  initialWordId: number;
  initialRevealedIndices?: number[];
}

export default function GameEngine({ initialWord, initialDescription, initialLevel, initialWordId, initialRevealedIndices }: GameEngineProps) {
  const { data: session, update: updateSession } = useSession();
  
  // Game State
  const [currentWord, setCurrentWord] = useState(initialWord);
  const [currentWordId, setCurrentWordId] = useState(initialWordId);
  const [currentDescription, setCurrentDescription] = useState(initialDescription);
  const [currentLevel, setCurrentLevel] = useState(initialLevel);
  const [revealedIndices, setRevealedIndices] = useState<number[]>(initialRevealedIndices || []);
  const [isPoolMode, setIsPoolMode] = useState(true);
  
  const [userInput, setUserInput] = useState<string[]>(() => {
    const input = new Array(initialWord.length).fill('');
    (initialRevealedIndices || []).forEach(idx => {
      input[idx] = initialWord[idx].toUpperCase();
    });
    return input;
  });
  
  const [cursorPos, setCursorPos] = useState(0);
  const [timer, setTimer] = useState(0);
  const [isGuessed, setIsGuessed] = useState(false);
  const [isLoadingNext, setIsLoadingNext] = useState(false);
  const [feedbackMessage, setFeedbackMessage] = useState<string | null>(null);
  const [messageType, setMessageType] = useState<'success' | 'fail' | null>(null);
  const [showParticles, setShowParticles] = useState(false);
  const [soundEnabled, setSoundEnabled] = useState(true);
  const [showLevelUpModal, setShowLevelUpModal] = useState(false);
  const [levelStats, setLevelStats] = useState<{ solved: number; total: number } | null>(null);

  const neuronas = session?.user?.neuronas ?? 50;

  // Timer logic
  useEffect(() => {
    if (isGuessed) return;
    const interval = setInterval(() => {
      setTimer((prev) => prev + 1);
    }, 1000);
    return () => clearInterval(interval);
  }, [isGuessed]);

  // Handle keyboard input
  const handleKeyDown = useCallback((e: globalThis.KeyboardEvent) => {
    if (isGuessed || isLoadingNext) return;

    if (e.key === 'Backspace') {
      const newInputs = [...userInput];
      if (userInput[cursorPos] === '' && cursorPos > 0) {
        newInputs[cursorPos - 1] = '';
        setUserInput(newInputs);
        setCursorPos(cursorPos - 1);
        setFeedbackMessage(null);
      } else {
        newInputs[cursorPos] = '';
        setUserInput(newInputs);
        setFeedbackMessage(null);
      }
    } else if (e.key.length === 1 && /^[a-zA-ZñÑ]$/.test(e.key)) {
      const newInputs = [...userInput];
      newInputs[cursorPos] = e.key.toUpperCase();
      setUserInput(newInputs);
      setFeedbackMessage(null);
      
      if (cursorPos < currentWord.length - 1) {
        setCursorPos(cursorPos + 1);
      }
    }
  }, [cursorPos, userInput, isGuessed, currentWord, isLoadingNext]);

  useEffect(() => {
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [handleKeyDown]);

  // Check word
  useEffect(() => {
    const checkWord = async () => {
      const fullInput = userInput.join('');
      if (fullInput.length === currentWord.length && !userInput.includes('')) {
        if (fullInput === currentWord.toUpperCase()) {
          setIsGuessed(true);
          setFeedbackMessage(getRandomSuccessMessage(currentWordId));
          setMessageType('success');
          setShowParticles(true);
          soundManager.play('success');
          // Persist success
          const result = await recordWordSolved(currentWordId, timer);
          await updateSession();
          
          if (result.success && result.isLevelComplete) {
            setLevelStats(result.levelStats);
            setTimeout(() => {
              setShowLevelUpModal(true);
              soundManager.play('level_up'); // Assumed sound exists
            }, 1500);
          }

          setTimeout(() => setShowParticles(false), 3000);
        } else {
          // Only show fail message if they reached the end and it's wrong
          setFeedbackMessage(getRandomFailMessage());
          setMessageType('fail');
          soundManager.play('fail');
        }
      }
    };
    checkWord();
  }, [userInput, currentWord, currentWordId]);

  const handleNextWord = async () => {
    setIsLoadingNext(true);
    try {
      const nextWord = await getRandomWordFromLevel(currentLevel);
      if (nextWord) {
        setCurrentWord(nextWord.word);
        setCurrentWordId(nextWord.id);
        setCurrentDescription(nextWord.description);
        setUserInput(new Array(nextWord.word.length).fill(''));
        setCursorPos(0);
        setTimer(0);
        setIsGuessed(false);
        setFeedbackMessage(null);
        setMessageType(null);
        setShowParticles(false);
        soundManager.play('new_word');
      }
    } catch (error) {
      console.error('Error fetching next word:', error);
    } finally {
      setIsLoadingNext(false);
    }
  };

  const handleStartNextLevel = async () => {
    setIsLoadingNext(true);
    try {
      const result = await moveToNextLevel();
      if (result.success && result.nextWord) {
        setCurrentLevel(result.nextLevelId);
        setCurrentWord(result.nextWord.word);
        setCurrentWordId(result.nextWord.id);
        setCurrentDescription(result.nextWord.description);
        setUserInput(new Array(result.nextWord.word.length).fill(''));
        setCursorPos(0);
        setTimer(0);
        setIsGuessed(false);
        setFeedbackMessage(null);
        setMessageType(null);
        setShowLevelUpModal(false);
        soundManager.play('new_word');
        await updateSession();
      }
    } catch (error) {
      console.error('Error moving to next level:', error);
    } finally {
      setIsLoadingNext(false);
    }
  };

  // (Old handleGiveHint removed)

  const handleSkip = async () => {
    if (neuronas < 15 || isGuessed || isLoadingNext) return;
    
    setIsLoadingNext(true);
    const result = await useHint(currentWordId, 'skip');
    if (result.success) {
      await updateSession();
      await handleNextWord();
    } else {
      setIsLoadingNext(false);
    }
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  // Mobile keyboard support
  const inputRef = React.useRef<HTMLInputElement>(null);

  const handleFocus = () => {
    if (!isGuessed && !isLoadingNext) {
      inputRef.current?.focus();
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value.slice(-1); // Get last char
    if (val && /^[a-zA-ZñÑ]$/.test(val)) {
      const newInputs = [...userInput];
      newInputs[cursorPos] = val.toUpperCase();
      setUserInput(newInputs);
      setFeedbackMessage(null);
      
      if (cursorPos < currentWord.length - 1) {
        setCursorPos(cursorPos + 1);
      }
    }
    e.target.value = ''; // Always clear
  };

  const handleInputKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Backspace') {
      const newInputs = [...userInput];
      if (userInput[cursorPos] === '' && cursorPos > 0) {
        newInputs[cursorPos - 1] = '';
        setUserInput(newInputs);
        setCursorPos(cursorPos - 1);
        setFeedbackMessage(null);
      } else {
        newInputs[cursorPos] = '';
        setUserInput(newInputs);
        setFeedbackMessage(null);
      }
    }
  };

  // Generate Letter Pool (14 letters)
  const letterPool = React.useMemo(() => {
    const solutionLetters = currentWord.toUpperCase().split('');
    const poolSize = 14;
    const fillersNeeded = poolSize - solutionLetters.length;
    
    // Distractor logic: Common vowels and tricky consonants
    const commonDistractors = 'AEIOURSTNMPL';
    const distractors: string[] = [];
    for (let i = 0; i < fillersNeeded; i++) {
      distractors.push(commonDistractors[Math.floor(Math.random() * commonDistractors.length)]);
    }
    
    // Shuffle solution + distractors
    return [...solutionLetters, ...distractors].sort(() => Math.random() - 0.5);
  }, [currentWord]);

  // Track used indices from pool to gray them out
  // We need to know WHICH instance of a letter is used.
  // Actually, simpler: count occurrences.
  const getUsedInPool = (char: string) => {
    const totalInPool = letterPool.filter(l => l === char).length;
    const totalInInput = userInput.filter(l => l === char).length;
    // But wait, if they are hints, they are already there.
    return totalInInput;
  };

  const handlePoolClick = (char: string, poolIndex: number) => {
    if (isGuessed || isLoadingNext) return;
    
    // Find first empty position in userInput that is NOT a revealed index
    const firstEmpty = userInput.findIndex((c, i) => c === '' && !revealedIndices.includes(i));
    if (firstEmpty !== -1) {
      const newInputs = [...userInput];
      newInputs[firstEmpty] = char;
      setUserInput(newInputs);
      setFeedbackMessage(null);
      soundManager.play('click'); // Assumed sound
    }
  };

  const handleWordBoxClick = (index: number) => {
    if (isGuessed || isLoadingNext || revealedIndices.includes(index)) return;
    
    // Clear from this position to the right, but RESPECT REVEALED INDICES
    const newInputs = [...userInput];
    for (let i = index; i < userInput.length; i++) {
      if (!revealedIndices.includes(i)) {
        newInputs[i] = '';
      }
    }
    setUserInput(newInputs);
    setFeedbackMessage(null);
  };

  const handleClearAll = () => {
    if (isGuessed || isLoadingNext) return;
    const newInputs = [...userInput];
    userInput.forEach((_, i) => {
      if (!revealedIndices.includes(i)) {
        newInputs[i] = '';
      }
    });
    setUserInput(newInputs);
    setFeedbackMessage(null);
  };

  // Check word (Modified to use dynamic message from DB)
  useEffect(() => {
    const checkWord = async () => {
      const fullInput = userInput.join('');
      if (fullInput.length === currentWord.length && !userInput.includes('')) {
        if (fullInput === currentWord.toUpperCase()) {
          setIsGuessed(true);
          const msg = await getRandomMessage('success');
          setFeedbackMessage(msg);
          setMessageType('success');
          setShowParticles(true);
          soundManager.play('success');
          
          const result = await recordWordSolved(currentWordId, timer);
          await updateSession();
          
          if (result.success && result.isLevelComplete) {
            setLevelStats(result.levelStats);
            setTimeout(() => {
              setShowLevelUpModal(true);
              soundManager.play('level_up');
            }, 1500);
          }
          setTimeout(() => setShowParticles(false), 3000);
        } else {
          const msg = await getRandomMessage('fail');
          setFeedbackMessage(msg);
          setMessageType('fail');
          soundManager.play('fail');
        }
      }
    };
    checkWord();
  }, [userInput, currentWord, currentWordId]);

  const handleGiveHint = async () => {
    if (isGuessed || isLoadingNext) return;
    
    setIsLoadingNext(true);
    const result = await useHint(currentWordId, 'letter');
    if (result.success && result.revealedIndices) {
      setRevealedIndices(result.revealedIndices);
      const newInputs = [...userInput];
      result.revealedIndices.forEach(idx => {
        newInputs[idx] = currentWord[idx].toUpperCase();
      });
      setUserInput(newInputs);
      await updateSession();
    } else if (result.error) {
       setFeedbackMessage(result.error);
       setMessageType('fail');
    }
    setIsLoadingNext(false);
  };

  return (
    <div className="space-y-6 md:space-y-8 max-w-4xl mx-auto px-2 sm:px-0">
      {/* Hidden input for Keyboard Mode */}
      {!isPoolMode && (
        <input
          ref={inputRef}
          type="text"
          className="opacity-0 absolute -z-10 pointer-events-none"
          onChange={handleInputChange}
          onKeyDown={handleInputKeyDown}
          autoComplete="off"
          autoCorrect="off"
          spellCheck="false"
        />
      )}

      {/* Stats Bar */}
      <div className="flex justify-between items-center bg-[#1a1033]/60 border-2 border-[#00ffff]/30 rounded-2xl p-3 md:p-4 backdrop-blur-md">
        <div className="flex items-center gap-3 md:gap-6">
          <div className="flex items-center gap-2">
            <Trophy className="w-4 h-4 md:w-5 md:h-5 text-[#ff00ff]" />
            <span className="font-mono text-[10px] md:text-sm tracking-widest uppercase text-[#ff00ff]/80">Nivel {currentLevel}</span>
          </div>
          <div className="flex items-center gap-2 border-l border-[#00ffff]/20 pl-3 md:pl-6 space-x-1 md:space-x-2">
            <TimerIcon className="w-4 h-4 md:w-5 md:h-5 text-[#00ffff]" />
            <span className="font-mono text-base md:text-xl text-[#00ffff] font-bold tabular-nums">
              {formatTime(timer)}
            </span>
          </div>
        </div>
        <div className="flex items-center gap-2 md:gap-4">
          <button 
            onClick={() => setIsPoolMode(!isPoolMode)}
            className={`p-2 rounded-full transition-all ${isPoolMode ? 'bg-[#00ffff]/20 text-[#00ffff]' : 'text-[#00ffff]/40 hover:text-[#00ffff]'}`}
            title="Cambiar modo de entrada"
          >
            {isPoolMode ? <LayoutGrid className="w-4 h-4" /> : <Keyboard className="w-4 h-4" />}
          </button>
          <button 
            onClick={() => {
              const newState = !soundEnabled;
              setSoundEnabled(newState);
              soundManager.setEnabled(newState);
            }}
            className="p-1.5 md:p-2 hover:bg-white/10 rounded-full transition-colors text-[#00ffff]/40 hover:text-[#00ffff]"
          >
            {soundEnabled ? <Volume2 className="w-4 h-4" /> : <VolumeX className="w-4 h-4" />}
          </button>
          <div className="flex items-center gap-1.5 md:gap-2 bg-[#00ffff]/10 px-2 md:px-3 py-1 md:py-1.5 rounded-full border border-[#00ffff]/20">
            <Brain className="w-3 h-3 md:w-4 md:h-4 text-[#00ffff]" />
            <span className="font-mono text-[10px] md:text-xs font-bold text-[#00ffff]">{neuronas} N</span>
          </div>
        </div>
      </div>

      {/* Main Game Area */}
      <div 
        onClick={!isPoolMode ? handleFocus : undefined}
        className="bg-[#1a1033]/40 border-2 border-[#ff00ff]/20 rounded-2xl md:rounded-3xl p-6 md:p-12 backdrop-blur-xl relative overflow-hidden group"
      >
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-[#ff00ff]/50 to-transparent" />
        
        {/* Definition */}
        <div className="mb-6 md:mb-12 relative">
          <div className="absolute -left-4 md:-left-6 top-0 bottom-0 w-1 bg-[#ff00ff]/30 rounded-full" />
          <h2 className="text-lg md:text-2xl font-medium leading-relaxed text-[#00ffff]/90 italic">
            "{currentDescription}"
          </h2>
        </div>

        {/* Feedback Message */}
        <AnimatePresence>
          {feedbackMessage && (
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className={`
                mb-8 p-4 rounded-xl text-center font-mono text-xs md:text-base border-2 backdrop-blur-md
                ${messageType === 'success' 
                  ? 'bg-[#00ff00]/10 border-[#00ff00]/40 text-[#00ff00] shadow-[0_0_15px_rgba(0,255,0,0.2)]' 
                  : 'bg-[#ff0000]/10 border-[#ff0000]/40 text-[#ff0000] shadow-[0_0_15px_rgba(255,0,0,0.2)]'
                }
              `}
            >
              <div className="flex items-center justify-center gap-2 mb-1">
                {messageType === 'success' ? <Trophy className="w-4 h-4" /> : <Brain className="w-4 h-4" />}
                <span className="font-black tracking-widest uppercase text-[10px] opacity-70">
                  {messageType === 'success' ? 'LOGRO DESBLOQUEADO' : 'ERROR DE TRANSMISIÓN'}
                </span>
              </div>
              <p className="whitespace-pre-line leading-relaxed font-bold italic">
                {feedbackMessage}
              </p>
            </motion.div>
          )}
        </AnimatePresence>

        {/* Word Input */}
        <div className="flex flex-wrap justify-center gap-2 md:gap-4 mb-8 md:mb-12 relative">
          {userInput.map((char, index) => (
            <motion.div
              key={index}
              initial={false}
              onClick={() => handleWordBoxClick(index)}
              animate={{
                borderColor: isGuessed ? '#00ff00' : (revealedIndices.includes(index) ? '#00ffff60' : (cursorPos === index && !isPoolMode ? '#ff00ff' : '#ffffff20')),
                scale: !isPoolMode && cursorPos === index ? 1.05 : 1,
                boxShadow: isGuessed 
                  ? '0 0 15px rgba(0,255,0,0.3)' 
                  : (!isPoolMode && cursorPos === index ? '0 0 15px rgba(255,0,255,0.3)' : 'none')
              }}
              className={`
                w-9 h-12 md:w-14 md:h-20 flex items-center justify-center 
                text-2xl md:text-5xl font-black rounded-lg md:rounded-xl border-b-4 bg-[#050510]/60
                transition-all duration-200 shrink-0 cursor-pointer
                ${char ? 'text-[#00ffff]' : 'text-transparent'}
                ${revealedIndices.includes(index) ? 'bg-[#00ffff]/5 pointer-events-none border-b-[#00ffff]/40' : ''}
              `}
            >
              {char}
            </motion.div>
          ))}
          {/* Clear All Button */}
          {isPoolMode && !isGuessed && (
             <button 
              onClick={(e) => { e.stopPropagation(); handleClearAll(); }}
              className="absolute -right-2 md:-right-10 top-1/2 -translate-y-1/2 p-2 text-[#ff0000]/40 hover:text-[#ff0000] transition-colors"
              title="Limpiar palabra"
             >
                <Trash2 className="w-5 h-5 md:w-6 md:h-6" />
             </button>
          )}
        </div>

        {/* Letter Pool (New) */}
        {isPoolMode && !isGuessed && (
          <div className="flex flex-col gap-2 mb-10">
            <div className="flex justify-center gap-2">
              {letterPool.slice(0, 7).map((char, i) => (
                <LetterBox 
                  key={i} 
                  char={char} 
                  isUsed={getUsedInPool(char) > letterPool.slice(0, i+1).filter(l => l === char).length - 1} 
                  onClick={() => handlePoolClick(char, i)} 
                />
              ))}
            </div>
            <div className="flex justify-center gap-2">
              {letterPool.slice(7, 14).map((char, i) => (
                <LetterBox 
                  key={i+7} 
                  char={char} 
                  isUsed={getUsedInPool(char) > letterPool.slice(0, i+8).filter(l => l === char).length - 1} 
                  onClick={() => handlePoolClick(char, i+7)} 
                />
              ))}
            </div>
          </div>
        )}

        {/* Controls */}
        <div className="flex flex-col sm:flex-row justify-center items-center gap-3 md:gap-4">
          {!isGuessed ? (
            <>
              <button 
                onClick={(e) => { e.stopPropagation(); handleGiveHint(); }}
                disabled={isLoadingNext || neuronas < 5}
                className="w-full sm:w-auto flex items-center justify-center gap-2 px-6 py-3 bg-[#00ffff]/10 hover:bg-[#00ffff]/20 border border-[#00ffff]/40 rounded-xl transition-all group/btn disabled:opacity-50 disabled:cursor-not-allowed"
                title="Pedir letra"
              >
                <Lightbulb className="w-4 h-4 md:w-5 md:h-5 group-hover/btn:text-yellow-400 transition-colors" />
                <span className="font-mono text-xs md:text-sm font-bold">LETRA (-5 N)</span>
              </button>
              <button 
                onClick={(e) => { e.stopPropagation(); handleSkip(); }}
                disabled={isLoadingNext || neuronas < 15}
                className="w-full sm:w-auto flex items-center justify-center gap-2 px-6 py-3 bg-[#ff00ff]/10 hover:bg-[#ff00ff]/20 border border-[#ff00ff]/40 rounded-xl transition-all group/btn disabled:opacity-50 disabled:cursor-not-allowed"
                title="Saltar palabra"
              >
                <SkipForward className="w-4 h-4 md:w-5 md:h-5 group-hover/btn:translate-x-1 transition-transform" />
                <span className="font-mono text-xs md:text-sm font-bold">SALTAR (-15 N)</span>
              </button>
            </>
          ) : (
            <motion.button
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              onClick={(e) => { e.stopPropagation(); handleNextWord(); }}
              disabled={isLoadingNext}
              className="w-full sm:w-auto flex items-center justify-center gap-3 px-8 md:px-10 py-4 md:py-5 bg-[#00ffff] text-[#050510] rounded-xl md:rounded-2xl font-black text-lg md:text-xl hover:scale-105 active:scale-95 transition-all shadow-[0_0_20px_#00ffff40]"
            >
              SIGUIENTE <ChevronRight className="w-6 h-6" />
            </motion.button>
          )}
        </div>
      </div>

      <div className="flex justify-center gap-4 text-[#00ffff]/40 font-mono text-[9px] md:text-[10px] tracking-widest uppercase">
        <div className="flex items-center gap-1">
          <Keyboard className="w-3 h-3" /> {(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) ? 'TOCÁ EL JUEGO PARA ESCRIBIR' : 'USÁ TU TECLADO PARA ESCRIBIR'}
        </div>
      </div>
      <AnimatePresence>
        {showParticles && <VictoryParticles />}
      </AnimatePresence>
      <AnimatePresence>
        {showLevelUpModal && levelStats && (
          <LevelUpModal 
            level={currentLevel}
            stats={levelStats}
            onNextLevel={handleStartNextLevel}
          />
        )}
      </AnimatePresence>
    </div>
  );
}

// Helper Components
function LetterBox({ char, isUsed, onClick }: { char: string; isUsed: boolean; onClick: () => void }) {
  return (
    <motion.button
      whileHover={!isUsed ? { scale: 1.05, borderColor: '#00ffff' } : {}}
      whileTap={!isUsed ? { scale: 0.95 } : {}}
      onClick={!isUsed ? onClick : undefined}
      className={`
        w-10 h-12 md:w-14 md:h-16 flex items-center justify-center 
        text-xl md:text-3xl font-black rounded-lg border-2 
        transition-all duration-200
        ${isUsed 
          ? 'bg-black/40 border-[#ffffff10] text-[#ffffff20] cursor-not-allowed' 
          : 'bg-[#050510] border-[#00ffff]/20 text-[#00ffff] cursor-pointer hover:shadow-[0_0_10px_rgba(0,255,255,0.2)]'
        }
      `}
    >
      {char}
    </motion.button>
  );
}
