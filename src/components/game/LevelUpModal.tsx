'use client';

import React from 'react';
import { motion } from 'framer-motion';
import { Trophy, Star, ArrowRight, Brain, Clock } from 'lucide-react';

interface LevelUpModalProps {
  level: number;
  stats: {
    solved: number;
    total: number;
  };
  onNextLevel: () => void;
}

export default function LevelUpModal({ level, stats, onNextLevel }: LevelUpModalProps) {
  return (
    <motion.div 
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-[#050510]/90 backdrop-blur-md"
    >
      <motion.div
        initial={{ scale: 0.8, y: 20 }}
        animate={{ scale: 1, y: 0 }}
        className="w-full max-w-lg bg-[#1a1033] border-4 border-[#ff00ff] rounded-[2rem] p-8 shadow-[0_0_80px_rgba(255,0,255,0.3)] text-center relative overflow-hidden"
      >
        {/* Glow Effects */}
        <div className="absolute -top-24 -left-24 w-48 h-48 bg-[#00ffff] rounded-full blur-[100px] opacity-20" />
        <div className="absolute -bottom-24 -right-24 w-48 h-48 bg-[#ff00ff] rounded-full blur-[100px] opacity-20" />

        <div className="relative z-10">
          <motion.div
            animate={{ rotate: [0, -10, 10, 0], scale: [1, 1.2, 1] }}
            transition={{ duration: 2, repeat: Infinity }}
            className="inline-block p-4 bg-[#ff00ff]/20 rounded-full mb-6"
          >
            <Trophy className="w-16 h-16 text-[#ff00ff]" />
          </motion.div>

          <h2 className="text-4xl font-black italic text-[#00ffff] mb-2 tracking-tighter">
            ¡NIVEL {level} COMPLETADO!
          </h2>
          <p className="font-mono text-[#ff00ff] text-sm tracking-[0.3em] uppercase mb-8">
            Tu cerebro está a otro nivel
          </p>

          <div className="grid grid-cols-2 gap-4 mb-10">
            <div className="bg-[#050510]/60 border border-[#00ffff]/20 p-4 rounded-2xl">
              <Brain className="w-6 h-6 text-[#00ffff] mx-auto mb-2" />
              <p className="text-[10px] font-mono text-[#00ffff]/50 uppercase">Palabras</p>
              <p className="text-2xl font-black text-[#00ffff] tracking-tight">
                {stats.solved}/{stats.total}
              </p>
            </div>
            <div className="bg-[#050510]/60 border border-[#ff00ff]/20 p-4 rounded-2xl">
              <Star className="w-6 h-6 text-[#ff00ff] mx-auto mb-2" />
              <p className="text-[10px] font-mono text-[#ff00ff]/50 uppercase">Rango</p>
              <p className="text-2xl font-black text-[#ff00ff] tracking-tight">DIVERGENTE</p>
            </div>
          </div>

          <button
            onClick={onNextLevel}
            className="w-full py-5 bg-gradient-to-r from-[#00ffff] to-[#0088ff] text-black font-black italic text-xl rounded-2xl flex items-center justify-center gap-3 hover:scale-[1.02] active:scale-95 transition-all shadow-[0_0_30px_rgba(0,255,255,0.4)]"
          >
            ACCEDER AL NIVEL {level + 1}
            <ArrowRight className="w-6 h-6" />
          </button>
        </div>
      </motion.div>
    </motion.div>
  );
}
