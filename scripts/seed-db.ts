import pg from 'pg';
import { randomUUID } from 'node:crypto';
import { pathToFileURL } from 'node:url';
import { SUCCESS_MESSAGES, FAIL_MESSAGES } from '../src/lib/messages';

// Carga Settings y GameMessage. Los mensajes salen de src/lib/messages.ts (única fuente, DATA-06).
// Idempotente (DATA-05): no pisa costos ya configurados y reemplaza los mensajes en una transacción.
// Uso: npx dotenv -e .env.test -- npx tsx scripts/seed-db.ts   (el .env local apunta a producción)

export async function seed(pool: pg.Pool) {
  const client = await pool.connect();
  try {
    await client.query('BEGIN');
    for (const [id, value] of [['costo_pista', '5'], ['costo_salto', '15']]) {
      await client.query(
        'INSERT INTO "Setting" ("id", "value") VALUES ($1, $2) ON CONFLICT ("id") DO NOTHING',
        [id, value]
      );
    }
    await client.query('DELETE FROM "GameMessage"');
    for (const [type, list] of [['success', SUCCESS_MESSAGES], ['fail', FAIL_MESSAGES]] as const) {
      for (const content of list) {
        await client.query(
          'INSERT INTO "GameMessage" ("id", "content", "type") VALUES ($1, $2, $3)',
          [randomUUID(), content, type]
        );
      }
    }
    await client.query('COMMIT');
  } catch (error) {
    await client.query('ROLLBACK');
    throw error;
  } finally {
    client.release();
  }
}

async function main() {
  const pool = new pg.Pool({ connectionString: process.env.DATABASE_URL });
  try {
    await seed(pool);
  } catch (error) {
    console.error('Seeding failed:', error);
    process.exitCode = 1;
  } finally {
    await pool.end();
  }
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  main();
}
