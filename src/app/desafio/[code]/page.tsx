import type { Metadata } from 'next';
import Link from 'next/link';
import { getChallenge } from '@/app/actions/challenges';
import { formatDuration } from '@/lib/share';
import PageShell from '@/components/layout/PageShell';
import ChallengePlay from '@/components/social/ChallengePlay';

// Página pública de un desafío (002-social FR-022). Nunca recibe la definición ni la solución
// salvo que el visitante ya lo haya terminado.

type Props = { params: Promise<{ code: string }> };

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { code } = await params;
  const view = await getChallenge(code);
  if (!view) return { title: 'Desafío no encontrado · Matete' };
  const title = `¡@${view.creator.username} te desafía en Matete!`;
  const description = `Resolvé una palabra de ${view.length} letras en menos de ${formatDuration(view.creatorTime)}.`;
  return {
    title,
    description,
    openGraph: { title, description, type: 'website', siteName: 'Matete', locale: 'es_AR' },
    twitter: { card: 'summary', title, description },
  };
}

export default async function ChallengePage({ params }: Props) {
  const { code } = await params;
  const view = await getChallenge(code);

  return (
    <PageShell>
      {view ? (
        <ChallengePlay view={view} />
      ) : (
        <div className="text-center py-16 space-y-4">
          <p className="text-2xl font-black text-white">Este desafío no existe</p>
          <p className="font-mono text-sm text-[#00ffff]/60">Revisá el link o pedile a tu amigo que te lo mande de nuevo.</p>
          <Link href="/" className="inline-block px-6 py-3 rounded-xl bg-[#00ffff] text-[#050510] font-black">JUGAR MATETE</Link>
        </div>
      )}
    </PageShell>
  );
}
