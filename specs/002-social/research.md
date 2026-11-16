# Research: 002-social

## R1. Datos del texto compartido

- **Decision**: `submitGuess` agrega `result: { timeTaken, revealedIndices }` a la respuesta `correct: true`, leídos de `Progress` después del `UPDATE` (también cuando `alreadySolved`). El texto lo arma `buildShareText()` (módulo puro) en el cliente.
- **Rationale**: FR-003 exige los valores del servidor; ya están en la misma transacción, sin un round-trip extra. El texto es presentación y no tiene valor económico.
- **Alternatives**: action `getShareResult(wordId)` aparte (un pedido más por acierto); armar el texto en servidor (acopla copy y dominio al backend).
- **Formato**:
  ```
  Matete 🧠 Nivel 3
  🟩🟨🟩🟩🟩🟩🟩
  ⏱️ 0:42 · 💡 1 pista
  https://<dominio>
  ```
  🟩 = letra resuelta, 🟨 = letra revelada por pista. Sin pistas: "💡 sin pistas".

## R2. Visibilidad de comentarios

- **Decision**: helper `hasSolved(userId, wordId)` = existe `Progress` con `guessed = true`. Sin usuario o sin acierto, `getWordComments` devuelve `{ locked: true }` sin consultar comentarios.
- **Rationale**: FR-005 (ni siquiera la cantidad). Una sola condición, testeable.

## R3. Rate limit de comentarios y desafíos

- **Decision**: transacción con `SELECT pg_advisory_xact_lock(hashtext('comment:' || userId))`, luego `count` de los últimos 30 s y 24 h, luego `INSERT`. Mismo patrón para desafíos (`'challenge:'`) y seguidos (`'follow:'`).
- **Rationale**: serializa solo los pedidos del mismo usuario; dos pedidos simultáneos no pueden pasar ambos el chequeo. Sin tablas ni infraestructura extra (sin Redis).
- **Alternatives**: columna `lastCommentAt` con `UPDATE` condicional (resuelve 30 s pero no el tope diario); rate limit en memoria (no sobrevive reinicios ni varios procesos).

## R4. Reportes y auto-ocultado

- **Decision**: `CommentReport` con PK `(commentId, reporterId)`; `createMany skipDuplicates` y, solo si insertó, `UPDATE "Comment" SET "reportCount" = "reportCount" + 1, status = CASE WHEN status = 'visible' AND "reportCount" + 1 >= 3 THEN 'hidden_reports' ELSE status END`.
- **Restaurar (FR-013)**: el admin pone `status = 'visible'` y `reportCount = 0`; las filas de `CommentReport` quedan, así que los mismos usuarios no pueden volver a reportar. Hacen falta 3 reportes nuevos para volver a ocultarlo.
- **Estados**: `visible`, `hidden_reports`, `hidden_admin`, `deleted`. Solo `visible` se lista a jugadores.

## R5. Administradores

- **Decision**: `ADMIN_EMAILS` (lista separada por comas, sin distinguir mayúsculas). `isAdmin()` exige sesión de Google y email en la lista. La página `/admin/comentarios` responde `notFound()` a no admins.
- **Rationale**: un solo admin hoy (el dueño); sin tabla de roles. FR-012.

## R6. Seguir

- **Decision**: `Follow(followerId, followeeId)` con PK compuesta e índice por `followeeId`. Las actions reciben `username`. Ranking de amigos: `User` donde `id = yo` o `id IN (seguidos)`, orden `points desc, totalTime asc`, máximo 501 filas.
- **Marca en rankings**: `getGlobalRanking` y `getLevelRanking` agregan `isFollowed` e `isMe` calculados en servidor con el usuario actual. Siguen sin devolver `id`.
- **Búsqueda**: `username ILIKE prefix%` (escapando `%` y `_`), `email IS NOT NULL`, máximo 10. A esta escala no hace falta un índice de prefijo (`text_pattern_ops`); se agrega si la búsqueda se vuelve lenta.

## R7. Código de desafío

- **Decision**: 10 caracteres base62 con `crypto.randomInt` (~59 bits). `@@unique([creatorId, wordId])` hace idempotente la creación (FR-020); colisión de código → reintento (hasta 3).
- **Alternatives**: usar el `id` cuid (más largo y feo en un link); hashids sobre un entero (adivinable).

## R8. Tiempo y resultado del desafío

- **Decision**: `startChallenge` crea el intento con `createMany skipDuplicates` (`startTime` = default `now()` del servidor) y calcula `knewWord` en ese momento (existe `Progress.guessed` del jugador para la palabra). `submitChallengeGuess` cierra con `UPDATE "ChallengeAttempt" SET solved = true, "endTime" = now(), "timeTaken" = EXTRACT(EPOCH FROM now() - "startTime") WHERE ... AND "endTime" IS NULL RETURNING`. `giveUpChallenge` cierra con `gaveUp = true`.
- **Zona horaria**: igual que 001: columnas `timestamp(3)` sin zona escritas en UTC; se usa `now() AT TIME ZONE 'UTC'`.
- **Victoria**: `solved AND NOT knewWord AND timeTaken < creatorTime`, calculada al leer (no se guarda).

## R9. Página pública y vista previa

- **Decision**: `/desafio/[code]` es Server Component; `generateMetadata` arma `title` "¡@user te desafía en Matete!" y `description` "Resolvé una palabra de 7 letras en menos de 0:42". Sin imagen OG dinámica (fuera de alcance).
- **Login y regreso**: `signIn('google', { callbackUrl: '/desafio/<code>' })`.

## R10. UI del desafío

- **Decision**: `ChallengePlay` es un componente propio y reducido (casilleros + pool + teclado físico, sin pistas, sin salto, sin neuronas). No se reutiliza `GameEngine` porque está acoplado a niveles y economía (refactor fuera de alcance).
