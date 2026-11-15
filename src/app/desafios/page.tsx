import type { Metadata } from 'next';
import Link from 'next/link';
import { Swords, Trophy, Users } from 'lucide-react';
import { getMyChallenges } from '@/app/actions/challenges';
import { formatDuration } from '@/lib/share';
import { formatRelativeTime } from '@/lib/relative-time';
import PageShell from '@/components/layout/PageShell';
import LoginPrompt from '@/components/social/LoginPrompt';

export const metadata: Metadata = { title: 'Mis desafíos · Matete' };

export default async function MyChallengesPage() {
  const data = await getMyChallenges();

  return (
    <PageShell>
      <h1 className="flex items-center gap-3 text-2xl md:text-3xl font-black italic text-[#00ffff] mb-6">
        <Swords className="w-7 h-7 text-[#ffaa00]" /> MIS DESAFÍOS
      </h1>

      {!data.ok ? (
        <LoginPrompt reason={data.error} />
      ) : (
        <div className="space-y-8">
          <section>
            <h2 className="font-mono text-sm tracking-widest text-[#ff00ff] mb-3">LOS QUE CREÉ</h2>
            {data.created.length === 0 ? (
              <p className="font-mono text-xs text-[#00ffff]/50">
                Todavía no creaste ninguno. Acertá una palabra y tocá &quot;Desafiar a un amigo&quot;.
              </p>
            ) : (
              <ul className="space-y-2">
                {data.created.map((c) => (
                  <li key={c.code}>
                    <Link href={`/desafio/${c.code}`} className="flex items-center gap-3 p-3 rounded-xl bg-[#050510]/60 border border-[#00ffff]/10 hover:border-[#00ffff]/40">
                      <span className="flex-1 font-mono text-sm text-[#00ffff]">
                        {c.length} letras · {formatDuration(c.creatorTime)}
                      </span>
                      <span className="flex items-center gap-1 font-mono text-xs text-[#00ffff]/70">
                        <Users className="w-3.5 h-3.5" /> {c.participants}
                      </span>
                      <span className="font-mono text-[10px] text-[#00ffff]/40">{formatRelativeTime(c.createdAt)}</span>
                    </Link>
                  </li>
                ))}
              </ul>
            )}
          </section>

          <section>
            <h2 className="font-mono text-sm tracking-widest text-[#ff00ff] mb-3">LOS QUE JUGUÉ</h2>
            {data.played.length === 0 ? (
              <p className="font-mono text-xs text-[#00ffff]/50">Todavía no jugaste ninguno.</p>
            ) : (
              <ul className="space-y-2">
                {data.played.map((p) => (
                  <li key={p.code}>
                    <Link href={`/desafio/${p.code}`} className="flex items-center gap-3 p-3 rounded-xl bg-[#050510]/60 border border-[#00ffff]/10 hover:border-[#00ffff]/40">
                      <span className="flex-1 truncate text-sm font-bold text-[#00ffff]">de @{p.creator.username}</span>
                      <span className="font-mono text-xs text-[#00ffff]/80">
                        {p.result.solved ? formatDuration(p.result.timeTaken ?? 0) : 'te rendiste'}
                      </span>
                      {p.result.won ? <Trophy className="w-4 h-4 text-yellow-400" aria-label="Ganaste" /> : <span className="w-4" />}
                      <span className="font-mono text-[10px] text-[#00ffff]/40">{formatRelativeTime(p.playedAt)}</span>
                    </Link>
                  </li>
                ))}
              </ul>
            )}
          </section>
        </div>
      )}
    </PageShell>
  );
}
