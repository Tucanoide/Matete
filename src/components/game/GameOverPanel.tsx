'use client';

import React from 'react';
import { motion } from 'framer-motion';
import { Trophy } from 'lucide-react';
import Link from 'next/link';

// PROG-09: final del juego cuando no quedan palabras en ningún nivel.
export default function GameOverPanel({ points }: { points?: number }) {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      role="dialog"
      aria-label="Fin del juego"
      className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-[#050510]/90 backdrop-blur-md"
    >
      <div className="w-full max-w-lg bg-[#1a1033] border-4 border-[#00ffff] rounded-[2rem] p-8 text-center shadow-[0_0_80px_rgba(0,255,255,0.3)]">
        <div className="inline-block p-4 bg-[#00ffff]/20 rounded-full mb-6">
          <Trophy className="w-16 h-16 text-[#00ffff]" />
        </div>
        <h2 className="text-3xl md:text-4xl font-black italic text-[#00ffff] mb-2 tracking-tighter">
          ¡COMPLETASTE MATETE!
        </h2>
        <p className="font-mono text-[#ff00ff] text-sm mb-6">
          Resolviste todos los niveles disponibles. Pronto vamos a sumar palabras nuevas.
        </p>
        {typeof points === 'number' && (
          <p className="text-2xl font-black text-[#00ffff] mb-6">{points} puntos</p>
        )}
        <div className="flex flex-col sm:flex-row gap-3 justify-center">
          <Link href="/desafios" className="px-6 py-3 bg-[#00ffff] text-black font-black rounded-xl">
            MIS DESAFÍOS
          </Link>
        </div>
      </div>
    </motion.div>
  );
}
