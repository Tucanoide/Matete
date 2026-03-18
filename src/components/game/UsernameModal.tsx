'use client';

import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { User, CheckCircle2, AlertCircle, Loader2, Sparkles } from 'lucide-react';
import { checkUsername, updateUsername } from '@/app/actions/game';

interface UsernameModalProps {
  onSuccess: () => void;
}

export default function UsernameModal({ onSuccess }: UsernameModalProps) {
  const [username, setUsername] = useState('');
  const [status, setStatus] = useState<'idle' | 'checking' | 'available' | 'taken' | 'error'>('idle');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (username.length < 3) {
      setStatus('idle');
      return;
    }

    const timer = setTimeout(async () => {
      setStatus('checking');
      try {
        const result = await checkUsername(username);
        setStatus(result.available ? 'available' : 'taken');
      } catch (err) {
        setStatus('error');
      }
    }, 500);

    return () => clearTimeout(timer);
  }, [username]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (status !== 'available' || isSubmitting) return;

    setIsSubmitting(true);
    setError(null);

    try {
      const result = await updateUsername(username);
      if (result.success) {
        onSuccess();
      } else {
        setError(result.error || 'No se pudo guardar el nombre.');
      }
    } catch (err) {
      setError('Ocurrió un error inesperado.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-[110] flex items-center justify-center p-4 bg-[#050510]/95 backdrop-blur-xl">
      <motion.div
        initial={{ scale: 0.9, opacity: 0, y: 20 }}
        animate={{ scale: 1, opacity: 1, y: 0 }}
        className="w-full max-w-md bg-[#1a1033] border-4 border-[#ff00ff] rounded-[2rem] p-8 shadow-[0_0_100px_rgba(255,0,255,0.2)] text-center relative overflow-hidden"
      >
        {/* Animated Background Elements */}
        <div className="absolute -top-20 -left-20 w-40 h-40 bg-[#00ffff] rounded-full blur-[80px] opacity-10 animate-pulse" />
        <div className="absolute -bottom-20 -right-20 w-40 h-40 bg-[#ff00ff] rounded-full blur-[80px] opacity-10 animate-pulse" />

        <div className="relative z-10">
          <div className="inline-block p-4 bg-[#ff00ff]/10 rounded-2xl mb-6">
            <User className="w-12 h-12 text-[#ff00ff]" />
          </div>

          <h2 className="text-3xl font-black italic text-[#00ffff] mb-2 tracking-tighter uppercase">
            ¡IDENTIFICATE!
          </h2>
          <p className="text-[#00ffff]/60 text-sm mb-8 font-mono">
            Elegí un nombre de usuario para el ranking. 
            No queremos mostrar tu mail, queremos ver tu alias.
          </p>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="relative">
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value.replace(/[^a-zA-Z0-9_]/g, '').slice(0, 15))}
                placeholder="Ej: Matetero_Alpha"
                className="w-full bg-[#050510]/60 border-2 border-[#ff00ff]/30 focus:border-[#00ffff] rounded-xl px-5 py-4 text-center text-xl font-bold text-white placeholder:text-white/20 transition-all outline-none"
                autoFocus
              />
              
              <div className="absolute right-4 top-1/2 -translate-y-1/2">
                <AnimatePresence mode="wait">
                  {status === 'checking' && (
                    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
                      <Loader2 className="w-6 h-6 text-[#00ffff] animate-spin" />
                    </motion.div>
                  )}
                  {status === 'available' && (
                    <motion.div initial={{ opacity: 0, scale: 0.5 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0 }}>
                      <CheckCircle2 className="w-6 h-6 text-green-400" />
                    </motion.div>
                  )}
                  {status === 'taken' && (
                    <motion.div initial={{ opacity: 0, scale: 0.5 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0 }}>
                      <AlertCircle className="w-6 h-6 text-red-400" />
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            </div>

            {status === 'taken' && (
              <p className="text-red-400 text-xs font-mono animate-bounce">
                Ese alias ya está en uso. Probá con otro.
              </p>
            )}

            {error && (
              <p className="text-red-400 text-xs font-mono">
                {error}
              </p>
            )}

            <button
              type="submit"
              disabled={status !== 'available' || isSubmitting}
              className="w-full py-5 bg-gradient-to-r from-[#ff00ff] to-[#7000ff] text-white font-black italic text-xl rounded-2xl flex items-center justify-center gap-3 hover:scale-[1.02] active:scale-95 disabled:opacity-50 disabled:grayscale transition-all shadow-[0_0_30px_rgba(255,0,255,0.4)]"
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="w-6 h-6 animate-spin" />
                  GUARDANDO...
                </>
              ) : (
                <>
                  <Sparkles className="w-6 h-6" />
                  ESTABLECER ALIAS
                </>
              )}
            </button>
          </form>
        </div>
      </motion.div>
    </div>
  );
}
