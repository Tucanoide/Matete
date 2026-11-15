import type { Metadata } from 'next';
import { notFound } from 'next/navigation';
import { isAdmin } from '@/lib/social-auth';
import { getModerationQueue } from '@/app/actions/moderation';
import PageShell from '@/components/layout/PageShell';
import ModerationList from '@/components/social/ModerationList';

// Moderación de comentarios (002-social FR-012). A quien no es admin se le responde 404.

export const metadata: Metadata = { title: 'Moderación · Matete', robots: { index: false } };

export default async function ModerationPage() {
  if (!(await isAdmin())) notFound();
  const queue = await getModerationQueue();
  if (!queue.ok) notFound();

  return (
    <PageShell>
      <h1 className="text-2xl font-black italic text-[#00ffff] mb-2">MODERACIÓN DE COMENTARIOS</h1>
      <p className="font-mono text-xs text-[#00ffff]/50 mb-6">Comentarios reportados u ocultos. Se ocultan solos al llegar a 3 reportes.</p>
      <ModerationList items={queue.items} />
    </PageShell>
  );
}
