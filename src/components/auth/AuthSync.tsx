'use client';

import { useEffect, useRef } from 'react';
import { useSession } from 'next-auth/react';
import { migrateGuestProgress } from '@/app/actions/sync';

export function AuthSync() {
  const { data: session, status } = useSession();
  const hasSynced = useRef(false);

  useEffect(() => {
    // Only attempt sync if we are authenticated and haven't synced in this component lifecycle
    if (status === 'authenticated' && !hasSynced.current) {
      hasSynced.current = true;
      
      const performSync = async () => {
        try {
          const result = await migrateGuestProgress();
          if (result.success && result.message !== 'No guest data to migrate') {
            console.log('Progress migrated from guest to authenticated account');
            // We could force a session update here if needed, 
            // but migrateGuestProgress already updated the DB.
          }
        } catch (error) {
          console.error('Failed to migrate guest progress:', error);
        }
      };

      performSync();
    }
  }, [status]);

  return null; // This component doesn't render anything
}
