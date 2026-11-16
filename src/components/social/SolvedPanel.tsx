'use client';

import React from 'react';
import ShareButton from './ShareButton';
import ChallengeButton from './ChallengeButton';
import { CommentsToggle } from './CommentsPanel';
import { buildShareText } from '@/lib/share';

// Lo social que aparece tras acertar una palabra (002-social US1, US2, US4).
// data-social: el GameEngine ignora el teclado dentro de este panel (escribir un comentario
// no debe disparar "siguiente palabra" con Enter).

export type SolvedInfo = {
  wordId: number;
  levelId: number;
  length: number;
  timeTaken: number;
  revealedIndices: number[];
};

interface SolvedPanelProps {
  solved: SolvedInfo;
  onNeedUsername?: () => void;
}

export default function SolvedPanel({ solved, onNeedUsername }: SolvedPanelProps) {
  const shareText = () =>
    buildShareText({
      levelId: solved.levelId,
      length: solved.length,
      revealedIndices: solved.revealedIndices,
      timeTaken: solved.timeTaken,
      url: window.location.origin,
    });

  return (
    <div data-social className="bg-[#1a1033]/40 border-2 border-[#00ffff]/15 rounded-2xl p-4 md:p-6 backdrop-blur-xl space-y-4">
      <div className="flex flex-col sm:flex-row justify-center gap-3">
        <ShareButton getText={shareText} />
        <ChallengeButton wordId={solved.wordId} length={solved.length} creatorTime={solved.timeTaken} />
      </div>
      <CommentsToggle key={solved.wordId} wordId={solved.wordId} onNeedUsername={onNeedUsername} />
    </div>
  );
}
