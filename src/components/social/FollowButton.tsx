'use client';

import React, { useState } from 'react';
import { UserPlus, UserCheck } from 'lucide-react';
import { followPlayer, unfollowPlayer } from '@/app/actions/follows';

const ERRORS: Record<string, string> = {
  username_required: 'Elegí un nombre de usuario primero',
  login_required: 'Ingresá con Google',
  limit: 'Máximo 500 seguidos',
  not_found: 'No existe',
  self: 'Sos vos',
};

interface FollowButtonProps {
  username: string;
  initialFollowed: boolean;
  onChange?: (followed: boolean) => void;
}

export default function FollowButton({ username, initialFollowed, onChange }: FollowButtonProps) {
  const [followed, setFollowed] = useState(initialFollowed);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const toggle = async (e: React.MouseEvent) => {
    e.stopPropagation();
    if (busy) return;
    setBusy(true);
    setError(null);
    try {
      const r = followed ? await unfollowPlayer(username) : await followPlayer(username);
      if (r.ok) {
        setFollowed(!followed);
        onChange?.(!followed);
      } else {
        setError(ERRORS[r.error] ?? 'Error');
      }
    } catch {
      setError('Error');
    } finally {
      setBusy(false);
    }
  };

  return (
    <button
      type="button"
      onClick={toggle}
      disabled={busy}
      title={error ?? (followed ? 'Dejar de seguir' : 'Seguir')}
      aria-label={followed ? `Dejar de seguir a ${username}` : `Seguir a ${username}`}
      className={`shrink-0 flex items-center gap-1 px-2.5 py-1.5 rounded-full border font-mono text-[10px] font-bold transition-all disabled:opacity-50 ${
        followed
          ? 'border-[#00ff88]/40 text-[#00ff88] bg-[#00ff88]/10 hover:bg-red-500/10 hover:text-red-300 hover:border-red-400/40'
          : 'border-[#00ffff]/40 text-[#00ffff] hover:bg-[#00ffff]/10'
      }`}
    >
      {followed ? <UserCheck className="w-3 h-3" /> : <UserPlus className="w-3 h-3" />}
      <span className="hidden sm:inline">{error ?? (followed ? 'SIGUIENDO' : 'SEGUIR')}</span>
    </button>
  );
}
