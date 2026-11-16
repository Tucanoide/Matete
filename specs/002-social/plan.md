# Implementation Plan: Funciones sociales (compartir, comentarios, seguir, desafíos)

**Branch**: `002-social` | **Date**: 2026-11-15 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `specs/002-social/spec.md`

## Summary

Cuatro funciones asincrónicas sobre la base de `001-hardening`:

1. **Compartir**: `submitGuess` devuelve además el tiempo y las letras reveladas que registró el servidor; un módulo puro arma el texto estilo Wordle y el cliente usa `navigator.share` o el portapapeles.
2. **Comentarios**: tablas `Comment` y `CommentReport`. Todo acceso pasa por "¿este usuario acertó esta palabra?" (`Progress.guessed`). Rate limit y auto-ocultado atómicos (advisory lock por usuario y `UPDATE` condicional). Página de moderación para emails de `ADMIN_EMAILS`.
3. **Seguir**: tabla `Follow` (clave compuesta). Búsqueda por prefijo de `username`, pestaña "Amigos" en el ranking y marca `isFollowed` en los rankings existentes.
4. **Desafíos**: tablas `Challenge` y `ChallengeAttempt`. Página pública `/desafio/[code]` con metadata Open Graph; el intento se crea con una action, el tiempo lo mide Postgres y el resultado nunca toca la economía ni el progreso. Página `/desafios` ("Mis desafíos").

## Technical Context

**Language/Version**: TypeScript 5, Node 20+
**Primary Dependencies**: Next.js 16.1 (App Router, Server Actions), React 19, Prisma 7.5 + `@prisma/adapter-pg`, NextAuth 4.24 (Google), Tailwind 4, Framer Motion, lucide-react
**Storage**: PostgreSQL 16 (producción), 18 local para tests. Una migración nueva: `20261115200000_social`.
**Testing**: Vitest 4 + RTL (unit/componentes, jsdom); Vitest + Postgres real (`npm run test:integration`)
**Target Platform**: Web (navegador móvil y escritorio), Node en VPS Hostinger
**Project Type**: Aplicación web Next.js (un solo proyecto)
**Performance Goals**: paneles sociales < 1 s con 1000 comentarios por palabra o 500 seguidos (SC-006): índices por `(wordId, status, createdAt)` y paginación por cursor.
**Constraints**: sin websockets ni polling (constitución 7); sin `User.id` hacia el cliente (constitución 2); textos en español rioplatense (constitución 9).
**Scale/Scope**: ~100 usuarios activos hoy; diseñado para miles.

## Constitution Check

| # | Principio | Cumplimiento |
| --- | --- | --- |
| 1 | Servidor única autoridad | Tiempo y pistas del texto compartido y de desafíos salen de la base. Resultado de desafío calculado en Postgres. La solución solo se devuelve tras acertar o rendirse. ✅ |
| 2 | Identidad no falsificable; social solo con Google | Toda action resuelve identidad con `getCurrentUser()` de 001. Comentar, seguir, crear y jugar desafíos exigen `email` y `username`. **Excepción justificada**: compartir lo usan invitados porque no crea datos ni expone a nadie (ver Complexity Tracking). Las respuestas usan `username`, nunca `User.id`. ✅ |
| 3 | Economía idempotente y atómica | Los desafíos no tocan la economía (FR-028). Reportes, follows y cierre de intentos son idempotentes (`skipDuplicates`, `UPDATE ... WHERE endTime IS NULL`). ✅ |
| 4 | Una sola fuente de verdad | Límites sociales en `src/lib/social-rules.ts` (un solo lugar). ✅ |
| 5 | Schema reproducible | Migración `20261115200000_social` generada con `migrate diff` y commiteada. ✅ |
| 6 | Tests contra Postgres real | Suites `comments`, `follows`, `challenges`, `share-result` en `src/test/integration/`. ✅ |
| 7 | Sin tiempo real persistente | Todo se carga al abrir un panel o página. ✅ |
| 8 | Contenido moderable | Reporte, auto-ocultado a 3 reportes, borrado por el autor, moderación de admin, validación de longitud y rate limit en servidor. ✅ |
| 9 | Español rioplatense | ✅ |

Re-evaluado después del diseño: sin cambios.

## Project Structure

### Documentation (this feature)

```text
specs/002-social/
├── spec.md
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── social-actions.md
├── checklists/
│   └── requirements.md
└── tasks.md
```

### Source Code (repository root)

```text
prisma/
├── schema.prisma                         # + Comment, CommentReport, Follow, Challenge, ChallengeAttempt
└── migrations/20261115200000_social/     # nueva

src/lib/
├── social-rules.ts        # límites (280 caracteres, 30 s, 20/día, 3 reportes, 500 seguidos, 20 desafíos/día)
├── social-auth.ts         # requireSocialUser(), isAdmin()
├── share.ts               # buildShareText(), buildChallengeShareText(), formatDuration()
├── comment-text.ts        # normalizeCommentBody()
├── challenge-code.ts      # generateChallengeCode(), isChallengeCode()
└── relative-time.ts       # formatRelativeTime() en rioplatense

src/app/actions/
├── game.ts                # submitGuess devuelve `result`; rankings devuelven isFollowed/isMe
├── comments.ts            # getWordComments, postComment, reportComment, deleteComment
├── follows.ts             # followPlayer, unfollowPlayer, searchPlayers, getFriendsRanking
├── challenges.ts          # createChallenge, getChallenge, startChallenge, submitChallengeGuess, giveUpChallenge, getMyChallenges
└── moderation.ts          # getModerationQueue, moderateComment

src/app/
├── desafio/[code]/page.tsx        # página pública + generateMetadata (Open Graph)
├── desafios/page.tsx              # Mis desafíos
└── admin/comentarios/page.tsx     # moderación

src/components/social/
├── SolvedPanel.tsx        # Compartir + Desafiar + Comentarios, se muestra tras acertar
├── ShareButton.tsx
├── ChallengeButton.tsx
├── CommentsPanel.tsx
├── FollowButton.tsx
├── FriendsTab.tsx
├── ChallengePlay.tsx      # juego del desafío (pool/teclado simplificado)
└── ModerationList.tsx

src/components/game/GameEngine.tsx   # guarda el resultado del acierto y monta <SolvedPanel>
src/components/game/RankingBoard.tsx # pestaña Amigos + botones Seguir
src/components/layout/Header.tsx     # acceso a "Desafíos" para registrados

src/test/integration/
├── share-result.int.test.ts
├── comments.int.test.ts
├── follows.int.test.ts
└── challenges.int.test.ts
```

**Structure Decision**: se mantiene el proyecto único de Next.js. Las actions sociales van en archivos propios para no inflar `game.ts`; las reglas puras van en `src/lib` con tests unitarios.

## Riesgos y mitigaciones

| Riesgo | Mitigación |
| --- | --- |
| Spoiler por comentarios | Acceso solo si `Progress.guessed`; el servidor no devuelve ni la cantidad. Test explícito. |
| Spam de comentarios | Rate limit atómico con `pg_advisory_xact_lock` por usuario; auto-ocultado a 3 reportes; moderación. |
| Trampa en desafíos | Tiempo con `now()` de Postgres desde la creación del intento; un intento por jugador (unique); cierre condicional. |
| Desafío como atajo económico | Las actions de desafío no escriben `User` ni `Progress` (test SC-005). |
| GameEngine crece más | La UI social vive en `SolvedPanel`; GameEngine solo le pasa datos. |
| Producción | La migración solo agrega tablas; `prisma migrate deploy` después del baseline de 001. Nada se ejecuta en producción sin confirmación del dueño. |

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
| --- | --- | --- |
| Compartir disponible para invitados (principio 2 reserva lo social a Google) | Es el canal para traer al grupo de Facebook; no crea datos en el servidor ni muestra a nadie más que al propio jugador. | Exigir Google para compartir frena justo a quien más interesa que comparta (el que recién llega) sin ganar nada en seguridad. |
