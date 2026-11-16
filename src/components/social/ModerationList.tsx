'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Eye, EyeOff, Flag } from 'lucide-react';
import { moderateComment, type ModerationItem } from '@/app/actions/moderation';
import { formatRelativeTime } from '@/lib/relative-time';

const STATUS: Record<string, string> = {
  visible: 'Visible',
  hidden_reports: 'Oculto por reportes',
  hidden_admin: 'Oculto por admin',
};

export default function ModerationList({ items }: { items: ModerationItem[] }) {
  const router = useRouter();
  const [busy, setBusy] = useState<string | null>(null);

  const act = async (id: string, action: 'hide' | 'restore') => {
    setBusy(id);
    try {
      await moderateComment(id, action);
      router.refresh();
    } finally {
      setBusy(null);
    }
  };

  if (items.length === 0) {
    return <p className="font-mono text-sm text-[#00ffff]/60">No hay comentarios para revisar.</p>;
  }

  return (
    <ul className="space-y-3">
      {items.map((c) => (
        <li key={c.id} className="p-4 rounded-xl bg-[#050510]/60 border border-[#00ffff]/15 space-y-2">
          <div className="flex flex-wrap items-center gap-2 font-mono text-[11px]">
            <span className="font-bold text-[#00ffff]">@{c.author.username}</span>
            <span className="text-[#00ffff]/40">en {c.word}</span>
            <span className="text-[#00ffff]/40">{formatRelativeTime(c.createdAt)}</span>
            <span className="flex items-center gap-1 text-[#ffaa00]"><Flag className="w-3 h-3" /> {c.reportCount} ({c.totalReports} en total)</span>
            <span className={`ml-auto px-2 py-0.5 rounded-full ${c.status === 'visible' ? 'bg-green-500/10 text-green-300' : 'bg-red-500/10 text-red-300'}`}>
              {STATUS[c.status] ?? c.status}
            </span>
          </div>
          <p className="text-sm text-[#e0ffff] whitespace-pre-line break-words">{c.body}</p>
          <div className="flex gap-2">
            {c.status === 'visible' ? (
              <button type="button" disabled={busy === c.id} onClick={() => act(c.id, 'hide')} className="flex items-center gap-1 px-3 py-1.5 rounded-lg border border-red-400/40 text-red-300 font-mono text-xs">
                <EyeOff className="w-3.5 h-3.5" /> OCULTAR
              </button>
            ) : (
              <button type="button" disabled={busy === c.id} onClick={() => act(c.id, 'restore')} className="flex items-center gap-1 px-3 py-1.5 rounded-lg border border-green-400/40 text-green-300 font-mono text-xs">
                <Eye className="w-3.5 h-3.5" /> VOLVER A MOSTRAR
              </button>
            )}
          </div>
        </li>
      ))}
    </ul>
  );
}
