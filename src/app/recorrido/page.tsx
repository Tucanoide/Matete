import type { Metadata } from 'next';
import Link from 'next/link';
import { Map as MapIcon, Trophy, Brain, Timer, Lightbulb, Swords, CheckCircle2, SkipForward, Medal } from 'lucide-react';
import { getMyJourney, type Journey } from '@/app/actions/journey';
import { formatDuration } from '@/lib/share';
import PageShell from '@/components/layout/PageShell';
import GoogleInvite from '@/components/social/GoogleInvite';

export const metadata: Metadata = { title: 'Mi recorrido · Matete' };

// specs/003-recorrido. Solo números: ninguna palabra ni definición.

const time = (s: number | null) => (s === null ? '—' : formatDuration(s));
const plural = (n: number, one: string, many: string) => `${n} ${n === 1 ? one : many}`;

function Stat({ icon, label, value, accent = '#00ffff' }: { icon: React.ReactNode; label: string; value: React.ReactNode; accent?: string }) {
  return (
    <div className="p-4 rounded-2xl bg-[#050510]/60 border border-[#00ffff]/10">
      <div className="flex items-center gap-2 mb-1" style={{ color: accent }}>
        {icon}
        <span className="font-mono text-[10px] tracking-widest uppercase opacity-70">{label}</span>
      </div>
      <p className="text-2xl font-black tabular-nums" style={{ color: accent }}>{value}</p>
    </div>
  );
}

function Levels({ levels, current }: { levels: Journey['levels']; current: number }) {
  return (
    <ul className="space-y-3">
      {levels.map((l) => {
        const done = l.guessed + l.skipped;
        const pct = l.totalWords > 0 ? Math.round((done / l.totalWords) * 100) : 0;
        const guessedPct = l.totalWords > 0 ? (l.guessed / l.totalWords) * 100 : 0;
        return (
          <li key={l.levelId} className="p-3 rounded-xl bg-[#050510]/60 border border-[#00ffff]/10">
            <div className="flex items-baseline justify-between gap-3 mb-2">
              <span className="font-black text-sm text-[#00ffff]">
                {l.name}
                {l.levelId === current && <span className="ml-2 font-mono text-[10px] text-[#ff00ff]">ACTUAL</span>}
              </span>
              <span className="font-mono text-xs text-[#00ffff]/70 tabular-nums">
                {l.guessed}/{l.totalWords} acertadas
              </span>
            </div>
            <div
              className="h-2 rounded-full bg-[#00ffff]/10 overflow-hidden flex"
              role="progressbar"
              aria-label={`${l.name}: ${pct}% terminado`}
              aria-valuenow={pct}
              aria-valuemin={0}
              aria-valuemax={100}
            >
              <div className="h-full bg-[#00ffff]" style={{ width: `${guessedPct}%` }} />
              <div className="h-full bg-[#ff00ff]/60" style={{ width: `${l.totalWords > 0 ? (l.skipped / l.totalWords) * 100 : 0}%` }} />
            </div>
            <p className="mt-2 font-mono text-[10px] text-[#00ffff]/50">
              <span className="text-[#ff00ff]/80">{plural(l.skipped, 'salteada', 'salteadas')}</span> · promedio {time(l.avgTime)} · {plural(l.hints, 'pista', 'pistas')}
            </p>
          </li>
        );
      })}
    </ul>
  );
}

export default async function JourneyPage() {
  const r = await getMyJourney();

  return (
    <PageShell>
      <h1 className="flex items-center gap-3 text-2xl md:text-3xl font-black italic text-[#00ffff] mb-6">
        <MapIcon className="w-7 h-7 text-[#ff00ff]" /> MI RECORRIDO
      </h1>

      {!r.ok ? (
        <div className="text-center py-10 space-y-4">
          <p className="font-mono text-sm text-[#00ffff]/70">Todavía no jugaste. Tu recorrido empieza con la primera palabra.</p>
          <Link href="/" className="inline-block px-6 py-3 rounded-xl bg-[#00ffff] text-[#050510] font-black">EMPEZAR A JUGAR</Link>
        </div>
      ) : (
        <JourneyView j={r.journey} />
      )}
    </PageShell>
  );
}

function JourneyView({ j }: { j: Journey }) {
  return (
    <div className="space-y-8">
      {j.isGuest && <GoogleInvite />}

      <section aria-label="Resumen">
        <h2 className="font-mono text-sm tracking-widest text-[#ff00ff] mb-3">RESUMEN</h2>
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
          <Stat icon={<Trophy className="w-4 h-4" />} label="Nivel" value={j.levelId} accent="#ff00ff" />
          <Stat icon={<Medal className="w-4 h-4" />} label="Puntos" value={j.points} />
          <Stat icon={<Brain className="w-4 h-4" />} label="Neuronas" value={j.neuronas} accent="#4ade80" />
          <Stat icon={<CheckCircle2 className="w-4 h-4" />} label="Acertadas" value={j.guessed} />
          <Stat icon={<SkipForward className="w-4 h-4" />} label="Salteadas" value={j.skipped} accent="#ff00ff" />
          {j.rank !== null ? (
            <Stat icon={<Trophy className="w-4 h-4" />} label="Ranking" value={`#${j.rank}`} accent="#facc15" />
          ) : (
            <Stat icon={<Timer className="w-4 h-4" />} label="En curso" value={j.inProgress} />
          )}
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mt-3">
          <Stat icon={<Timer className="w-4 h-4" />} label="Tiempo total" value={time(j.totalTime)} />
          <Stat icon={<Timer className="w-4 h-4" />} label="Promedio" value={time(j.avgTime)} />
          <Stat icon={<Timer className="w-4 h-4" />} label="Mejor tiempo" value={time(j.bestTime)} />
          <Stat icon={<Lightbulb className="w-4 h-4" />} label="Pistas usadas" value={j.hintsUsed} accent="#facc15" />
        </div>
        <p className="mt-3 font-mono text-xs text-[#00ffff]/60">
          {j.solvedWithoutHints} de {plural(j.guessed, 'palabra resuelta', 'palabras resueltas')} sin pistas.
        </p>
      </section>

      <section aria-label="Por nivel">
        <h2 className="font-mono text-sm tracking-widest text-[#ff00ff] mb-3">POR NIVEL</h2>
        <Levels levels={j.levels} current={j.levelId} />
      </section>

      {j.challenges && (
        <section aria-label="Desafíos">
          <h2 className="font-mono text-sm tracking-widest text-[#ffaa00] mb-3">DESAFÍOS</h2>
          <div className="grid grid-cols-3 gap-3">
            <Stat icon={<Swords className="w-4 h-4" />} label="Creados" value={j.challenges.created} accent="#ffaa00" />
            <Stat icon={<Swords className="w-4 h-4" />} label="Jugados" value={j.challenges.played} accent="#ffaa00" />
            <Stat icon={<Trophy className="w-4 h-4" />} label="Ganados" value={j.challenges.won} accent="#facc15" />
          </div>
          <Link href="/desafios" className="inline-block mt-3 font-mono text-xs text-[#ffaa00]/80 hover:text-[#ffaa00]">
            Ver mis desafíos →
          </Link>
        </section>
      )}
    </div>
  );
}
