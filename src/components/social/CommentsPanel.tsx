'use client';

import React, { useCallback, useEffect, useState } from 'react';
import { signIn } from 'next-auth/react';
import { MessageCircle, Flag, Trash2, Send, User as UserIcon, Loader2 } from 'lucide-react';
import { getWordComments, postComment, reportComment, deleteComment, type CommentView } from '@/app/actions/comments';
import { COMMENT_MAX_LENGTH } from '@/lib/social-rules';
import { formatRelativeTime } from '@/lib/relative-time';

// Comentarios de una palabra acertada (002-social US2). El servidor decide si se pueden ver.

const POST_ERRORS: Record<string, string> = {
  login_required: 'Ingresá con Google para comentar.',
  username_required: 'Elegí un nombre de usuario para comentar.',
  not_solved: 'Solo podés comentar palabras que acertaste.',
  invalid_body: `El comentario tiene que tener entre 1 y ${COMMENT_MAX_LENGTH} caracteres.`,
  too_fast: 'Esperá unos segundos antes de volver a comentar.',
  daily_limit: 'Llegaste al máximo de comentarios por hoy.',
};

interface CommentsPanelProps {
  wordId: number;
  onNeedUsername?: () => void;
}

export default function CommentsPanel({ wordId, onNeedUsername }: CommentsPanelProps) {
  const [state, setState] = useState<'loading' | 'locked' | 'ready' | 'error'>('loading');
  const [comments, setComments] = useState<CommentView[]>([]);
  const [nextCursor, setNextCursor] = useState<string | null>(null);
  const [canPost, setCanPost] = useState(false);
  const [blockedReason, setBlockedReason] = useState<string | undefined>();
  const [body, setBody] = useState('');
  const [posting, setPosting] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const apply = useCallback((r: Awaited<ReturnType<typeof getWordComments>>, append: boolean) => {
    if (r.locked) {
      setState('locked');
      return;
    }
    setComments((prev) => (append ? [...prev, ...r.comments] : r.comments));
    setNextCursor(r.nextCursor);
    setCanPost(r.canPost);
    setBlockedReason(r.postBlockedReason);
    setState('ready');
  }, []);

  /** Página siguiente (botón "ver más"). */
  const load = (cursor?: string) => {
    getWordComments(wordId, cursor).then((r) => apply(r, Boolean(cursor)), () => setState('error'));
  };

  // Al cambiar de palabra se vuelve a "cargando" durante el render (no en el efecto).
  const [shownWordId, setShownWordId] = useState(wordId);
  if (shownWordId !== wordId) {
    setShownWordId(wordId);
    setState('loading');
    setComments([]);
  }

  // Primera página; se descarta la respuesta si la palabra cambió mientras tanto.
  useEffect(() => {
    let active = true;
    getWordComments(wordId).then(
      (r) => active && apply(r, false),
      () => active && setState('error')
    );
    return () => {
      active = false;
    };
  }, [wordId, apply]);

  const handlePost = async (e: React.FormEvent) => {
    e.preventDefault();
    if (posting || !body.trim()) return;
    setPosting(true);
    setMessage(null);
    try {
      const r = await postComment(wordId, body);
      if (r.ok) {
        setComments((prev) => [r.comment, ...prev]);
        setBody('');
      } else {
        setMessage(POST_ERRORS[r.error] ?? 'No pudimos publicar el comentario.');
      }
    } catch {
      setMessage('No pudimos publicar el comentario.');
    } finally {
      setPosting(false);
    }
  };

  const handleReport = async (id: string) => {
    const r = await reportComment(id);
    if (r.ok) {
      setComments((prev) =>
        r.hidden ? prev.filter((c) => c.id !== id) : prev.map((c) => (c.id === id ? { ...c, reportedByMe: true } : c))
      );
      setMessage('Gracias, lo vamos a revisar.');
    }
  };

  const handleDelete = async (id: string) => {
    const r = await deleteComment(id);
    if (r.ok) setComments((prev) => prev.filter((c) => c.id !== id));
  };

  if (state === 'loading') {
    return (
      <div className="flex justify-center py-6 text-[#00ffff]/60">
        <Loader2 className="w-5 h-5 animate-spin" aria-label="Cargando comentarios" />
      </div>
    );
  }
  if (state === 'locked') {
    return <p className="text-center font-mono text-xs text-[#00ffff]/50 py-4">Los comentarios se ven después de acertar la palabra.</p>;
  }
  if (state === 'error') {
    return <p className="text-center font-mono text-xs text-red-400 py-4">No pudimos cargar los comentarios.</p>;
  }

  const remaining = COMMENT_MAX_LENGTH - Array.from(body).length;

  return (
    <div className="space-y-4">
      {canPost ? (
        <form onSubmit={handlePost} className="space-y-2">
          <textarea
            value={body}
            onChange={(e) => setBody(e.target.value)}
            maxLength={COMMENT_MAX_LENGTH * 2}
            rows={2}
            placeholder="¿Qué te pareció la definición?"
            aria-label="Escribí un comentario"
            className="w-full resize-none rounded-xl bg-[#050510]/70 border border-[#00ffff]/20 focus:border-[#00ffff]/60 outline-none p-3 text-sm text-[#e0ffff] placeholder:text-[#00ffff]/30"
          />
          <div className="flex items-center justify-between">
            <span className={`font-mono text-[10px] ${remaining < 0 ? 'text-red-400' : 'text-[#00ffff]/40'}`}>{remaining}</span>
            <button
              type="submit"
              disabled={posting || !body.trim() || remaining < 0}
              className="flex items-center gap-2 px-4 py-2 rounded-lg bg-[#00ffff] text-[#050510] font-black text-xs disabled:opacity-40"
            >
              <Send className="w-3.5 h-3.5" /> PUBLICAR
            </button>
          </div>
        </form>
      ) : (
        <div className="text-center rounded-xl border border-[#00ffff]/15 bg-[#050510]/40 p-3">
          {blockedReason === 'username_required' ? (
            <button type="button" onClick={onNeedUsername} className="font-mono text-xs text-[#00ffff] underline underline-offset-4">
              Elegí un nombre de usuario para comentar
            </button>
          ) : (
            <button type="button" onClick={() => signIn('google')} className="font-mono text-xs text-[#00ffff] underline underline-offset-4">
              Ingresá con Google para comentar
            </button>
          )}
        </div>
      )}

      {message && <p role="status" className="text-center font-mono text-[11px] text-[#ffaa00]">{message}</p>}

      {comments.length === 0 ? (
        <p className="text-center font-mono text-xs text-[#00ffff]/40 py-2">Todavía nadie comentó. ¡Sé el primero!</p>
      ) : (
        <ul className="space-y-3">
          {comments.map((c) => (
            <li key={c.id} className="flex gap-3 rounded-xl bg-[#050510]/50 border border-[#00ffff]/10 p-3">
              <div className="w-8 h-8 shrink-0 rounded-full overflow-hidden border border-[#ff00ff]/30 bg-[#1a1033]">
                {c.author.image ? (
                  // eslint-disable-next-line @next/next/no-img-element
                  <img src={c.author.image} alt="" className="w-full h-full object-cover" />
                ) : (
                  <UserIcon className="w-full h-full p-1.5 text-[#ff00ff]/50" />
                )}
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2">
                  <span className="font-bold text-xs text-[#00ffff]">@{c.author.username}</span>
                  <span className="font-mono text-[10px] text-[#00ffff]/40">{formatRelativeTime(c.createdAt)}</span>
                  <div className="ml-auto flex gap-1">
                    {c.mine ? (
                      <button type="button" onClick={() => handleDelete(c.id)} title="Borrar" aria-label="Borrar comentario" className="p-1 text-[#00ffff]/30 hover:text-red-400">
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    ) : canPost && !c.reportedByMe ? (
                      <button type="button" onClick={() => handleReport(c.id)} title="Reportar" aria-label="Reportar comentario" className="p-1 text-[#00ffff]/30 hover:text-[#ffaa00]">
                        <Flag className="w-3.5 h-3.5" />
                      </button>
                    ) : null}
                  </div>
                </div>
                {/* Texto plano: React escapa el contenido, nunca se interpreta como HTML (FR-009). */}
                <p className="mt-1 text-sm text-[#e0ffff]/90 whitespace-pre-line break-words">{c.body}</p>
              </div>
            </li>
          ))}
        </ul>
      )}

      {nextCursor && (
        <button type="button" onClick={() => load(nextCursor)} className="w-full font-mono text-xs text-[#00ffff]/60 hover:text-[#00ffff] py-2">
          Ver más comentarios
        </button>
      )}
    </div>
  );
}

export function CommentsToggle({ wordId, onNeedUsername }: CommentsPanelProps) {
  const [open, setOpen] = useState(false);
  return (
    <div className="w-full">
      <button
        type="button"
        onClick={() => setOpen((v) => !v)}
        aria-expanded={open}
        className="w-full flex items-center justify-center gap-2 px-5 py-3 bg-[#00ffff]/5 hover:bg-[#00ffff]/10 border border-[#00ffff]/30 rounded-xl font-mono text-xs md:text-sm font-bold text-[#00ffff]"
      >
        <MessageCircle className="w-4 h-4" /> {open ? 'OCULTAR COMENTARIOS' : 'COMENTARIOS'}
      </button>
      {open && (
        <div className="mt-3">
          <CommentsPanel wordId={wordId} onNeedUsername={onNeedUsername} />
        </div>
      )}
    </div>
  );
}
