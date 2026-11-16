'use client';

import { signIn } from 'next-auth/react';
import { LogIn } from 'lucide-react';

// Invitación a invitados (specs/003-recorrido FR-008).
export default function GoogleInvite() {
  return (
    <div className="flex flex-col sm:flex-row sm:items-center gap-3 p-4 rounded-xl bg-[#00ffff]/5 border border-[#00ffff]/20">
      <p className="flex-1 font-mono text-xs text-[#00ffff]/70">
        Estás jugando como invitado. Entrá con Google para guardar tu recorrido en tu cuenta, aparecer en el ranking y desafiar a tus amigos.
      </p>
      <button
        type="button"
        onClick={() => signIn('google')}
        className="self-start sm:self-auto flex items-center gap-2 px-4 py-2 rounded-lg bg-[#00ffff] text-[#050510] font-black text-xs"
      >
        <LogIn className="w-4 h-4" /> ENTRAR CON GOOGLE
      </button>
    </div>
  );
}
