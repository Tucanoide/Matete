'use client';

import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Brain, MousePointer2, Lightbulb, Trophy, Sparkles } from 'lucide-react';

interface InstructionsModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function InstructionsModal({ isOpen, onClose }: InstructionsModalProps) {
  return (
    <div className="fixed inset-0 z-[120] flex items-center justify-center p-4 bg-[#050510]/95 backdrop-blur-xl">
      <motion.div
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        exit={{ scale: 0.9, opacity: 0 }}
        className="w-full max-w-2xl bg-[#1a1033] border-4 border-[#00ffff] rounded-[2rem] p-6 md:p-10 shadow-[0_0_100px_rgba(0,255,255,0.2)] relative overflow-hidden"
      >
        {/* Glow Effects */}
        <div className="absolute -top-20 -right-20 w-40 h-40 bg-[#ff00ff] rounded-full blur-[80px] opacity-10 animate-pulse" />
        
        <button 
          onClick={onClose}
          className="absolute top-6 right-6 p-2 hover:bg-white/10 rounded-full transition-colors text-[#00ffff]"
        >
          <X className="w-6 h-6" />
        </button>

        <div className="relative z-10">
          <div className="flex items-center gap-4 mb-8">
            <div className="p-3 bg-[#00ffff]/10 rounded-2xl">
              <Sparkles className="w-8 h-8 text-[#00ffff]" />
            </div>
            <div>
              <h2 className="text-3xl font-black italic text-[#00ffff] tracking-tighter uppercase">
                Cómo Jugar
              </h2>
              <p className="text-[#00ffff]/60 font-mono text-sm uppercase tracking-widest">Manual del Sistema Matete</p>
            </div>
          </div>

          <div className="space-y-8 text-[#00ffff]/90">
            {/* Step 1 */}
            <div className="flex gap-4">
              <div className="flex-shrink-0 w-10 h-10 rounded-full bg-[#00ffff]/20 flex items-center justify-center font-black italic border border-[#00ffff]/50">
                01
              </div>
              <div className="space-y-2">
                <h3 className="text-xl font-bold italic flex items-center gap-2">
                  <Brain className="w-5 h-5" /> LA DEFINICIÓN
                </h3>
                <p className="font-sans leading-relaxed opacity-80">
                  Lee la definición en pantalla. Tu objetivo es adivinar la palabra oculta. 
                  ¡Usa tu ingenio para descifrar el concepto!
                </p>
              </div>
            </div>

            {/* Step 2 */}
            <div className="flex gap-4">
              <div className="flex-shrink-0 w-10 h-10 rounded-full bg-[#ff00ff]/20 flex items-center justify-center font-black italic border border-[#ff00ff]/50 text-[#ff00ff]">
                02
              </div>
              <div className="space-y-2">
                <h3 className="text-xl font-bold italic flex items-center gap-2 text-[#ff00ff]">
                  <MousePointer2 className="w-5 h-5" /> ARMANDO LA PALABRA
                </h3>
                <p className="font-sans leading-relaxed opacity-80">
                  Usa el <span className="text-[#ff00ff] font-bold">Pool de Letras</span> (las 14 letras de abajo). 
                  Haz clic en una letra para colocarla en el primer espacio vacío. 
                  Si te equivocas, haz clic en una letra de la palabra para borrarla.
                </p>
              </div>
            </div>

            {/* Step 3 */}
            <div className="flex gap-4">
              <div className="flex-shrink-0 w-10 h-10 rounded-full bg-[#00ffff]/20 flex items-center justify-center font-black italic border border-[#00ffff]/50">
                03
              </div>
              <div className="space-y-2">
                <h3 className="text-xl font-bold italic flex items-center gap-2">
                  <Lightbulb className="w-5 h-5" /> PISTAS Y NEURONAS
                </h3>
                <p className="font-sans leading-relaxed opacity-80">
                  ¿Te trabaste? Usa tus <span className="text-[#00ffff] font-bold">Neuronas</span> para comprar una pista. 
                  La pista revelará una letra en su posición correcta de forma permanente.
                </p>
              </div>
            </div>

            {/* Step 4 */}
            <div className="flex gap-4">
              <div className="flex-shrink-0 w-10 h-10 rounded-full bg-[#ff00ff]/20 flex items-center justify-center font-black italic border border-[#ff00ff]/50 text-[#ff00ff]">
                04
              </div>
              <div className="space-y-2">
                <h3 className="text-xl font-bold italic flex items-center gap-2 text-[#ff00ff]">
                  <Trophy className="w-5 h-5" /> NIVELES Y RANKING
                </h3>
                <p className="font-sans leading-relaxed opacity-80">
                  Al completar el nivel, ganarás neuronas extra y avanzarás en el ranking. 
                  ¡Tu alias aparecerá en el Top mundial!
                </p>
              </div>
            </div>
          </div>

          <button
            onClick={onClose}
            className="mt-10 w-full py-5 bg-[#00ffff] text-black font-black italic text-xl rounded-2xl flex items-center justify-center gap-3 hover:scale-[1.02] active:scale-95 transition-all shadow-[0_0_30px_rgba(0,255,255,0.4)]"
          >
            ¡ENTENDIDO, A JUGAR!
          </button>
        </div>
      </motion.div>
    </div>
  );
}
