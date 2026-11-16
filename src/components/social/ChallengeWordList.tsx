'use client';

import React from 'react';
import Link from 'next/link';
import type { ChallengeableWord } from '@/app/actions/challenges';
import { formatDuration } from '@/lib/share';
import ChallengeButton from './ChallengeButton';

// "Mis desafíos" → crear uno a partir de una palabra ya acertada (SOC-38).
export default function ChallengeWordList({ words }: { words: ChallengeableWord[] }) {
  if (words.length === 0) {
    return (
      <div className="flex flex-col sm:flex-row sm:items-center gap-3 p-4 rounded-xl bg-[#050510]/60 border border-[#00ffff]/10">
        <p className="flex-1 font-mono text-xs text-[#00ffff]/60">
          Para desafiar necesitás acertar una palabra con tu cuenta. Tus amigos juegan la misma palabra contra tu tiempo.
        </p>
        <Link href="/" className="self-start sm:self-auto px-4 py-2 rounded-lg bg-[#00ffff] text-[#050510] font-black text-xs">
          IR A JUGAR
        </Link>
      </div>
    );
  }

  return (
    <>
      <p className="font-mono text-xs text-[#00ffff]/50 mb-3">
        Elegí una palabra que ya acertaste. Se copia un link: tus amigos la juegan contra tu tiempo, sin ver la palabra.
      </p>
      <ul className="space-y-2">
        {words.map((w) => (
          <li key={w.wordId} className="flex items-center gap-3 p-3 rounded-xl bg-[#050510]/60 border border-[#00ffff]/10">
            <span className="flex-1 min-w-0">
              <span className="block truncate font-black tracking-wider text-sm text-[#00ffff]">{w.word}</span>
              <span className="font-mono text-[10px] text-[#00ffff]/50">
                {w.length} letras · tu tiempo {formatDuration(w.timeTaken)}
                {w.challenged && ' · ya lo compartiste'}
              </span>
            </span>
            <ChallengeButton wordId={w.wordId} length={w.length} creatorTime={w.timeTaken} compact />
          </li>
        ))}
      </ul>
    </>
  );
}
