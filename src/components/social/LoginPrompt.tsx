'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { signIn } from 'next-auth/react';
import UsernameModal from '@/components/game/UsernameModal';

export default function LoginPrompt({ reason }: { reason: 'login_required' | 'username_required' }) {
  const router = useRouter();
  const [open, setOpen] = useState(false);

  if (reason === 'username_required') {
    return (
      <div className="text-center py-10 space-y-4">
        {open && <UsernameModal onSuccess={() => { setOpen(false); router.refresh(); }} />}
        <p className="font-mono text-sm text-[#00ffff]/70">Elegí un nombre de usuario para usar los desafíos.</p>
        <button type="button" onClick={() => setOpen(true)} className="px-6 py-3 rounded-xl bg-[#00ffff] text-[#050510] font-black">ELEGIR NOMBRE</button>
      </div>
    );
  }
  return (
    <div className="text-center py-10 space-y-4">
      <p className="font-mono text-sm text-[#00ffff]/70">Ingresá con Google para ver tus desafíos.</p>
      <button type="button" onClick={() => signIn('google')} className="px-6 py-3 rounded-xl bg-[#00ffff] text-[#050510] font-black">INGRESAR CON GOOGLE</button>
    </div>
  );
}
