'use client';

import { useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { useSession } from 'next-auth/react';
import { migrateGuestProgress } from '@/app/actions/sync';

/**
 * Al entrar con Google, funde el invitado de este navegador en la cuenta. La home ya se
 * renderizó antes de migrar (con el alias, neuronas y nivel viejos), así que después de
 * migrar se pide de nuevo el estado al servidor (SYNC-10).
 */
export function AuthSync() {
  const { status, update } = useSession();
  const router = useRouter();
  const hasSynced = useRef(false);

  useEffect(() => {
    if (status !== 'authenticated' || hasSynced.current) return;
    hasSynced.current = true;

    migrateGuestProgress()
      .then(async (result) => {
        if (result.success && result.migrated) {
          await update();
          router.refresh();
        }
      })
      .catch((error) => console.error('Failed to migrate guest progress:', error));
  }, [status, update, router]);

  return null;
}
