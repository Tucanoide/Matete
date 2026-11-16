'use client';

import React, { useState } from 'react';
import { useSession, signIn, signOut } from 'next-auth/react';
import { Trophy, LogIn, LogOut, User as UserIcon, HelpCircle, Swords, Map as MapIcon } from 'lucide-react';
import Link from 'next/link';
import RankingBoard from '../game/RankingBoard';
import InstructionsModal from '../game/InstructionsModal';
import { AnimatePresence } from 'framer-motion';

export default function Header() {
  const { data: session } = useSession();
  const [showRanking, setShowRanking] = useState(false);
  const [showInstructions, setShowInstructions] = useState(false);

  return (
    <>
      {/* Dos filas: marca + jugador arriba, navegación abajo. No se pisan con ningún ancho. */}
      <header className="max-w-4xl mx-auto mb-10 md:mb-12 animate-fade-in relative z-20 space-y-4">
        <div className="flex items-center justify-between gap-4">
          <Link href="/" aria-label="Ir al juego" className="group shrink-0">
            <h1 className="flex flex-col items-start leading-none">
              <span className="text-4xl md:text-5xl font-black tracking-tighter italic text-transparent bg-clip-text bg-gradient-to-b from-[#00ffff] to-[#0088ff] drop-shadow-[0_0_10px_rgba(0,255,255,0.5)] pr-2">
                MATETE
              </span>
              <span className="mt-1 text-[9px] md:text-[11px] font-mono font-bold text-[#ff00ff]/70 tracking-[0.45em] uppercase">
                Divergente
              </span>
            </h1>
          </Link>

          <div className="flex items-center">
            {session ? (
              <div className="flex items-center gap-3 bg-[#1a1033]/60 border border-[#0088ff]/30 py-1.5 pl-1.5 pr-4 rounded-full">
                <div className="w-8 h-8 rounded-full border border-[#00ffff]/40 overflow-hidden">
                  {session.user?.image ? (
                    // Avatar de Google (host externo variable): <img> en lugar de next/image a propósito.
                    // eslint-disable-next-line @next/next/no-img-element
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
        </div>

        <nav aria-label="Secciones" className="flex flex-wrap gap-2">
          <button 
            onClick={() => setShowRanking(true)}
            className="flex items-center justify-center gap-2 px-3 py-2 md:px-4 bg-[#ff00ff]/10 hover:bg-[#ff00ff]/20 border border-[#ff00ff]/30 rounded-full transition-all group shrink-0"
          >
            <Trophy className="w-4 h-4 text-[#ff00ff] group-hover:scale-110 transition-transform" />
            <span className="font-mono text-[10px] sm:text-xs font-bold text-[#ff00ff] tracking-widest uppercase">Rankings</span>
          </button>
          
          <button 
            onClick={() => setShowInstructions(true)}
            className="flex items-center justify-center gap-2 px-3 py-2 md:px-4 bg-[#00ffff]/10 hover:bg-[#00ffff]/20 border border-[#00ffff]/30 rounded-full transition-all group shrink-0"
          >
            <HelpCircle className="w-4 h-4 text-[#00ffff] group-hover:scale-110 transition-transform" />
            <span className="font-mono text-[10px] sm:text-xs font-bold text-[#00ffff] tracking-widest uppercase">Cómo jugar</span>
          </button>

          <Link
            href="/recorrido"
            className="flex items-center justify-center gap-2 px-3 py-2 md:px-4 bg-[#4ade80]/10 hover:bg-[#4ade80]/20 border border-[#4ade80]/30 rounded-full transition-all group shrink-0"
          >
            <MapIcon className="w-4 h-4 text-[#4ade80] group-hover:scale-110 transition-transform" />
            <span className="font-mono text-[10px] sm:text-xs font-bold text-[#4ade80] tracking-widest uppercase">Mi recorrido</span>
          </Link>

          {session && (
            <Link
              href="/desafios"
              className="flex items-center justify-center gap-2 px-3 py-2 md:px-4 bg-[#ffaa00]/10 hover:bg-[#ffaa00]/20 border border-[#ffaa00]/30 rounded-full transition-all group shrink-0"
            >
              <Swords className="w-4 h-4 text-[#ffaa00] group-hover:scale-110 transition-transform" />
              <span className="font-mono text-[10px] sm:text-xs font-bold text-[#ffaa00] tracking-widest uppercase">Desafíos</span>
            </Link>
          )}
        </nav>
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
