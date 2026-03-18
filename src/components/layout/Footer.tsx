'use client';

import React from 'react';
import { Mail, Github, ExternalLink } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="mt-20 pb-8 pt-12 border-t border-[#00ffff]/10 relative">
      <div className="max-w-6xl mx-auto px-4">
        <div className="grid grid-cols-1 md:grid-cols-3 items-center gap-8 text-center md:text-left">
          {/* Brand Section */}
          <div className="flex flex-col items-center md:items-start space-y-2 order-2 md:order-1">
            <div className="flex items-center gap-2">
              <span className="text-xl font-black italic tracking-tighter text-transparent bg-clip-text bg-gradient-to-r from-[#00ffff] to-[#ff00ff]">
                MATETE
              </span>
              <span className="text-[10px] font-mono text-[#00ffff]/40 tracking-[0.3em] uppercase">v2.0</span>
            </div>
            <p className="text-[10px] font-mono text-[#00ffff]/30">
              Divergente &copy; {new Date().getFullYear()} - Todos los derechos reservados.
            </p>
          </div>

          {/* Contact Section */}
          <div className="flex justify-center order-1 md:order-2">
            <a 
              href="mailto:matetejuego@gmail.com"
              className="flex items-center gap-3 px-4 py-2 bg-[#00ffff]/5 border border-[#00ffff]/10 rounded-xl group hover:bg-[#00ffff]/10 hover:border-[#00ffff]/30 transition-all"
            >
              <div className="p-2 bg-[#00ffff]/10 rounded-lg group-hover:bg-[#00ffff]/20 transition-colors">
                <Mail className="w-4 h-4 text-[#00ffff]" />
              </div>
              <span className="text-xs font-bold text-[#00ffff]/80 group-hover:text-[#00ffff] transition-colors font-mono tracking-tight">
                matetejuego@gmail.com
              </span>
            </a>
          </div>

          {/* Tagline Section */}
          <div className="flex flex-col items-center md:items-end space-y-3 order-3">
            <div className="flex items-center gap-4 text-[#00ffff]/30 text-[10px] font-mono tracking-widest uppercase">
              <span>Pensamiento Lateral</span>
              <span className="w-1 h-1 bg-[#ff00ff]/30 rounded-full" />
              <span>Lógica Creativa</span>
            </div>
            <div className="text-[10px] font-mono text-[#00ffff]/20 tracking-[0.2em] uppercase">
              Retro System v4.1 // Matete Desktop
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
}
