# Research: 001-hardening

Decisiones técnicas para implementar [spec.md](./spec.md). Cada una referencia el hallazgo del [CODE_REVIEW.md](../../CODE_REVIEW.md) que corrige.

## R1 — Credencial de invitado firmada (S-1, FR-001..FR-005)

**Decision**: Cookie `matete-guest` con formato `v1.<userId>.<expEpochSeconds>.<hmac>`, donde `hmac = HMAC-SHA256(GUEST_COOKIE_SECRET, "v1.<userId>.<exp>")` en base64url. Atributos: `httpOnly`, `sameSite=lax`, `secure` en producción, `path=/`, `maxAge` 30 días. Verificación con `crypto.timingSafeEqual`. Implementada con `node:crypto`, sin dependencias nuevas, en `src/lib/guest-cookie.ts`.

- `getUser()` resuelve el invitado **solo** si el usuario encontrado tiene `email IS NULL` (FR-003).
- Secreto en `GUEST_COOKIE_SECRET` (dedicado; no se reutiliza `NEXTAUTH_SECRET` para que rotar uno no invalide el otro). Si falta en producción, la credencial se considera inválida (fail closed) y se loguea un error.
- Nombre de cookie nuevo (`matete-guest`) para que la convivencia con la cookie vieja (`matete-guest-id`) sea explícita.

**Transición (FR-004, Q1)**: variable `GUEST_LEGACY_COOKIE_UNTIL` (fecha ISO = día del deploy + 30). Mientras `now < GUEST_LEGACY_COOKIE_UNTIL`, si no hay cookie nueva válida se lee `matete-guest-id`; se acepta solo si apunta a un usuario con `email IS NULL`. Al aceptarla se emite la cookie firmada y se borra la vieja. Sin la variable, la cookie vieja se rechaza siempre (default seguro).

**Restricción de Next.js**: `cookies().set()` solo funciona en Server Actions y Route Handlers, no al renderizar un Server Component (`page.tsx`). Por eso la reemisión es "best effort": se intenta en `getUser()` dentro de try/catch y se concreta en la primera Server Action del jugador (`ensureUser()` siempre corre en una action). El GameEngine ya dispara una action al montar (ver R3), así que en la práctica la cookie se reemite en la primera carga.

**Ranking (FR-005)**: `getGlobalRanking` y `getLevelRanking` dejan de seleccionar/devolver `id`. `RankingBoard` usa la posición como `key`.

**Alternatives considered**:
- *JWT (jose) o `iron-session`*: resuelven lo mismo con una dependencia más; un HMAC de 20 líneas es suficiente para un único claim (`userId` + `exp`).
- *Mover invitados a sesiones de NextAuth (Credentials anónimo)*: cambio mucho más grande, afecta el adapter y la migración a Google.
- *Tabla de tokens opacos en BD*: requiere migración de schema y una query extra por request; el HMAC es stateless.

## R2 — La solución no viaja al cliente (S-2, FR-006..FR-010)

**Decision**: El servidor entrega una vista pública de la palabra:

```ts
type PublicWord = {
  wordId: number
  description: string
  length: number
  pool: string[]          // 14 letras mayúsculas desordenadas: solución + distractores
  revealed: { index: number; letter: string }[]
}
```

- `buildPublicWord(word, revealedIndices)` en `src/lib/public-word.ts` genera el pool en el servidor con la misma lógica que hoy tiene el cliente (distractores de `AEIOURSTNMPL`, shuffle Fisher–Yates en lugar de `sort(random)`).
- Nueva action `submitGuess(wordId, guess)`: compara `guess.toUpperCase() === word.word.toUpperCase()` (regla actual; las 486 palabras usan solo letras y ñ, largo 4–11). Responde `{ correct: false }` sin detalle, o `{ correct: true, solution, ... }` (FR-010).
- `submitGuess` reemplaza a `recordWordSolved` como única vía de acreditación; `recordWordSolved` se elimina.
- `requestHint('letter')` devuelve `{ index, letter }` (FR-009).
- Una palabra está **en juego** para un jugador si `word.levelId === user.levelId` y no tiene `Progress` con `guessed` o `skipped`. `submitGuess` y `requestHint` rechazan cualquier otra.

**Alternatives considered**:
- *Enviar la solución cifrada u ofuscada*: la clave también estaría en el cliente; no cumple FR-006.
- *Validar letra por letra*: revelaría qué letras fallan (FR-007) y multiplica los requests.

## R3 — Momento de entrega y tiempo en servidor (S-4, FR-015..FR-017)

**Decision**: La entrega de una palabra crea su fila `Progress` con `startTime = now()` si no existe (create-only: `INSERT ... ON CONFLICT DO NOTHING`), sin cambiar el schema (`startTime` ya existe con default `now()`).

- Puntos de entrega: `getInitialGameState` (si hay usuario), `getNextWord` (renombre de `getRandomWordFromLevel`) y `moveToNextLevel`.
- Primera visita sin usuario: el Server Component no puede crear el usuario (no puede setear cookie). El GameEngine llama al montar a la nueva action `startWord(wordId)`, que hace `ensureUser()` + registra la entrega si la palabra está en juego. Esa misma llamada reemite la cookie de invitado (R1).
- Al acertar: `timeTaken = EXTRACT(EPOCH FROM now() - "startTime")` en segundos, calculado en el mismo `UPDATE` que marca el acierto (R4). El tiempo que manda el cliente se ignora; el parámetro desaparece de la firma.
- Dos pestañas: la fila ya existe, `ON CONFLICT DO NOTHING` conserva el primer `startTime`.
- El timer visual del GameEngine queda igual (FR-017).

**Alternatives considered**: *columna nueva `deliveredAt`*: redundante con `startTime`; requeriría migración y backfill.

## R4 — Acreditación idempotente y economía atómica (S-3, S-5, FR-011..FR-014)

**Decision**: Todas las mutaciones de valor dentro de `prisma.$transaction(async tx => ...)` y con `UPDATE` condicional, de modo que la condición y la escritura sean una sola sentencia atómica en Postgres (row lock implícito, READ COMMITTED alcanza).

- **Acierto**: asegurar la fila (`INSERT ... ON CONFLICT DO NOTHING`), luego
  `UPDATE "Progress" SET guessed = true, "endTime" = now(), "timeTaken" = EXTRACT(EPOCH FROM now() - "startTime") WHERE "userId" = $1 AND "wordId" = $2 AND guessed = false AND skipped = false RETURNING "timeTaken"`.
  Solo si devuelve una fila: `user.update` con `neuronas +10`, `points +1`, `totalTime + timeTaken`. Si no devuelve filas, respuesta idempotente `{ correct: true, alreadySolved: true }` sin acreditar.
- **Cobro (pista o salto)**: `tx.user.updateMany({ where: { id, neuronas: { gte: cost } }, data: { neuronas: { decrement: cost } } })`; `count === 0` → error "Not enough neuronas" y rollback.
- **Pista de letra**: dentro de la misma transacción, `tx.progress.updateMany({ where: { userId, wordId, guessed: false, skipped: false, revealedIndices: { equals: current } }, data: { revealedIndices: next } })`. `count === 0` → otra pista ganó la carrera → rollback sin cobro. Mantiene el límite de 2.
- **Salto**: `tx.progress.updateMany({ where: { userId, wordId, guessed: false, skipped: false }, data: { skipped: true } })`; `count === 0` → rechazo sin cobro (FR-013).
- Costos sin cambios: `Setting['costo_pista']` (fallback 5) y 15 por salto (FR-014). El cobro se aplica después de validar la palabra, nunca antes.

**Alternatives considered**: *`SERIALIZABLE` con reintentos*: más costoso y requiere loop de retry; los `UPDATE` condicionales resuelven cada carrera puntual.

## R5 — Migración invitado→Google (B-1, B-2, FR-018..FR-022)

**Decision**: `migrateGuestProgress` en una única transacción interactiva:

1. Bloquear al invitado: `SELECT id, neuronas, "levelId" FROM "User" WHERE id = $guestId AND email IS NULL FOR UPDATE`. Si no hay fila (ya migrado, borrado o no es invitado) → borrar cookies de invitado y salir con éxito. Una segunda pestaña concurrente queda esperando el lock y luego no encuentra fila (FR-020).
2. Fusionar `Progress` del invitado en la cuenta: por palabra, `guessed = a OR b`, `skipped = (a.skipped OR b.skipped) AND NOT guessed`, `timeTaken`/`endTime` de quien la acertó (prioridad cuenta registrada), `revealedIndices` de la cuenta si existe.
3. Borrar el `Progress` del invitado y luego al invitado (evita el FK RESTRICT que hoy rompe todo).
4. **Recalcular** en la cuenta: `points = count(guessed)`, `totalTime = sum(timeTaken where guessed)` desde `Progress`. Así una palabra resuelta por ambos cuenta una vez (FR-022) y el resultado es idempotente por construcción.
5. `neuronas += max(0, guest.neuronas - 50)` (FR-021) y `levelId = max(auth, guest)`.
6. Fuera de la transacción, tras el commit: borrar las cookies de invitado.

`AuthSync` no cambia: aunque se re-ejecute en cada montaje, la migración es no-op si no hay invitado.

**Alternatives considered**: *`onDelete: Cascade` en `Progress.user`*: arregla el FK pero convierte cualquier borrado accidental de usuario en borrado masivo de progreso; se prefiere el borrado explícito dentro de la transacción.

## R6 — Migraciones reproducibles (B-3, FR-023..FR-025)

**Decision**: Generar una migración `sync_schema` que lleve las dos migraciones existentes al `schema.prisma` actual, y *baselinarla* en producción.

1. Unificar el default `neuronas` en 50 (schema ya dice 50; corregir el fallback `?? 150` de `auth.ts`).
2. Generar el SQL con `prisma migrate diff` desde las migraciones existentes hacia el schema (usando un Postgres local como shadow DB). Revisar que sea **aditivo** (agrega `User.username` + unique, `Progress.revealedIndices`, `Setting`, `GameMessage`, cambia default de `neuronas`).
3. Verificar equivalencia: `prisma migrate diff` entre la base de producción (lectura, introspección) y el resultado de las migraciones. Diferencia esperada: vacía.
4. En producción no se ejecuta el SQL (las tablas ya existen): se registra con `prisma migrate resolve --applied <migration>`. Es una escritura en `_prisma_migrations` → requiere confirmación explícita del dueño al implementar.
5. Los flags exactos de Prisma 7 (`--from-migrations`, `--to-schema`, `--from-config-datasource`, shadow DB vía `prisma.config.ts`) se confirman con `npx prisma migrate diff --help` al implementar.

Esta feature **no agrega columnas**; la migración solo sincroniza la deuda existente.

**Resultado (2026-11-15)**: la migración generada es aditiva (`revealedIndices`, `username` + unique, `Setting`, `GameMessage`, default `neuronas` 50). Aplicada sobre una base vacía, el diff contra `schema.prisma` da vacío. El diff de solo lectura entre producción y las tres migraciones también da vacío (`-- This is an empty migration.`): producción se puede baselinar con `migrate resolve --applied 20261115120000_sync_schema` sin ejecutar SQL. `prisma migrate reset` no se usó: Prisma 7 lo bloquea cuando lo ejecuta un agente; los tests usan `migrate deploy` sobre bases `*_test` y `TRUNCATE` entre casos.

**Alternatives considered**: *Reescribir una migración `init` única desde cero*: rompe el historial de `_prisma_migrations` en producción.

## R7 — Recálculo de datos históricos (FR-026, FR-027, Q2)

**Decision**: Script `scripts/recalculate-points.ts`, dry-run por defecto, `--apply` para escribir:

1. `CREATE TABLE "User_backup_20261115" AS SELECT id, points, "totalTime", neuronas FROM "User"` (respaldo previo, obligatorio).
2. `UPDATE "User" u SET points = (SELECT count(*) FROM "Progress" p WHERE p."userId" = u.id AND p.guessed)`.
3. `totalTime` se recalcula igual (`sum("timeTaken") WHERE guessed`): sufrió el mismo bug (S-3 sumaba tiempo en cada repetición) y, a diferencia de las neuronas, es determinístico desde `Progress`. Desempata el ranking.
4. Neuronas no se tocan (FR-027).
5. Dry-run imprime cantidad de usuarios afectados y diferencia total (esperado ≈ 364 puntos).

Se ejecuta una vez en producción después del deploy, con confirmación explícita del dueño.

## R8 — Estrategia de tests (constitución, principio 6)

**Decision**: Dos niveles.

- **Unit (Vitest, jsdom, existente)**: `guest-cookie` (firma, alteración, expiración, legacy), `public-word` (el pool contiene las letras de la solución, largo 14, sin la palabra en claro), y los tests de GameEngine adaptados a las nuevas props (mismas aserciones de experiencia).
- **Integración contra Postgres real**: `vitest.integration.config.ts` (environment `node`), base `matete_test` local (Postgres 18 de Homebrew disponible; producción usa 16, sin features específicas de versión). Antes de la suite: `prisma migrate reset --force` contra `TEST_DATABASE_URL` (esto además valida FR-023). Se mockean solo `next/headers` (cookies) y `next-auth` (sesión); Prisma es real. Cubre idempotencia, concurrencia (`Promise.all` de pedidos), migración invitado→Google con progreso y FK.
- Script `npm run test:integration`. Los tests citan IDs de `tests/CASOS_DE_PRUEBA.md` / FR.

**Alternatives considered**: *Testcontainers/Docker*: Docker no está instalado en la máquina; Postgres local alcanza.
