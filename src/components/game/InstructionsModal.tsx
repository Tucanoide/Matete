'use client';

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Brain, MousePointer2, Lightbulb, Trophy, Sparkles, ArrowRight, Info, ChevronLeft, ChevronRight } from 'lucide-react';

interface InstructionsModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const DIVERGENT_EXAMPLES = [
  {
    definition: "Coloca sobre sí mismo una montura de caballo",
    parts: ["SE", "ENSILLA"],
    result: "SENCILLA",
    explanation: "Sobre sí mismo (SE) + Colocar montura (ENSILLA)",
    color: "#00ffff"
  },
  {
    definition: "Celebración del cacahuate",
    parts: ["MANÍ", "FIESTA"],
    result: "MANIFIESTA",
    explanation: "Cacahuate (MANÍ) + Celebración (FIESTA)",
    color: "#ff00ff"
  },
  {
    definition: "Baile de la vaca para cambiar de casa",
    parts: ["MU", "DANZA"],
    result: "MUDANZA",
    explanation: "Sonido de vaca (MU) + Baile (DANZA)",
    color: "#00ffff"
  }
];

export default function InstructionsModal({ isOpen, onClose }: InstructionsModalProps) {
  const [exampleIndex, setExampleIndex] = useState(0);

  const nextExample = () => setExampleIndex((prev) => (prev + 1) % DIVERGENT_EXAMPLES.length);
  const prevExample = () => setExampleIndex((prev) => (prev - 1 + DIVERGENT_EXAMPLES.length) % DIVERGENT_EXAMPLES.length);

  return (
    <div className="fixed inset-0 z-[120] flex items-center justify-center p-4 bg-[#050510]/95 backdrop-blur-xl overflow-y-auto">
      <motion.div
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        exit={{ scale: 0.9, opacity: 0 }}
        className="w-full max-w-2xl bg-[#1a1033] border-4 border-[#00ffff] rounded-[2rem] p-6 md:p-10 shadow-[0_0_100px_rgba(0,255,255,0.2)] relative overflow-hidden my-auto"
      >
        {/* Glow Effects */}
        <div className="absolute -top-20 -right-20 w-40 h-40 bg-[#ff00ff] rounded-full blur-[80px] opacity-10 animate-pulse" />
        
        <button 
          onClick={onClose}
          className="absolute top-6 right-6 p-2 hover:bg-white/10 rounded-full transition-colors text-[#00ffff] z-20"
        >
          <X className="w-6 h-6" />
        </button>

        <div className="relative z-10">
          <div className="flex items-center gap-4 mb-8">
            <div className="p-3 bg-[#00ffff]/10 rounded-2xl">
              <Sparkles className="w-8 h-8 text-[#00ffff]" />
            </div>
            <div>
              <h2 className="text-3xl font-black italic text-[#00ffff] tracking-tighter uppercase leading-none">
                Cómo Jugar
              </h2>
              <p className="text-[#00ffff]/60 font-mono text-xs uppercase tracking-widest mt-1">Manual del Sistema Matete</p>
            </div>
          </div>

          <div className="space-y-6 text-[#00ffff]/90">
            {/* Divergent Thinking Section - THE CORE CHANGE */}
            <div className="bg-white/5 border border-white/10 rounded-3xl p-5 space-y-4">
              <div className="flex items-center gap-2 text-[#ff00ff]">
                <Brain className="w-6 h-6 animate-pulse" />
                <h3 className="text-xl font-black italic uppercase italic tracking-tight">Pensamiento Divergente</h3>
              </div>
              <p className="text-sm font-medium leading-relaxed opacity-80">
                ¡Matete no es un juego común! Las palabras se esconden tras <span className="text-[#ff00ff] font-bold">asociaciones fonéticas</span> y lógica lateral. Para ganar, debés pensar fuera de la caja.
              </p>

              {/* Example Carousel */}
              <div className="relative bg-[#050510]/50 rounded-2xl p-4 border border-[#ff00ff]/20">
                <AnimatePresence mode="wait">
                  <motion.div
                    key={exampleIndex}
                    initial={{ opacity: 0, x: 20 }}
                    animate={{ opacity: 1, x: 0 }}
                    exit={{ opacity: 0, x: -20 }}
                    className="space-y-3"
                  >
                    <div className="text-xs font-mono uppercase text-[#ff00ff]/60 tracking-widest">Ejemplo {exampleIndex + 1}</div>
                    <p className="italic font-bold text-lg text-white">"{DIVERGENT_EXAMPLES[exampleIndex].definition}"</p>
                    
                    <div className="flex items-center justify-center gap-2 py-2">
                       {DIVERGENT_EXAMPLES[exampleIndex].parts.map((part, i) => (
                         <React.Fragment key={i}>
                           <div className="px-3 py-1 bg-white/10 rounded-lg font-black text-[#00ffff] border border-[#00ffff]/30">
                             {part}
                           </div>
                           {i < DIVERGENT_EXAMPLES[exampleIndex].parts.length - 1 && (
                             <span className="text-white/40 font-black">+</span>
                           )}
                         </React.Fragment>
                       ))}
                       <ArrowRight className="w-4 h-4 text-[#ff00ff]" />
                       <div className="px-3 py-1 bg-[#ff00ff]/20 rounded-lg font-black text-[#ff00ff] border border-[#ff00ff]/50">
                         {DIVERGENT_EXAMPLES[exampleIndex].result}
                       </div>
                    </div>
                    
                    <p className="text-xs italic opacity-60 text-center">
                      {DIVERGENT_EXAMPLES[exampleIndex].explanation}
                    </p>
                  </motion.div>
                </AnimatePresence>

                {/* Carousel Controls */}
                <div className="flex justify-between mt-4">
                  <button onClick={prevExample} className="p-1 hover:bg-white/10 rounded-full transition-colors text-[#ff00ff]">
                    <ChevronLeft className="w-5 h-5" />
                  </button>
                  <div className="flex gap-1 items-center">
                    {DIVERGENT_EXAMPLES.map((_, i) => (
                      <div 
                        key={i} 
                        className={`w-1.5 h-1.5 rounded-full transition-all ${i === exampleIndex ? 'bg-[#ff00ff] w-4' : 'bg-white/20'}`} 
                      />
                    ))}
                  </div>
                  <button onClick={nextExample} className="p-1 hover:bg-white/10 rounded-full transition-colors text-[#ff00ff]">
                    <ChevronRight className="w-5 h-5" />
                  </button>
                </div>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Step 1 */}
              <div className="flex gap-3 bg-white/5 p-4 rounded-2xl border border-white/5">
                <div className="flex-shrink-0 w-8 h-8 rounded-full bg-[#00ffff]/20 flex items-center justify-center font-black italic border border-[#00ffff]/50 text-xs text-[#00ffff]">
                  01
                </div>
                <div className="space-y-1">
                  <h3 className="font-bold italic flex items-center gap-2 text-sm uppercase">
                    <MousePointer2 className="w-4 h-4 text-[#00ffff]" /> Mecánica
                  </h3>
                  <p className="text-xs leading-relaxed opacity-70">
                    Clic en las letras del <span className="text-[#00ffff] font-bold">Pool</span> para armar la palabra. Clic en la palabra para borrar.
                  </p>
                </div>
              </div>

              {/* Step 2 */}
              <div className="flex gap-3 bg-white/5 p-4 rounded-2xl border border-white/5">
                <div className="flex-shrink-0 w-8 h-8 rounded-full bg-[#ff00ff]/20 flex items-center justify-center font-black italic border border-[#ff00ff]/50 text-xs text-[#ff00ff]">
                  02
                </div>
                <div className="space-y-1">
                  <h3 className="font-bold italic flex items-center gap-2 text-sm uppercase text-[#ff00ff]">
                    <Lightbulb className="w-4 h-4" /> Ayuda
                  </h3>
                  <p className="text-xs leading-relaxed opacity-70">
                    ¿Trabas? Usá <span className="text-[#ff00ff] font-bold">Neuronas</span> para comprar una pista o saltar el nivel.
                  </p>
                </div>
              </div>
            </div>
          </div>

          <button
            onClick={onClose}
            className="mt-8 w-full py-4 bg-[#00ffff] text-black font-black italic text-lg rounded-2xl flex items-center justify-center gap-3 hover:scale-[1.02] active:scale-95 transition-all shadow-[0_0_30px_rgba(0,255,255,0.4)]"
          >
            ¡LISTO PARA EL MATETE!
          </button>
        </div>
      </motion.div>
    </div>
  );
}

