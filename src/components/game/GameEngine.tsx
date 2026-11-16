'use client';

import React, { useState, useEffect, useCallback, useSyncExternalStore } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Trophy, Timer as TimerIcon, LayoutGrid, Keyboard, Volume2, VolumeX, Brain, Trash2, Lightbulb, SkipForward, ChevronRight, Clock } from 'lucide-react';
import UsernameModal from './UsernameModal';
import InstructionsModal from './InstructionsModal';
import { useSession } from 'next-auth/react';
import { getNextWord, submitGuess, startWord, requestHint, moveToNextLevel, getRandomMessage } from '@/app/actions/game';
import type { PublicWord } from '@/lib/public-word';
import { soundManager } from '@/lib/sounds';
import VictoryParticles from './VictoryParticles';
import LevelUpModal from './LevelUpModal';
import SolvedPanel, { type SolvedInfo } from '@/components/social/SolvedPanel';
import GameOverPanel from './GameOverPanel';

interface GameEngineProps {
  // Vista pública de la palabra: el navegador nunca recibe la solución (spec 001-hardening, FR-006).
  initialWord: PublicWord;
  initialLevel: number;
  initialNeedsUsername?: boolean;
  initialNeuronas?: number;
  /** Costos que cobra el servidor (HINT-09): el cliente solo los muestra. */
  initialCosts?: { letter: number; skip: number };
}

const DEFAULT_COSTS = { letter: 5, skip: 15 };

// UI-16: el user agent se lee solo en el navegador; en el servidor se asume escritorio.
const MOBILE_UA = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i;
const noopSubscribe = () => () => {};
const isMobileClient = () => typeof navigator !== 'undefined' && MOBILE_UA.test(navigator.userAgent);
const isMobileServer = () => false;

const inputFromRevealed = (length: number, revealed: PublicWord['revealed']) => {
  const input = new Array(length).fill('');
  revealed.forEach(({ index, letter }) => {
    if (index >= 0 && index < length) input[index] = letter;
  });
  return input;
};

const firstFreeCursor = (length: number, revealedIdx: number[]) => {
  let first = 0;
  while (first < length && revealedIdx.includes(first)) first++;
  return Math.min(first, length - 1);
};

export default function GameEngine({
  initialWord,
  initialLevel,
  initialNeedsUsername = false,
  initialNeuronas = 50,
  initialCosts = DEFAULT_COSTS,
}: GameEngineProps) {
  const costs = initialCosts;
  const { data: session, update: updateSession } = useSession();

  // Game State
  const [wordLength, setWordLength] = useState(initialWord.length);
  const [letterPool, setLetterPool] = useState<string[]>(initialWord.pool);
  const [currentWordId, setCurrentWordId] = useState(initialWord.wordId);
  const [currentDescription, setCurrentDescription] = useState(initialWord.description);
  const [currentLevel, setCurrentLevel] = useState(initialLevel);
  const [revealedIndices, setRevealedIndices] = useState<number[]>(initialWord.revealed.map(r => r.index));
  const [isPoolMode, setIsPoolMode] = useState(true);
  const [needsUsername, setNeedsUsername] = useState(initialNeedsUsername);
  // Si el servidor pasa a decir que el alias ya está (p. ej. llegó migrado desde el invitado, SYNC-10),
  // se cierra el modal. Nunca se abre a mitad de partida: en la primera visita no se pide alias (INI-01).
  const [prevNeedsUsername, setPrevNeedsUsername] = useState(initialNeedsUsername);
  if (initialNeedsUsername !== prevNeedsUsername) {
    setPrevNeedsUsername(initialNeedsUsername);
    if (!initialNeedsUsername) setNeedsUsername(false);
  }
  const [showInstructions, setShowInstructions] = useState(false);
  // Local neuronas state — initialized from the server, updated optimistically on spend
  const [neuronas, setNeuronas] = useState(initialNeuronas);
  
  const [userInput, setUserInput] = useState<string[]>(() =>
    inputFromRevealed(initialWord.length, initialWord.revealed)
  );

  const [cursorPos, setCursorPos] = useState(() =>
    firstFreeCursor(initialWord.length, initialWord.revealed.map(r => r.index))
  );

  const getValidCursor = useCallback((target: number, direction: 'forward' | 'backward') => {
    let current = target;
    if (direction === 'forward') {
      while (current < wordLength && revealedIndices.includes(current)) {
        current++;
      }
      return current; // Can be wordLength
    } else {
      while (current >= 0 && revealedIndices.includes(current)) {
        current--;
      }
      return Math.max(current, 0);
    }
  }, [wordLength, revealedIndices]);

  const [timer, setTimer] = useState(0);
  const [isGuessed, setIsGuessed] = useState(false);
  const [isLoadingNext, setIsLoadingNext] = useState(false);
  const [feedbackMessage, setFeedbackMessage] = useState<string | null>(null);
  const [messageType, setMessageType] = useState<'success' | 'fail' | null>(null);
  const [showParticles, setShowParticles] = useState(false);
  const [soundEnabled, setSoundEnabled] = useState(false);
  const [showLevelUpModal, setShowLevelUpModal] = useState(false);
  const [levelStats, setLevelStats] = useState<{ solved: number; total: number } | null>(null);
  const [needsRefresh, setNeedsRefresh] = useState(false);
  // Resultado del último acierto según el servidor: alimenta compartir/desafiar/comentarios (002-social).
  const [solvedInfo, setSolvedInfo] = useState<SolvedInfo | null>(null);
  // Terminó todos los niveles (PROG-09).
  const [gameOver, setGameOver] = useState(false);
  const isMobile = useSyncExternalStore(noopSubscribe, isMobileClient, isMobileServer);

  // Helper to handle server action errors (detect stale sessions)
  const handleActionError = useCallback((error: unknown, context: string) => {
    console.error(`Error in ${context}:`, error);
    const errorMsg = error instanceof Error ? error.message : String(error);
    if (errorMsg.includes('UnrecognizedActionError') || errorMsg.includes('not found on the server')) {
      setNeedsRefresh(true);
    }
  }, []);

  // Al montar: el servidor crea el invitado (cookie firmada) si hace falta y registra
  // el momento de entrega de la palabra, que es desde donde mide el tiempo (FR-015/16).
  useEffect(() => {
    startWord(initialWord.wordId).catch(error => handleActionError(error, 'starting word'));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Carga una palabra nueva (siguiente o primera del nivel) en el estado del juego.
  const applyWord = useCallback((word: PublicWord) => {
    const revealedIdx = word.revealed.map(r => r.index);
    setWordLength(word.length);
    setLetterPool(word.pool);
    setCurrentWordId(word.wordId);
    setCurrentDescription(word.description);
    setRevealedIndices(revealedIdx);
    setUserInput(inputFromRevealed(word.length, word.revealed));
    setCursorPos(firstFreeCursor(word.length, revealedIdx));
    setTimer(0);
    setIsGuessed(false);
    setFeedbackMessage(null);
    setMessageType(null);
    setShowParticles(false);
    setSolvedInfo(null);
  }, []);

  // Las neuronas de la sesión (usuario de Google) pisan el valor local cuando cambian.
  // Se ajusta durante el render en lugar de un efecto (evita un render en cascada).
  const sessionNeuronas = session?.user?.neuronas;
  // Arranca en undefined para que una sesión ya cargada al montar también se aplique (UI-21).
  const [seenSessionNeuronas, setSeenSessionNeuronas] = useState<number | undefined>(undefined);
  if (sessionNeuronas !== seenSessionNeuronas) {
    setSeenSessionNeuronas(sessionNeuronas);
    if (sessionNeuronas !== undefined) setNeuronas(sessionNeuronas);
  }

  // Timer logic
  useEffect(() => {
    if (isGuessed) return;
    const interval = setInterval(() => {
      setTimer((prev) => prev + 1);
    }, 1000);
    return () => clearInterval(interval);
  }, [isGuessed]);

  const handlersRef = React.useRef({ nextWord: () => {}, startNextLevel: () => {} });

  // Ref for all states needed by the keydown listener to avoid frequent re-binding
  const listenerStateRef = React.useRef({ userInput, cursorPos, wordLength, revealedIndices, isGuessed, isLoadingNext, showLevelUpModal });
  useEffect(() => {
    listenerStateRef.current = { userInput, cursorPos, wordLength, revealedIndices, isGuessed, isLoadingNext, showLevelUpModal };
  }, [userInput, cursorPos, wordLength, revealedIndices, isGuessed, isLoadingNext, showLevelUpModal]);

  useEffect(() => {
    const handleKeyDown = (e: globalThis.KeyboardEvent) => {
      // El teclado dentro del panel social (comentarios) no maneja el juego.
      const target = e.target as HTMLElement | null;
      if (target && typeof target.closest === 'function' && target.closest('[data-social]')) return;

      const { isGuessed: g, isLoadingNext: l, wordLength: wl, revealedIndices: ri, showLevelUpModal: slm } = listenerStateRef.current;

      if (e.key === 'Enter') {
        if (g && !l) {
          // Siempre la versión vigente de los handlers (no la del render en que se registró el listener).
          if (slm) {
            handlersRef.current.startNextLevel();
          } else {
            handlersRef.current.nextWord();
          }
        }
        return;
      }

      if (g || l) return;

      // UI-19: se calcula el próximo estado desde la referencia y se aplica con dos setState
      // independientes (antes había un setState dentro del updater de otro y el segundo
      // Backspace no borraba). La referencia se actualiza en el acto para teclas seguidas.
      const state = listenerStateRef.current;
      if (e.key === 'Backspace') {
        const newInputs = [...state.userInput];
        let actualCursor = state.cursorPos;
        if (actualCursor >= wl) actualCursor = getValidCursor(wl - 1, 'backward');
        let targetCursor = actualCursor;
        if (state.userInput[actualCursor] === '' && actualCursor > 0) {
          targetCursor = getValidCursor(actualCursor - 1, 'backward');
        }
        if (!ri.includes(targetCursor)) newInputs[targetCursor] = '';
        state.userInput = newInputs;
        state.cursorPos = targetCursor;
        setUserInput(newInputs);
        setCursorPos(targetCursor);
        setFeedbackMessage(null);
      } else if (e.key.length === 1 && /^[a-zA-ZñÑ]$/.test(e.key)) {
        const cursor = state.cursorPos;
        if (cursor >= wl || ri.includes(cursor)) return;
        const newInputs = [...state.userInput];
        newInputs[cursor] = e.key.toUpperCase();
        const next = getValidCursor(cursor + 1, 'forward');
        state.userInput = newInputs;
        state.cursorPos = next;
        setUserInput(newInputs);
        setCursorPos(next);
        setFeedbackMessage(null);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [getValidCursor]); // Only getValidCursor is stable enough

  // (Old checkWord removed, using version below)

  const handleNextWord = async () => {
    setIsLoadingNext(true);
    try {
      const nextWord = await getNextWord(currentLevel);
      if (nextWord) {
        applyWord(nextWord);
        soundManager.play('new_word');
      }
    } catch (error) {
       handleActionError(error, 'fetching next word');
    } finally {
      setIsLoadingNext(false);
    }
  };

  const handleLevelComplete = (stats: { solved: number; total: number }) => {
    setLevelStats(stats);
    setTimeout(() => {
      setShowLevelUpModal(true);
      soundManager.play('level_up');
    }, 500);
  };

  const handleStartNextLevel = async () => {
    setIsLoadingNext(true);
    try {
      const result = await moveToNextLevel();
      if (!result.success && result.gameOver) {
        setShowLevelUpModal(false);
        setGameOver(true);
        soundManager.play('level_up');
      } else if (result.success && result.nextWord) {
        setCurrentLevel(result.nextLevelId);
        applyWord(result.nextWord);
        setNeuronas(result.neuronas);
        setShowLevelUpModal(false);
        soundManager.play('new_word');
        await updateSession();
      }
    } catch (error) {
      handleActionError(error, 'moving to next level');
    } finally {
      setIsLoadingNext(false);
    }
  };

  // (Old handleGiveHint removed)

  const handleSkip = async () => {
    if (neuronas < costs.skip || isGuessed || isLoadingNext) return;

    setIsLoadingNext(true);
    try {
      const result = await requestHint(currentWordId, 'skip');
      if (result.success) {
        if (result.remainingNeuronas !== undefined) {
          setNeuronas(result.remainingNeuronas);
        }
        await updateSession();

        if ('isLevelComplete' in result && result.isLevelComplete && result.levelStats) {
          handleLevelComplete(result.levelStats);
        } else {
          await handleNextWord();
        }
      } else {
        setFeedbackMessage(result.error);
        setMessageType('fail');
      }
    } catch (error) {
      // HINT-10: un error no deja el juego trabado.
      handleActionError(error, 'skipping word');
      setFeedbackMessage('No pudimos saltar la palabra. Probá de nuevo.');
      setMessageType('fail');
    } finally {
      setIsLoadingNext(false);
    }
  };

  useEffect(() => {
    handlersRef.current = { nextWord: handleNextWord, startNextLevel: handleStartNextLevel };
  });

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
      if (cursorPos >= wordLength || revealedIndices.includes(cursorPos)) return;
      const newInputs = [...userInput];
      newInputs[cursorPos] = val.toUpperCase();
      setUserInput(newInputs);
      setFeedbackMessage(null);
      
      const next = getValidCursor(cursorPos + 1, 'forward');
      setCursorPos(next);
    }
    e.target.value = ''; // Always clear
  };

  const handleInputKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Backspace') {
      const newInputs = [...userInput];
      
      let actualCursor = cursorPos;
      if (actualCursor >= wordLength) {
        actualCursor = getValidCursor(wordLength - 1, 'backward');
      }

      if (userInput[actualCursor] === '' && actualCursor > 0) {
        const prev = getValidCursor(actualCursor - 1, 'backward');
        newInputs[prev] = '';
        setUserInput(newInputs);
        setCursorPos(prev);
        setFeedbackMessage(null);
      } else {
        newInputs[actualCursor] = '';
        setUserInput(newInputs);
        setCursorPos(actualCursor);
        setFeedbackMessage(null);
      }
    } else if (e.key === 'Enter' && isGuessed && !isLoadingNext) {
      if (showLevelUpModal) {
        handleStartNextLevel();
      } else {
        handleNextWord();
      }
    }
  };

  // El pool de 14 letras (solución + distractores, desordenado) lo genera el servidor.

  // Una ficha del pool se ve usada si en la palabra hay más apariciones de esa letra que
  // fichas iguales anteriores a ella (las letras reveladas también consumen una ficha).
  const getUsedInPool = (char: string) => userInput.filter(l => l === char).length;

  const handlePoolClick = (char: string) => {
    if (isGuessed || isLoadingNext) return;
    
    // Find first empty position in userInput that is NOT a revealed index
    const firstEmpty = userInput.findIndex((c, i) => c === '' && !revealedIndices.includes(i));
    if (firstEmpty !== -1) {
      const newInputs = [...userInput];
      newInputs[firstEmpty] = char;
      setUserInput(newInputs);
      setCursorPos(getValidCursor(firstEmpty + 1, 'forward'));
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
    setCursorPos(getValidCursor(index, 'forward'));
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
    setCursorPos(getValidCursor(0, 'forward'));
    setFeedbackMessage(null);
  };

  // Cada intento completo lo valida el servidor (submitGuess): el navegador no conoce
  // la solución. checkSeq descarta respuestas viejas si el jugador siguió escribiendo.
  const checkSeq = React.useRef(0);

  useEffect(() => {
    if (isGuessed) return;
    const fullInput = userInput.join('');
    if (fullInput.length !== wordLength || userInput.includes('')) return;

    const seq = ++checkSeq.current;
    const wordId = currentWordId;

    const checkWord = async () => {
      try {
        const result = await submitGuess(wordId, fullInput);
        if (seq !== checkSeq.current) return;

        if ('correct' in result && result.correct) {
          setIsGuessed(true);
          setMessageType('success');
          setFeedbackMessage('¡Excelente!');
          setShowParticles(true);
          soundManager.play('success');
          setNeuronas(result.neuronas);
          if (result.result) {
            setSolvedInfo({
              wordId,
              levelId: result.levelStats.levelId,
              length: fullInput.length,
              timeTaken: result.result.timeTaken,
              revealedIndices: result.result.revealedIndices,
            });
          }
          setTimeout(() => setShowParticles(false), 3000);

          try {
            const msg = await getRandomMessage('success');
            if (msg) setFeedbackMessage(msg);
            await updateSession();
          } catch (error) {
            handleActionError(error, 'fetching success message');
          }

          if (result.isLevelComplete && result.levelStats) {
            handleLevelComplete(result.levelStats);
          }
          return;
        }

        if ('error' in result && result.error === 'not_in_play') {
          // La palabra dejó de estar en juego (otra pestaña, nivel cambiado): pedir otra.
          await handleNextWord();
          return;
        }

        try {
          const msg = await getRandomMessage('fail');
          if (seq !== checkSeq.current) return;
          setFeedbackMessage(msg);
        } catch (error) {
          handleActionError(error, 'checking word (fail path)');
          setFeedbackMessage('Esa no es la palabra correcta...');
        }
        setMessageType('fail');
        soundManager.play('fail');
      } catch (error) {
        handleActionError(error, 'submitting guess');
        setFeedbackMessage('No pudimos verificar la palabra. Probá de nuevo.');
        setMessageType('fail');
      }
    };
    checkWord();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userInput, wordLength, currentWordId]);

  const handleGiveHint = async () => {
    if (isGuessed || isLoadingNext) return;
    
    setIsLoadingNext(true);
    try {
      const result = await requestHint(currentWordId, 'letter');
      if (result.success && 'revealed' in result) {
        const { index, letter } = result.revealed;
        setRevealedIndices(prev => (prev.includes(index) ? prev : [...prev, index]));
        setUserInput(prev => {
          const newInputs = [...prev];
          newInputs[index] = letter;
          return newInputs;
        });
        // Update local neuronas immediately so button enables/disables correctly
        if (result.remainingNeuronas !== undefined) {
          setNeuronas(result.remainingNeuronas);
        }
        await updateSession();
      } else if (!result.success) {
         setFeedbackMessage(result.error);
         setMessageType('fail');
      }
    } catch (error) {
       handleActionError(error, 'giving hint');
       setFeedbackMessage('Error al obtener la pista.');
       setMessageType('fail');
    }
    setIsLoadingNext(false);
  };

  return (
    <div className="space-y-4 md:space-y-6 max-w-4xl mx-auto px-2 sm:px-0">
      <AnimatePresence>
        {needsUsername && (
          <UsernameModal onSuccess={() => setNeedsUsername(false)} />
        )}
        {showInstructions && (
          <InstructionsModal isOpen={showInstructions} onClose={() => setShowInstructions(false)} />
        )}
      </AnimatePresence>

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
          <div className="flex items-center gap-2 border-l border-[#00ffff]/20 pl-3 md:pl-6 space-x-1 md:space-x-2">
            <Brain className="w-4 h-4 md:w-5 md:h-5 text-green-400" />
            <span data-testid="neuronas-count" className="font-mono text-base md:text-xl text-green-400 font-bold tabular-nums">
              {neuronas}N
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
        data-testid="game-container"
        className="bg-[#1a1033]/40 border-2 border-[#ff00ff]/20 rounded-2xl md:rounded-3xl p-4 md:p-8 backdrop-blur-xl relative overflow-hidden group focus:outline-none"
      >
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-[#ff00ff]/50 to-transparent" />
        
        {/* Definition */}
        <div className="mb-4 md:mb-8 relative">
          <div className="absolute -left-4 md:-left-6 top-0 bottom-0 w-1 bg-[#ff00ff]/30 rounded-full" />
          <h2 className="text-base md:text-xl font-medium leading-relaxed text-[#00ffff]/90 italic">
            &ldquo;{currentDescription}&rdquo;
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
              data-testid={`word-box-${index}`}
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
                  onClick={() => handlePoolClick(char)} 
                />
              ))}
            </div>
            <div className="flex justify-center gap-2">
              {letterPool.slice(7, 14).map((char, i) => (
                <LetterBox 
                  key={i+7} 
                  char={char} 
                  isUsed={getUsedInPool(char) > letterPool.slice(0, i+8).filter(l => l === char).length - 1} 
                  onClick={() => handlePoolClick(char)} 
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
                disabled={isLoadingNext || neuronas < costs.letter}
                className="w-full sm:w-auto flex items-center justify-center gap-2 px-6 py-3 bg-[#00ffff]/10 hover:bg-[#00ffff]/20 border border-[#00ffff]/40 rounded-xl transition-all group/btn disabled:opacity-50 disabled:cursor-not-allowed"
                title="Pedir letra"
              >
                <Lightbulb className="w-4 h-4 md:w-5 md:h-5 group-hover/btn:text-yellow-400 transition-colors" />
                <span className="font-mono text-xs md:text-sm font-bold">LETRA (-{costs.letter} N)</span>
              </button>
              <button 
                onClick={(e) => { e.stopPropagation(); handleSkip(); }}
                disabled={isLoadingNext || neuronas < costs.skip}
                className="w-full sm:w-auto flex items-center justify-center gap-2 px-6 py-3 bg-[#ff00ff]/10 hover:bg-[#ff00ff]/20 border border-[#ff00ff]/40 rounded-xl transition-all group/btn disabled:opacity-50 disabled:cursor-not-allowed"
                title="Saltar palabra"
              >
                <SkipForward className="w-4 h-4 md:w-5 md:h-5 group-hover/btn:translate-x-1 transition-transform" />
                <span className="font-mono text-xs md:text-sm font-bold">SALTAR (-{costs.skip} N)</span>
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

      {isGuessed && solvedInfo && (
        <SolvedPanel solved={solvedInfo} onNeedUsername={() => setNeedsUsername(true)} />
      )}

      <div className="flex justify-center gap-4 text-[#00ffff]/40 font-mono text-[9px] md:text-[10px] tracking-widest uppercase">
        <div className="flex items-center gap-1">
          <Keyboard className="w-3 h-3" /> {isMobile ? 'TOCÁ EL JUEGO PARA ESCRIBIR' : 'USÁ TU TECLADO PARA ESCRIBIR'}
        </div>
      </div>
      <AnimatePresence>
        {showParticles && <VictoryParticles />}
      </AnimatePresence>
        {gameOver && <GameOverPanel />}
        {showLevelUpModal && levelStats && !gameOver && (
          <LevelUpModal 
            level={currentLevel}
            stats={levelStats}
            onNextLevel={handleStartNextLevel}
          />
        )}

      {/* Stale Session / Refresh Modal */}
      <AnimatePresence>
        {needsRefresh && (
          <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-[#050510]/95 backdrop-blur-xl">
            <motion.div 
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              className="max-w-md w-full bg-[#1a1033] border-2 border-[#00ffff]/30 rounded-3xl p-8 text-center shadow-[0_0_50px_rgba(0,255,255,0.1)]"
            >
              <div className="w-20 h-20 bg-[#00ffff]/10 rounded-full flex items-center justify-center mx-auto mb-6 border-2 border-[#00ffff]/20">
                <Clock className="w-10 h-10 text-[#00ffff]" />
              </div>
              <h3 className="text-2xl font-black text-white mb-4 tracking-tight">¡Sincronización requerida!</h3>
              <p className="text-[#00ffff]/70 mb-8 leading-relaxed">
                El Matete ha recibido una actualización crítica del sistema. Para continuar tu partida sin errores, necesitamos que recargues la página.
              </p>
              <button
                onClick={() => window.location.reload()}
                className="w-full py-4 bg-[#00ffff] text-[#050510] rounded-2xl font-black text-lg hover:scale-105 active:scale-95 transition-all shadow-[0_0_30px_rgba(0,255,255,0.3)]"
              >
                RECARGAR AHORA
              </button>
            </motion.div>
          </div>
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
