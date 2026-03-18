'use client';

import React, { useState } from 'react';
import { useSession, signIn, signOut } from 'next-auth/react';
import { Trophy, LogIn, LogOut, User as UserIcon, HelpCircle } from 'lucide-react';
import RankingBoard from '../game/RankingBoard';
import InstructionsModal from '../game/InstructionsModal';
import { AnimatePresence } from 'framer-motion';

export default function Header() {
  const { data: session } = useSession();
  const [showRanking, setShowRanking] = useState(false);
  const [showInstructions, setShowInstructions] = useState(false);

  return (
    <>
      <header className="flex justify-between items-center mb-12 animate-fade-in relative z-20">
        <div className="flex-1 flex flex-col sm:flex-row gap-2">
          <button 
            onClick={() => setShowRanking(true)}
            className="flex items-center justify-center sm:justify-start gap-2 px-3 py-2 md:px-4 bg-[#ff00ff]/10 hover:bg-[#ff00ff]/20 border border-[#ff00ff]/30 rounded-full transition-all group shrink-0"
          >
            <Trophy className="w-4 h-4 text-[#ff00ff] group-hover:scale-110 transition-transform" />
            <span className="font-mono text-[10px] sm:text-xs font-bold text-[#ff00ff] tracking-widest uppercase">Rankings</span>
          </button>
          
          <button 
            onClick={() => setShowInstructions(true)}
            className="flex items-center justify-center sm:justify-start gap-2 px-3 py-2 md:px-4 bg-[#00ffff]/10 hover:bg-[#00ffff]/20 border border-[#00ffff]/30 rounded-full transition-all group shrink-0"
          >
            <HelpCircle className="w-4 h-4 text-[#00ffff] group-hover:scale-110 transition-transform" />
            <span className="font-mono text-[10px] sm:text-xs font-bold text-[#00ffff] tracking-widest uppercase">Cómo jugar</span>
          </button>
        </div>

        <div className="flex-1 text-center">
          <h1 className="flex flex-col items-center">
            <span className="text-3xl md:text-6xl font-black tracking-tighter italic text-transparent bg-clip-text bg-gradient-to-b from-[#00ffff] to-[#0088ff] drop-shadow-[0_0_10px_rgba(0,255,255,0.5)] leading-none">
              MATETE
            </span>
            <span className="text-[10px] md:text-sm font-mono text-[#00ffff]/40 tracking-[0.8em] uppercase mt-1 mr-[-0.8em]">
              Divergente
            </span>
          </h1>
        </div>

        <div className="flex-1 flex justify-end items-center gap-4">
          {session ? (
            <div className="flex items-center gap-3 bg-[#1a1033]/60 border border-[#0088ff]/30 py-1.5 pl-1.5 pr-4 rounded-full">
              <div className="w-8 h-8 rounded-full border border-[#00ffff]/40 overflow-hidden">
                {session.user?.image ? (
                  <img src={session.user.image} alt={session.user.name || ''} className="w-full h-full object-cover" />
                ) : (
                  <div className="w-full h-full flex items-center justify-center bg-[#00ffff]/10">
                    <UserIcon className="w-5 h-5 text-[#00ffff]/50" />
                  </div>
                )}
              </div>
              <div className="hidden md:block text-left">
                <p className="text-[10px] font-mono text-[#00ffff]/60 leading-none mb-1">JUGADOR</p>
                <p className="text-xs font-bold text-[#00ffff] leading-none truncate max-w-[100px]">
                  {session.user?.name?.split(' ')[0].toUpperCase()}
                </p>
              </div>
              <button 
                onClick={() => signOut()}
                className="ml-2 p-1.5 hover:bg-red-500/20 text-[#00ffff]/40 hover:text-red-400 rounded-full transition-all"
                title="Cerrar sesión"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          ) : (
            <button 
              onClick={() => signIn('google')}
              className="flex items-center gap-2 px-4 py-2 bg-[#00ffff]/10 hover:bg-[#00ffff]/20 border border-[#00ffff]/30 rounded-full transition-all group"
            >
              <LogIn className="w-4 h-4 text-[#00ffff]" />
              <span className="font-mono text-xs font-bold text-[#00ffff] tracking-widest uppercase">Ingresar</span>
            </button>
          )}
        </div>
      </header>
      <AnimatePresence>
        {showRanking && (
          <RankingBoard 
            onClose={() => setShowRanking(false)} 
            currentLevel={session?.user?.levelId || 1} 
          />
        )}
        {showInstructions && (
          <InstructionsModal 
            isOpen={showInstructions} 
            onClose={() => setShowInstructions(false)} 
          />
        )}
      </AnimatePresence>
    </>
  );
}
