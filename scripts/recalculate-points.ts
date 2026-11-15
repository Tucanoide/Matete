/**
 * Recalcula una única vez points y totalTime de cada usuario a partir de Progress
 * (spec 001-hardening, FR-026/FR-027; research R7). Las neuronas NO se tocan.
 *
 *   npx tsx scripts/recalculate-points.ts            # dry-run: solo informa
 *   npx tsx scripts/recalculate-points.ts --apply    # respalda en User_backup_20261115 y recalcula
 *
 * Usa DATABASE_URL. En producción: solo con confirmación explícita del dueño, después
 * de un pg_dump completo, y corriendo primero el dry-run.
 */
import 'dotenv/config'
import { pathToFileURL } from 'node:url'
import type { PrismaClient } from '@prisma/client'

export const BACKUP_TABLE = 'User_backup_20261115'

type Diff = {
  name: string | null
  username: string | null
  points: number
  realPoints: number
  totalTime: number
  realTime: number
}

export type RecalcReport = {
  applied: boolean
  usersAffected: number
  pointsDelta: number // suma de (real - actual); negativo = puntos inflados que se quitan
  timeDelta: number
  sample: Diff[]
}

const DIFF_SQL = `
  SELECT u.name, u.username, u.points, u."totalTime",
         COALESCE(c.cnt, 0)::int        AS "realPoints",
         COALESCE(c.t, 0)::float8       AS "realTime"
  FROM "User" u
  LEFT JOIN (
    SELECT "userId", count(*) AS cnt, sum("timeTaken") AS t
    FROM "Progress" WHERE guessed GROUP BY "userId"
  ) c ON c."userId" = u.id
  WHERE u.points <> COALESCE(c.cnt, 0)
     OR abs(u."totalTime" - COALESCE(c.t, 0)) > 0.001`

export async function recalculatePoints(db: PrismaClient, opts: { apply: boolean }): Promise<RecalcReport> {
  const diffs = (await db.$queryRawUnsafe<Diff[]>(DIFF_SQL)).map((d) => ({
    ...d,
    points: Number(d.points),
    realPoints: Number(d.realPoints),
    totalTime: Number(d.totalTime),
    realTime: Number(d.realTime),
  }))

  const report: RecalcReport = {
    applied: false,
    usersAffected: diffs.length,
    pointsDelta: diffs.reduce((n, d) => n + (d.realPoints - d.points), 0),
    timeDelta: diffs.reduce((n, d) => n + (d.realTime - d.totalTime), 0),
    sample: [...diffs].sort((a, b) => Math.abs(b.points - b.realPoints) - Math.abs(a.points - a.realPoints)).slice(0, 5),
  }
  if (!opts.apply) return report

  const exists = await db.$queryRawUnsafe<{ exists: boolean }[]>(
    `SELECT to_regclass('public."${BACKUP_TABLE}"') IS NOT NULL AS exists`
  )
  if (exists[0]?.exists) {
    throw new Error(`La tabla de respaldo "${BACKUP_TABLE}" ya existe: el recálculo ya se aplicó o hay un respaldo previo. Revisar antes de continuar.`)
  }

  await db.$transaction(async (tx) => {
    await tx.$executeRawUnsafe(
      `CREATE TABLE "${BACKUP_TABLE}" AS SELECT id, points, "totalTime", neuronas, now() AS backed_up_at FROM "User"`
    )
    await tx.$executeRawUnsafe(`
      UPDATE "User" u SET
        points = COALESCE(c.cnt, 0),
        "totalTime" = COALESCE(c.t, 0)
      FROM "User" u2
      LEFT JOIN (
        SELECT "userId", count(*) AS cnt, sum("timeTaken") AS t
        FROM "Progress" WHERE guessed GROUP BY "userId"
      ) c ON c."userId" = u2.id
      WHERE u.id = u2.id`)
  })

  return { ...report, applied: true }
}

async function main() {
  const apply = process.argv.includes('--apply')
  const { default: prisma } = await import('../src/lib/prisma')
  const target = new URL(process.env.DATABASE_URL ?? 'postgres://unset/unset')
  console.log(`Base: ${target.hostname}:${target.port || 5432}${target.pathname}  Modo: ${apply ? 'APPLY' : 'dry-run'}`)

  try {
    const r = await recalculatePoints(prisma as unknown as PrismaClient, { apply })
    console.log(`Usuarios con diferencias: ${r.usersAffected}`)
    console.log(`Diferencia de puntos: ${r.pointsDelta} (negativo = puntos sin palabra acertada que se quitan)`)
    console.log(`Diferencia de tiempo total: ${r.timeDelta.toFixed(1)} s`)
    for (const d of r.sample) {
      console.log(`  ${d.username ?? d.name ?? '(sin nombre)'}: ${d.points} → ${d.realPoints} puntos`)
    }
    console.log(r.applied ? `Aplicado. Respaldo en "${BACKUP_TABLE}".` : 'Dry-run: no se escribió nada. Usar --apply para aplicar.')
  } finally {
    await prisma.$disconnect()
  }
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  main().catch((error) => {
    console.error(error)
    process.exit(1)
  })
}
