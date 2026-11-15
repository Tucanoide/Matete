'use client';

import React, { useCallback, useEffect, useState } from 'react';
import { signIn } from 'next-auth/react';
import { Search, User as UserIcon, Trophy, Clock, Users } from 'lucide-react';
import { getFriendsRanking, searchPlayers, type FriendsRankingEntry } from '@/app/actions/follows';
import { formatDuration } from '@/lib/share';
import FollowButton from './FollowButton';

// Pestaña "Amigos" del ranking (002-social US3).

type Player = { username: string; image: string | null; isFollowed: boolean };

function Avatar({ image }: { image: string | null }) {
  return (
    <div className="w-9 h-9 shrink-0 rounded-full border border-[#ff00ff]/40 overflow-hidden bg-[#1a1033]">
      {image ? (
        // eslint-disable-next-line @next/next/no-img-element
        <img src={image} alt="" className="w-full h-full object-cover" />
      ) : (
        <UserIcon className="w-full h-full p-2 text-[#ff00ff]/50" />
      )}
    </div>
  );
}

export default function FriendsTab({ onNeedUsername }: { onNeedUsername?: () => void }) {
  const [state, setState] = useState<'loading' | 'ready' | 'login_required' | 'username_required' | 'error'>('loading');
  const [entries, setEntries] = useState<FriendsRankingEntry[]>([]);
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<Player[] | null>(null);

  const loadRanking = useCallback(async () => {
    try {
      const r = await getFriendsRanking();
      if (r.ok) {
        setEntries(r.entries);
        setState('ready');
      } else {
        setState(r.error);
      }
    } catch {
      setState('error');
    }
  }, []);

  useEffect(() => {
    loadRanking();
  }, [loadRanking]);

  // Búsqueda con espera corta: un pedido por pausa de tipeo, no por tecla (constitución 7).
  useEffect(() => {
    const q = query.trim().replace(/^@/, '');
    if (q.length < 2) {
      setResults(null);
      return;
    }
    const t = setTimeout(async () => {
      const r = await searchPlayers(q);
      setResults(r.ok ? r.players : []);
    }, 350);
    return () => clearTimeout(t);
  }, [query]);

  if (state === 'loading') {
    return <p className="text-center font-mono text-xs text-[#ff00ff] animate-pulse py-10">CARGANDO AMIGOS...</p>;
  }
  if (state === 'login_required') {
    return (
      <div className="flex flex-col items-center justify-center gap-4 py-10 text-center">
        <Users className="w-10 h-10 text-[#00ffff]/30" />
        <p className="font-mono text-sm text-[#00ffff]/70">Ingresá con Google para seguir jugadores y competir con tus amigos.</p>
        <button type="button" onClick={() => signIn('google')} className="px-5 py-2 rounded-full bg-[#00ffff] text-[#050510] font-black text-xs">
          INGRESAR CON GOOGLE
        </button>
      </div>
    );
  }
  if (state === 'username_required') {
    return (
      <div className="flex flex-col items-center gap-4 py-10 text-center">
        <p className="font-mono text-sm text-[#00ffff]/70">Elegí un nombre de usuario para que tus amigos te encuentren.</p>
        {onNeedUsername && (
          <button type="button" onClick={onNeedUsername} className="px-5 py-2 rounded-full bg-[#00ffff] text-[#050510] font-black text-xs">
            ELEGIR NOMBRE
          </button>
        )}
      </div>
    );
  }
  if (state === 'error') {
    return <p className="text-center font-mono text-xs text-red-400 py-10">No pudimos cargar tus amigos.</p>;
  }

  return (
    <div className="space-y-4">
      <div className="relative">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#00ffff]/40" />
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Buscar por nombre de usuario"
          aria-label="Buscar jugadores"
          className="w-full rounded-xl bg-[#050510]/70 border border-[#00ffff]/20 focus:border-[#00ffff]/60 outline-none py-2.5 pl-9 pr-3 text-sm text-[#e0ffff] placeholder:text-[#00ffff]/30"
        />
      </div>

      {results !== null && (
        <div className="space-y-2" aria-label="Resultados de búsqueda">
          {results.length === 0 ? (
            <p className="text-center font-mono text-xs text-[#00ffff]/40">Nadie con ese nombre.</p>
          ) : (
            results.map((p) => (
              <div key={p.username} className="flex items-center gap-3 p-2 rounded-xl bg-[#050510]/40 border border-[#00ffff]/10">
                <Avatar image={p.image} />
                <span className="flex-1 truncate font-bold text-sm text-[#00ffff]">@{p.username}</span>
                <FollowButton username={p.username} initialFollowed={p.isFollowed} onChange={() => loadRanking()} />
              </div>
            ))
          )}
        </div>
      )}

      {entries.length <= 1 && results === null && (
        <p className="text-center font-mono text-xs text-[#00ffff]/50">Todavía no seguís a nadie. Buscá a tus amigos o seguilos desde el ranking.</p>
      )}

      <div className="space-y-2">
        {entries.map((e, index) => (
          <div
            key={e.username}
            className={`flex items-center gap-3 p-3 rounded-xl border ${e.isMe ? 'bg-[#ff00ff]/10 border-[#ff00ff]/40' : 'bg-[#050510]/60 border-[#00ffff]/10'}`}
          >
            <span className="w-6 text-center font-black italic text-[#00ffff]/60">{index + 1}</span>
            <Avatar image={e.image} />
            <div className="flex-1 min-w-0">
              <p className="font-bold text-sm text-[#00ffff] truncate">@{e.username}{e.isMe && <span className="ml-2 font-mono text-[10px] text-[#ff00ff]">(VOS)</span>}</p>
              <div className="flex gap-3 mt-0.5 font-mono text-[10px]">
                <span className="flex items-center gap-1 text-[#ff00ff]/80"><Trophy className="w-3 h-3" /> {e.points} P</span>
                <span className="flex items-center gap-1 text-[#00ffff]/80"><Clock className="w-3 h-3" /> {formatDuration(e.totalTime)}</span>
              </div>
            </div>
            {!e.isMe && <FollowButton username={e.username} initialFollowed onChange={() => loadRanking()} />}
          </div>
        ))}
      </div>
    </div>
  );
}
