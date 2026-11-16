'use client';

import React, { useState } from 'react';
import { useSession, signIn } from 'next-auth/react';
import { Swords, Check } from 'lucide-react';
import { createChallenge } from '@/app/actions/challenges';
import { buildChallengeShareText } from '@/lib/share';
import { shareOrCopy } from './ShareButton';

interface ChallengeButtonProps {
  wordId: number;
  length: number;
  creatorTime: number;
  /** Versión chica para listas (página "Mis desafíos"). */
  compact?: boolean;
}

const ERRORS: Record<string, string> = {
  username_required: 'Elegí un nombre de usuario para desafiar.',
  not_solved: 'Solo podés desafiar con palabras que acertaste.',
  daily_limit: 'Llegaste al máximo de 20 desafíos por día.',
};

export default function ChallengeButton({ wordId, length, creatorTime, compact = false }: ChallengeButtonProps) {
  const { status } = useSession();
  const [state, setState] = useState<'idle' | 'busy' | 'copied'>('idle');
  const [error, setError] = useState<string | null>(null);

  if (status !== 'authenticated') {
    return (
      <button
        type="button"
        onClick={() => signIn('google')}
        className="flex items-center justify-center gap-2 px-5 py-3 bg-[#00ffff]/5 hover:bg-[#00ffff]/10 border border-[#00ffff]/30 rounded-xl font-mono text-xs md:text-sm font-bold text-[#00ffff]/70"
      >
        <Swords className="w-4 h-4" /> INGRESÁ PARA DESAFIAR
      </button>
    );
  }

  const handleClick = async () => {
    if (state === 'busy') return;
    setState('busy');
    setError(null);
    try {
      const result = await createChallenge(wordId);
      if (!result.ok) {
        setError(ERRORS[result.error] ?? 'No pudimos crear el desafío.');
        setState('idle');
        return;
      }
      const url = `${window.location.origin}/desafio/${result.code}`;
      const text = buildChallengeShareText({ username: result.username, length, creatorTime, url });
      const shared = await shareOrCopy(text);
      setState(shared === 'copied' ? 'copied' : 'idle');
      if (shared === 'copied') setTimeout(() => setState('idle'), 2500);
      if (shared === 'failed') setError(`Copiá este link: ${url}`);
    } catch {
      setError('No pudimos crear el desafío.');
      setState('idle');
    }
  };

  return (
    <div className={`flex flex-col gap-1 ${compact ? 'items-end' : 'items-center'}`}>
      <button
        type="button"
        onClick={handleClick}
        disabled={state === 'busy'}
        className={`flex items-center justify-center gap-2 bg-[#ffaa00]/10 hover:bg-[#ffaa00]/20 border border-[#ffaa00]/40 font-mono font-bold text-[#ffaa00] disabled:opacity-60 ${
          compact ? 'px-3 py-1.5 rounded-lg text-[11px]' : 'px-5 py-3 rounded-xl text-xs md:text-sm'
        }`}
      >
        {state === 'copied' ? <Check className="w-4 h-4" /> : <Swords className="w-4 h-4" />}
        {state === 'copied' ? '¡LINK COPIADO!' : compact ? 'DESAFIAR' : 'DESAFIAR A UN AMIGO'}
      </button>
      {error && <p role="alert" className="text-[11px] font-mono text-[#ffaa00]/80 text-center break-all">{error}</p>}
    </div>
  );
}
