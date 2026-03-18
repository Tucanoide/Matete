'use client';

import React from 'react';
import { Mail, Github, ExternalLink } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="mt-20 pb-12 pt-8 border-t border-[#00ffff]/10">
      <div className="max-w-6xl mx-auto px-4 flex flex-col md:flex-row justify-between items-center gap-6">
        <div className="flex flex-col items-center md:items-start space-y-2">
          <div className="flex items-center gap-2">
            <span className="text-xl font-black italic tracking-tighter text-transparent bg-clip-text bg-gradient-to-r from-[#00ffff] to-[#ff00ff]">
              MATETE
            </span>
            <span className="text-[10px] font-mono text-[#00ffff]/40 tracking-[0.3em] uppercase">v2.0</span>
          </div>
          <p className="text-xs font-mono text-[#00ffff]/30">
            Divergente &copy; {new Date().getFullYear()} - Todos los derechos reservados.
          </p>
        </div>

        <div className="flex items-center gap-4 md:gap-8">
          <a 
            href="mailto:matetejuego@gmail.com"
            className="flex items-center gap-2 group transition-all max-w-[200px] sm:max-w-none"
          >
            <div className="p-2 bg-[#00ffff]/10 rounded-lg group-hover:bg-[#00ffff]/20 transition-colors shrink-0">
              <Mail className="w-4 h-4 text-[#00ffff]" />
            </div>
            <div className="text-left overflow-hidden">
              <p className="text-[10px] font-mono text-[#00ffff]/40 leading-none mb-1 uppercase tracking-widest">Contacto</p>
              <p className="text-sm font-bold text-[#00ffff]/80 group-hover:text-[#00ffff] transition-colors truncate sm:whitespace-normal break-all">
                matetejuego@gmail.com
              </p>
            </div>
          </a>
        </div>

        <div className="flex items-center gap-4 text-[#00ffff]/20 text-[10px] font-mono tracking-widest uppercase">
          <span>Pensamiento Lateral</span>
          <span className="w-1 h-1 bg-[#ff00ff]/30 rounded-full" />
          <span>Lógica Creativa</span>
        </div>
      </div>
    </footer>
  );
}
