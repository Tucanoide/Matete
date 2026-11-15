'use client';

import React, { useState } from 'react';
import { Share2, Check } from 'lucide-react';

/** Menú nativo de compartir si existe; si no, portapapeles (002-social FR-001). */
export async function shareOrCopy(text: string): Promise<'shared' | 'copied' | 'failed'> {
  try {
    if (typeof navigator !== 'undefined' && typeof navigator.share === 'function') {
      await navigator.share({ text });
      return 'shared';
    }
  } catch (error) {
    // El usuario canceló el menú nativo: no es un error ni hace falta copiar.
    if (error instanceof Error && error.name === 'AbortError') return 'shared';
  }
  try {
    await navigator.clipboard.writeText(text);
    return 'copied';
  } catch {
    return 'failed';
  }
}

interface ShareButtonProps {
  getText: () => string | Promise<string | null> | null;
  label?: string;
  className?: string;
}

export default function ShareButton({ getText, label = 'COMPARTIR', className = '' }: ShareButtonProps) {
  const [status, setStatus] = useState<'idle' | 'copied' | 'failed' | 'busy'>('idle');

  const handleClick = async () => {
    if (status === 'busy') return;
    setStatus('busy');
    const text = await getText();
    if (!text) {
      setStatus('idle');
      return;
    }
    const result = await shareOrCopy(text);
    setStatus(result === 'copied' ? 'copied' : result === 'failed' ? 'failed' : 'idle');
    if (result !== 'shared') setTimeout(() => setStatus('idle'), 2500);
  };

  return (
    <button
      type="button"
      onClick={handleClick}
      disabled={status === 'busy'}
      className={`flex items-center justify-center gap-2 px-5 py-3 bg-[#ff00ff]/10 hover:bg-[#ff00ff]/20 border border-[#ff00ff]/40 rounded-xl transition-all font-mono text-xs md:text-sm font-bold text-[#ff00ff] disabled:opacity-60 ${className}`}
    >
      {status === 'copied' ? <Check className="w-4 h-4" /> : <Share2 className="w-4 h-4" />}
      <span>{status === 'copied' ? '¡COPIADO!' : status === 'failed' ? 'NO SE PUDO COPIAR' : label}</span>
    </button>
  );
}
