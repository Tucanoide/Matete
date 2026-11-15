import "dotenv/config";
import { defineConfig } from '@prisma/config'

export default defineConfig({
  datasource: {
    url: process.env.DATABASE_URL,
    // Solo para `prisma migrate diff --from-migrations` / `migrate dev` en local.
    // Nunca apuntarla a producción: Prisma la vacía en cada uso.
    shadowDatabaseUrl: process.env.SHADOW_DATABASE_URL,
  },
})
