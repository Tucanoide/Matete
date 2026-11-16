'use client';

import React, { useCallback, useEffect, useRef, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { signIn } from 'next-auth/react';
import { Swords, Timer, Trophy, Flag, Trash2, User as UserIcon, Check, X } from 'lucide-react';
import { startChallenge, submitChallengeGuess, giveUpChallenge, type ChallengeView, type ChallengeResult } from '@/app/actions/challenges';
import { buildChallengeShareText, formatDuration } from '@/lib/share';
import UsernameModal from '@/components/game/UsernameModal';
import ShareButton from './ShareButton';

// Juego de un desafío (002-social US4). Sin pistas, sin salto, sin neuronas (FR-028).
// El cronómetro de pantalla es solo visual: el tiempo que cuenta lo mide el servidor.

type Phase = 'intro' | 'playing' | 'done';
type Slot = { letter: string; pool: number | null };
const emptySlots = (n: number): Slot[] => Array.from({ length: n }, () => ({ letter: '', pool: null }));
type Outcome = { solution: string; timeTaken: number | null; won: boolean; knewWord: boolean; gaveUp: boolean };

const START_ERRORS: Record<string, string> = {
  own_challenge: 'No podés jugar tu propio desafío.',
  finished: 'Ya jugaste este desafío.',
  not_found: 'Este desafío no existe.',
  login_required: 'Ingresá con Google para jugar.',
  username_required: 'Elegí un nombre de usuario para jugar.',
};

function ResultRow({ r, index }: { r: ChallengeResult; index: number }) {
  return (
    <li className="flex items-center gap-3 p-3 rounded-xl bg-[#050510]/60 border border-[#00ffff]/10">
      <span className="w-5 text-center font-black italic text-[#00ffff]/60">{index + 1}</span>
      <div className="w-8 h-8 rounded-full overflow-hidden border border-[#ff00ff]/40 bg-[#1a1033] shrink-0">
        {r.image ? (
          // eslint-disable-next-line @next/next/no-img-element
          <img src={r.image} alt="" className="w-full h-full object-cover" />
        ) : (
          <UserIcon className="w-full h-full p-1.5 text-[#ff00ff]/50" />
        )}
      </div>
      <span className="flex-1 truncate font-bold text-sm text-[#00ffff]">@{r.username}</span>
      <span className="font-mono text-xs text-[#00ffff]/80">
        {r.solved ? formatDuration(r.timeTaken ?? 0) : 'se rindió'}
        {r.knewWord && <span className="ml-1 text-[#ffaa00]/80">(ya la conocía)</span>}
      </span>
      {r.won ? <Trophy className="w-4 h-4 text-yellow-400" aria-label="Ganó" /> : <span className="w-4" />}
    </li>
  );
}

export default function ChallengePlay({ view }: { view: ChallengeView }) {
  const router = useRouter();
  const [phase, setPhase] = useState<Phase>(view.viewer === 'finished' ? 'done' : 'intro');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [needsUsername, setNeedsUsername] = useState(false);

  const [description, setDescription] = useState('');
  const [pool, setPool] = useState<string[]>([]);
  // Un casillero por letra; `pool` = índice de la ficha usada (null si se tipeó una letra fuera del pool).
  const [slots, setSlots] = useState<Slot[]>(() => emptySlots(view.length));
  const input = slots.map((s) => s.letter);
  const usedPool = slots.flatMap((s) => (s.pool === null ? [] : [s.pool]));
  const poolRef = useRef<string[]>([]);
  useEffect(() => {
    poolRef.current = pool;
  }, [pool]);
  const [elapsed, setElapsed] = useState(0);
  const [wrong, setWrong] = useState(false);
  const [outcome, setOutcome] = useState<Outcome | null>(
    view.viewer === 'finished' && view.myResult && view.solution
      ? { solution: view.solution, timeTaken: view.myResult.timeTaken, won: view.myResult.won, knewWord: view.myResult.knewWord, gaveUp: view.myResult.gaveUp }
      : null
  );
  const checking = useRef(false);

  // Cronómetro visual.
  useEffect(() => {
    if (phase !== 'playing') return;
    const t = setInterval(() => setElapsed((e) => e + 1), 1000);
    return () => clearInterval(t);
  }, [phase]);

  const handleStart = async () => {
    setBusy(true);
    setError(null);
    try {
      const r = await startChallenge(view.code);
      if (!r.ok) {
        if (r.error === 'username_required') setNeedsUsername(true);
        else setError(START_ERRORS[r.error] ?? 'No pudimos empezar el desafío.');
        if (r.error === 'finished') router.refresh();
        return;
      }
      setDescription(r.word.description);
      setPool(r.word.pool);
      setSlots(emptySlots(r.word.length));
      setElapsed(Math.floor(r.elapsed));
      setPhase('playing');
    } catch {
      setError('No pudimos empezar el desafío.');
    } finally {
      setBusy(false);
    }
  };

  const typeLetter = useCallback((letter: string, poolIndex: number | null) => {
    setWrong(false);
    setSlots((prev) => {
      const i = prev.findIndex((sl) => sl.letter === '');
      if (i === -1) return prev;
      const next = [...prev];
      next[i] = { letter: letter.toUpperCase(), pool: poolIndex };
      return next;
    });
  }, []);

  const backspace = useCallback(() => {
    setWrong(false);
    setSlots((prev) => {
      let i = prev.length - 1;
      while (i >= 0 && prev[i].letter === '') i--;
      if (i < 0) return prev;
      const next = [...prev];
      next[i] = { letter: '', pool: null };
      return next;
    });
  }, []);

  const clearAll = () => {
    setWrong(false);
    setSlots((prev) => emptySlots(prev.length));
  };

  // Teclado físico: si la letra está libre en el pool se usa esa ficha; si no, se escribe igual.
  const slotsRef = useRef<Slot[]>(slots);
  useEffect(() => {
    slotsRef.current = slots;
  }, [slots]);
  useEffect(() => {
    if (phase !== 'playing') return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Backspace') {
        backspace();
        return;
      }
      if (e.key.length !== 1 || !/^[a-zA-ZñÑ]$/.test(e.key)) return;
      const letter = e.key.toUpperCase();
      const used = slotsRef.current.flatMap((sl) => (sl.pool === null ? [] : [sl.pool]));
      const idx = poolRef.current.findIndex((c, i) => c === letter && !used.includes(i));
      typeLetter(letter, idx === -1 ? null : idx);
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [phase, backspace, typeLetter]);

  // Intento completo → lo valida el servidor.
  const guessKey = input.join('|');
  useEffect(() => {
    const letters = guessKey.split('|');
    if (phase !== 'playing' || letters.includes('') || checking.current) return;
    checking.current = true;
    const guess = letters.join('');
    submitChallengeGuess(view.code, guess)
      .then((r) => {
        if ('correct' in r && r.correct) {
          setOutcome({ solution: r.solution, timeTaken: r.timeTaken, won: r.won, knewWord: r.knewWord, gaveUp: false });
          setPhase('done');
          router.refresh();
        } else if ('correct' in r) {
          setWrong(true);
        } else if (r.error === 'finished') {
          router.refresh();
          setPhase('done');
        } else {
          setError('No pudimos verificar la palabra.');
        }
      })
      .catch(() => setError('No pudimos verificar la palabra.'))
      .finally(() => {
        checking.current = false;
      });
  }, [guessKey, phase, view.code, router]);

  const handleGiveUp = async () => {
    setBusy(true);
    try {
      const r = await giveUpChallenge(view.code);
      if (r.ok) {
        setOutcome({ solution: r.solution, timeTaken: null, won: false, knewWord: false, gaveUp: true });
        setPhase('done');
        router.refresh();
      }
    } finally {
      setBusy(false);
    }
  };

  const shareText = () =>
    buildChallengeShareText({ username: view.creator.username, length: view.length, creatorTime: view.creatorTime, url: window.location.href });

  return (
    <div className="space-y-6">
      {needsUsername && (
        <UsernameModal
          onSuccess={() => {
            setNeedsUsername(false);
            router.refresh();
          }}
        />
      )}

      {/* Encabezado del desafío */}
      <div className="text-center bg-[#1a1033]/60 border-2 border-[#ffaa00]/30 rounded-3xl p-6 backdrop-blur-xl">
        <Swords className="w-10 h-10 mx-auto text-[#ffaa00] mb-3" />
        <h1 className="text-2xl md:text-3xl font-black text-white">@{view.creator.username} te desafía</h1>
        <p className="mt-2 font-mono text-sm text-[#00ffff]/80">
          Una palabra de <b>{view.length}</b> letras. Tiempo a superar: <b className="text-[#ffaa00]">{formatDuration(view.creatorTime)}</b>
          {view.creatorHints > 0 && <span className="text-[#00ffff]/50"> (usó {view.creatorHints} {view.creatorHints === 1 ? 'pista' : 'pistas'})</span>}
        </p>
      </div>

      {phase === 'intro' && (
        <div className="flex flex-col items-center gap-3">
          {view.viewer === 'anonymous' && (
            <button type="button" onClick={() => signIn('google', { callbackUrl: `/desafio/${view.code}` })} className="px-8 py-4 rounded-2xl bg-[#00ffff] text-[#050510] font-black text-lg">
              INGRESÁ CON GOOGLE PARA JUGAR
            </button>
          )}
          {view.viewer === 'username_required' && (
            <button type="button" onClick={() => setNeedsUsername(true)} className="px-8 py-4 rounded-2xl bg-[#00ffff] text-[#050510] font-black text-lg">
              ELEGÍ TU NOMBRE PARA JUGAR
            </button>
          )}
          {(view.viewer === 'can_play' || view.viewer === 'in_progress') && (
            <>
              <button type="button" onClick={handleStart} disabled={busy} className="px-8 py-4 rounded-2xl bg-[#ffaa00] text-[#050510] font-black text-lg disabled:opacity-60">
                {view.viewer === 'in_progress' ? 'CONTINUAR (EL TIEMPO SIGUE CORRIENDO)' : 'ACEPTAR DESAFÍO'}
              </button>
              <p className="font-mono text-[11px] text-[#00ffff]/50 text-center max-w-sm">
                El tiempo empieza a correr al aceptar y no se pausa. Sin pistas ni saltos. No suma ni resta neuronas.
              </p>
            </>
          )}
          {view.viewer === 'creator' && (
            <>
              <p className="font-mono text-sm text-[#00ffff]/70">Es tu desafío. Compartilo con tus amigos.</p>
              <ShareButton getText={shareText} label="COMPARTIR DESAFÍO" />
            </>
          )}
          {error && <p role="alert" className="font-mono text-xs text-red-400">{error}</p>}
        </div>
      )}

      {phase === 'playing' && (
        <div className="bg-[#1a1033]/40 border-2 border-[#ff00ff]/20 rounded-3xl p-4 md:p-8 backdrop-blur-xl space-y-6">
          <div className="flex justify-between items-center font-mono text-sm">
            <span className="flex items-center gap-2 text-[#00ffff]"><Timer className="w-4 h-4" /> {formatDuration(elapsed)}</span>
            <span className="text-[#ffaa00]">a superar: {formatDuration(view.creatorTime)}</span>
          </div>
          <h2 className="text-base md:text-xl italic text-[#00ffff]/90">&quot;{description}&quot;</h2>
          <div className="flex flex-wrap justify-center gap-2" data-testid="challenge-boxes">
            {input.map((c, i) => (
              <div
                key={i}
                data-testid={`challenge-box-${i}`}
                className={`w-9 h-12 md:w-12 md:h-16 flex items-center justify-center text-2xl md:text-4xl font-black rounded-lg border-b-4 bg-[#050510]/60 ${
                  wrong ? 'border-red-500 text-red-300' : 'border-white/20 text-[#00ffff]'
                }`}
              >
                {c}
              </div>
            ))}
            <button type="button" onClick={clearAll} title="Limpiar" aria-label="Limpiar palabra" className="p-2 text-red-400/50 hover:text-red-400">
              <Trash2 className="w-5 h-5" />
            </button>
          </div>
          {wrong && <p role="status" className="text-center font-mono text-sm text-red-400">No es esa. Seguí intentando.</p>}
          <div className="flex flex-wrap justify-center gap-2 max-w-md mx-auto">
            {pool.map((c, i) => {
              const used = usedPool.includes(i);
              return (
                <button
                  key={i}
                  type="button"
                  disabled={used}
                  onClick={() => typeLetter(c, i)}
                  className={`w-10 h-12 md:w-12 md:h-14 rounded-lg border-2 text-xl font-black ${
                    used ? 'bg-black/40 border-white/5 text-white/20' : 'bg-[#050510] border-[#00ffff]/20 text-[#00ffff] hover:border-[#00ffff]'
                  }`}
                >
                  {c}
                </button>
              );
            })}
          </div>
          <div className="flex justify-center gap-3">
            <button type="button" onClick={backspace} className="px-4 py-2 rounded-xl border border-[#00ffff]/30 font-mono text-xs text-[#00ffff]">BORRAR</button>
            <button type="button" onClick={handleGiveUp} disabled={busy} className="flex items-center gap-2 px-4 py-2 rounded-xl border border-red-400/40 font-mono text-xs text-red-300">
              <Flag className="w-3.5 h-3.5" /> ME RINDO
            </button>
          </div>
          {error && <p role="alert" className="text-center font-mono text-xs text-red-400">{error}</p>}
        </div>
      )}

      {phase === 'done' && outcome && (
        <div className="text-center bg-[#1a1033]/60 border-2 border-[#00ffff]/30 rounded-3xl p-6 space-y-3">
          {outcome.gaveUp ? (
            <p className="text-xl font-black text-red-300 flex items-center justify-center gap-2"><X className="w-6 h-6" /> Te rendiste</p>
          ) : outcome.won ? (
            <p className="text-2xl font-black text-yellow-300 flex items-center justify-center gap-2"><Trophy className="w-7 h-7" /> ¡Le ganaste a @{view.creator.username}!</p>
          ) : outcome.knewWord ? (
            <p className="text-xl font-black text-[#ffaa00] flex items-center justify-center gap-2"><Check className="w-6 h-6" /> La resolviste, pero ya la conocías</p>
          ) : (
            <p className="text-xl font-black text-[#00ffff] flex items-center justify-center gap-2"><Check className="w-6 h-6" /> La resolviste, pero no llegaste a tiempo</p>
          )}
          <p className="font-mono text-sm text-[#00ffff]/80">
            La palabra era <b className="text-white tracking-widest">{outcome.solution}</b>
          </p>
          {outcome.timeTaken !== null && (
            <p className="font-mono text-sm text-[#00ffff]/70">
              Tu tiempo: <b>{formatDuration(outcome.timeTaken)}</b> · @{view.creator.username}: <b>{formatDuration(view.creatorTime)}</b>
            </p>
          )}
          <Link href="/" className="inline-block mt-2 px-6 py-3 rounded-xl bg-[#00ffff] text-[#050510] font-black">SEGUIR JUGANDO MATETE</Link>
        </div>
      )}
      {phase === 'done' && !outcome && (
        <p className="text-center font-mono text-sm text-[#00ffff]/70">Ya jugaste este desafío.</p>
      )}

      {/* Resultados (FR-022, FR-029) */}
      <div className="bg-[#1a1033]/40 border border-[#00ffff]/15 rounded-3xl p-4 md:p-6">
        <h3 className="font-black italic text-[#00ffff] mb-3">RESULTADOS</h3>
        {view.results.length === 0 ? (
          <p className="font-mono text-xs text-[#00ffff]/40">Todavía nadie lo jugó.</p>
        ) : (
          <ol className="space-y-2">
            {view.results.map((r, i) => (
              <ResultRow key={`${i}-${r.username}`} r={r} index={i} />
            ))}
          </ol>
        )}
      </div>
    </div>
  );
}
