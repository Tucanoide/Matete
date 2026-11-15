# Tasks: Funciones sociales (002-social)

**Input**: [plan.md](./plan.md), [spec.md](./spec.md), [data-model.md](./data-model.md), [contracts/social-actions.md](./contracts/social-actions.md), [research.md](./research.md)

**Tests**: sí. La constitución (principio 6) exige tests contra Postgres real para la lógica con constraints. Se escriben antes de la implementación de cada historia.

## Phase 1: Setup

- [X] T001 Agregar `ADMIN_EMAILS` a `.env.example` y `.env.test.example`
- [X] T002 Agregar los modelos `Comment`, `CommentReport`, `Follow`, `Challenge`, `ChallengeAttempt` y sus relaciones en `prisma/schema.prisma`
- [X] T003 Generar la migración `prisma/migrations/20261115200000_social/migration.sql` con `prisma migrate diff --from-migrations --to-schema` y regenerar el cliente
- [X] T004 Incluir las tablas nuevas en el `TRUNCATE` de `resetDb` y agregar helpers `solveWord`, `registeredWithUsername` en `src/test/integration/harness.ts`

## Phase 2: Foundational

- [X] T005 [P] Límites sociales en `src/lib/social-rules.ts`
- [X] T006 [P] `requireSocialUser()` e `isAdmin()` en `src/lib/social-auth.ts` (usa `getCurrentUser` de `src/lib/identity.ts`)
- [X] T007 [P] `formatRelativeTime()` en `src/lib/relative-time.ts` con tests en `src/lib/relative-time.test.ts`

## Phase 3: User Story 1 — Compartir (P1)

**Goal**: compartir el resultado sin spoilear. **Independent test**: resolver, compartir, el texto tiene nivel/casilleros/tiempo/pistas/link y no la palabra.

- [X] T008 [P] [US1] Tests de `buildShareText`, `buildChallengeShareText`, `formatDuration` en `src/lib/share.test.ts`
- [X] T009 [P] [US1] Test de integración: `submitGuess` devuelve `result` con tiempo y letras reveladas del servidor, también con `alreadySolved`, en `src/test/integration/share-result.int.test.ts`
- [X] T010 [US1] Implementar `src/lib/share.ts`
- [X] T011 [US1] Agregar `result: { timeTaken, revealedIndices }` a `submitGuess` en `src/app/actions/game.ts`
- [X] T012 [US1] `ShareButton` (navigator.share → portapapeles → aviso "Copiado") en `src/components/social/ShareButton.tsx`
- [X] T013 [US1] `SolvedPanel` (contenedor tras acertar) en `src/components/social/SolvedPanel.tsx` y montarlo en `src/components/game/GameEngine.tsx` guardando el resultado del acierto
- [X] T014 [US1] Tests de componente de `ShareButton` en `src/components/social/ShareButton.test.tsx` y ajuste de mocks en `src/components/game/GameEngine.test.tsx`

## Phase 4: User Story 2 — Comentarios (P1)

**Goal**: comentar solo después de acertar, con moderación. **Independent test**: A comenta; B no ve nada hasta acertar; 3 reportes lo ocultan.

- [X] T015 [P] [US2] Tests de `normalizeCommentBody` en `src/lib/comment-text.test.ts`
- [X] T016 [P] [US2] Tests de integración (visibilidad, publicación, rate limit concurrente, reportes, borrado, moderación) en `src/test/integration/comments.int.test.ts`
- [X] T017 [US2] Implementar `src/lib/comment-text.ts`
- [X] T018 [US2] Implementar `getWordComments`, `postComment`, `reportComment`, `deleteComment` en `src/app/actions/comments.ts`
- [X] T019 [US2] Implementar `getModerationQueue`, `moderateComment` en `src/app/actions/moderation.ts`
- [X] T020 [US2] `CommentsPanel` (lista paginada, formulario, reportar, borrar, invitación a ingresar/elegir usuario) en `src/components/social/CommentsPanel.tsx` y sumarlo a `SolvedPanel`
- [X] T021 [US2] Página de moderación `src/app/admin/comentarios/page.tsx` + `src/components/social/ModerationList.tsx`
- [X] T022 [US2] Tests de componente de `CommentsPanel` en `src/components/social/CommentsPanel.test.tsx`

## Phase 5: User Story 3 — Seguir y ranking de amigos (P2)

**Goal**: seguir por username y competir con amigos. **Independent test**: A sigue a B; pestaña Amigos muestra A y B; deja de seguir y B desaparece.

- [X] T023 [P] [US3] Tests de integración (seguir/dejar, rechazos, tope 500, búsqueda, ranking de amigos, `isFollowed`/`isMe` en rankings sin `id`) en `src/test/integration/follows.int.test.ts`
- [X] T024 [US3] Implementar `followPlayer`, `unfollowPlayer`, `searchPlayers`, `getFriendsRanking` en `src/app/actions/follows.ts`
- [X] T025 [US3] Agregar `isMe` e `isFollowed` a `getGlobalRanking` y `getLevelRanking` en `src/app/actions/game.ts`
- [X] T026 [US3] `FollowButton` en `src/components/social/FollowButton.tsx` y `FriendsTab` (búsqueda + tabla) en `src/components/social/FriendsTab.tsx`
- [X] T027 [US3] Pestaña "Amigos" y botones Seguir en `src/components/game/RankingBoard.tsx`

## Phase 6: User Story 4 — Desafíos (P2)

**Goal**: desafiar con link, tiempo medido en servidor, sin tocar la economía. **Independent test**: A crea; B juega y gana; ambos ven el resultado.

- [X] T028 [P] [US4] Tests de `generateChallengeCode`/`isChallengeCode` en `src/lib/challenge-code.test.ts`
- [X] T029 [P] [US4] Tests de integración (crear idempotente y con tope, solo palabra acertada, vista pública sin solución, un intento, creador no juega, tiempo de servidor, `knewWord`, victoria y orden, rendirse, sin cambios de economía/progreso, mis desafíos) en `src/test/integration/challenges.int.test.ts`
- [X] T030 [US4] Implementar `src/lib/challenge-code.ts`
- [X] T031 [US4] Implementar `createChallenge`, `getChallenge`, `startChallenge`, `submitChallengeGuess`, `giveUpChallenge`, `getMyChallenges` en `src/app/actions/challenges.ts`
- [X] T032 [US4] `ChallengeButton` (crear + compartir link) en `src/components/social/ChallengeButton.tsx` y sumarlo a `SolvedPanel`
- [X] T033 [US4] Página `src/app/desafio/[code]/page.tsx` con `generateMetadata` y `src/components/social/ChallengePlay.tsx`
- [X] T034 [US4] Página `src/app/desafios/page.tsx` (Mis desafíos) y acceso desde `src/components/layout/Header.tsx`
- [X] T035 [US4] Tests de componente de `ChallengePlay` en `src/components/social/ChallengePlay.test.tsx`

## Phase 7: Polish

- [X] T036 Correr `npm test`, `npm run test:integration`, `tsc --noEmit` y `next build`; corregir lo que falle
- [X] T037 Revisión de fugas: ninguna respuesta nueva devuelve `User.id`, ni solución/comentarios antes de acertar (grep + tests)
- [X] T038 Agregar los casos sociales a `tests/CASOS_DE_PRUEBA.md` (área SOC) y actualizar `quickstart.md` con resultados
- [ ] T039 Prueba manual en navegador (quickstart §2) — **a cargo del dueño** (el agente no puede abrir un servidor local desde el navegador integrado)

## Dependencies

- Setup (T001–T004) → Foundational (T005–T007) → historias.
- US1 primero (crea `SolvedPanel`, que usan US2 y US4).
- US2, US3 y US4 son independientes entre sí después de US1; US3 no depende de `SolvedPanel`.
- Dentro de cada historia: tests → lib → actions → UI.

## Parallel Opportunities

- T005, T006, T007.
- En cada historia, los tests unitarios y de integración ([P]).
- US3 completa en paralelo con US2/US4.

## Implementation Strategy

MVP = US1 + US2 (compartir y comentarios: lo que trae y retiene al grupo de Facebook). Luego US3 y US4. Cada historia se puede desplegar sola.
