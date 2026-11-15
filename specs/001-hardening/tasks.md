---
description: "Task list for 001-hardening"
---

# Tasks: Hardening de seguridad y bugs de producción

**Input**: Design documents from `/specs/001-hardening/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/server-actions.md, quickstart.md

**Tests**: Incluidos. La constitución (principio 6) exige tests contra Postgres real para la lógica que depende de constraints y transacciones, y la spec define criterios verificables (SC-001..SC-008).

**Organization**: Por historia de usuario. La migración `sync_schema` se adelanta a Foundational porque la suite de integración la necesita para levantar la base; US4 queda con la verificación contra producción y el baseline.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: paralelizable (archivos distintos, sin dependencias pendientes)
- **[Story]**: US1..US4 según spec.md

---

## Phase 1: Setup

**Purpose**: herramientas para scripts y tests de integración

- [X] T001 Instalar dev dependencies `tsx` y `dotenv-cli` y agregar scripts `test:integration` (`dotenv -e .env.test -- vitest run -c vitest.integration.config.ts`) y `typecheck` (`tsc --noEmit`) en package.json
- [X] T002 [P] Crear .env.test.example con DATABASE_URL, TEST_DATABASE_URL, GUEST_COOKIE_SECRET, GUEST_LEGACY_COOKIE_UNTIL, NEXTAUTH_SECRET y agregar `.env.test` a .gitignore
- [X] T003 [P] Agregar GUEST_COOKIE_SECRET y GUEST_LEGACY_COOKIE_UNTIL (con comentario sobre la transición de 30 días) a .env.example
- [X] T004 Levantar Postgres local y crear la base `matete_test` (no versionado; documentado en specs/001-hardening/quickstart.md)

---

## Phase 2: Foundational (bloquea a todas las historias)

**Purpose**: schema reproducible, harness de integración y librerías puras compartidas

- [X] T005 Unificar el saldo inicial: cambiar el fallback `?? 150` por `?? 50` en src/lib/auth.ts
- [X] T006 Generar la migración prisma/migrations/<timestamp>_sync_schema/migration.sql con `prisma migrate diff` desde las migraciones existentes hacia prisma/schema.prisma (shadow DB local); revisar que sea aditiva (username + unique, revealedIndices, Setting, GameMessage, default neuronas 50)
- [X] T007 Verificar que `prisma migrate reset --force` contra TEST_DATABASE_URL aplica las 3 migraciones sin error y que la estructura coincide con schema.prisma (`prisma migrate diff` vacío)
- [X] T008 Crear vitest.integration.config.ts (environment node, include `src/test/integration/**/*.int.test.ts`, alias `@`, fileParallelism false) y excluir `src/test/integration/**` de vitest.config.ts
- [X] T009 Crear src/test/integration/setup.ts: verificar que DATABASE_URL apunta a una base `*_test`, truncar tablas de jugadores entre tests, sembrar 2 niveles con palabras conocidas y Setting `costo_pista`, y mocks de `next/headers` (cookie jar en memoria) y `next-auth` (sesión configurable)
- [X] T010 [P] Implementar src/lib/public-word.ts: tipo `PublicWord`, `buildLetterPool(word)` (14 letras, distractores `AEIOURSTNMPL`, Fisher–Yates) y `toPublicWord(word, revealedIndices)` según contracts/server-actions.md
- [X] T011 [P] Tests unitarios en src/lib/public-word.test.ts: pool de 14 con todas las letras de la solución, sin campo con la solución, revealed con letra y posición correctas, palabras con ñ
- [X] T012 [P] Implementar src/lib/guest-cookie.ts: `signGuestToken(userId, now)`, `verifyGuestToken(token, now)` (HMAC-SHA256 base64url, timingSafeEqual, exp 30 días, fail closed sin secreto), `isLegacyWindowOpen(now)` leyendo GUEST_LEGACY_COOKIE_UNTIL, constantes de nombres y opciones de cookie
- [X] T013 [P] Tests unitarios en src/lib/guest-cookie.test.ts: token válido, carácter alterado, firma con otra clave, expirado, formato inválido, sin secreto, ventana legacy abierta/cerrada/ausente

**Checkpoint**: base reproducible + harness + librerías listas

---

## Phase 3: User Story 1 - Mi cuenta no la puede usar otra persona (P1) 🎯 MVP

**Goal**: identidad de invitado no falsificable y ningún `User.id` expuesto

**Independent Test**: alterar o fabricar la credencial de invitado y apuntarla a otra cuenta debe fallar siempre; el ranking no contiene ids

### Tests

- [X] T014 [P] [US1] Tests de integración en src/test/integration/identity.int.test.ts: cookie alterada → visitante nuevo; cookie firmada de una cuenta con email → rechazada; legacy de invitado dentro de ventana → aceptada y reemplazada por `matete-guest` firmada y legacy borrada; legacy fuera de ventana → rechazada; legacy de cuenta registrada → rechazada; rankings global y de nivel sin `id` (FR-001..FR-005)

### Implementation

- [X] T015 [US1] Reescribir `getUser()` y `ensureUser()` en src/app/actions/game.ts para usar src/lib/guest-cookie.ts: sesión Google primero; luego cookie firmada (solo `email IS NULL`); luego legacy dentro de ventana (solo invitado) con reemisión best-effort en try/catch; `ensureUser` emite cookie firmada httpOnly
- [X] T016 [US1] Quitar `id` del select y del resultado de `getGlobalRanking` y `getLevelRanking` en src/app/actions/game.ts
- [X] T017 [P] [US1] Usar la posición como `key` en lugar de `user.id` en src/components/game/RankingBoard.tsx
- [X] T018 [US1] Actualizar src/app/actions/sync.ts para leer el invitado con la cookie firmada (o legacy en ventana) vía helpers de src/lib/guest-cookie.ts y borrar ambas cookies

**Checkpoint**: US1 verificable con T014

---

## Phase 4: User Story 2 - Los puntos reflejan lo que realmente jugué (P1)

**Goal**: solución solo en el servidor, acreditación idempotente, tiempo de servidor, cobros atómicos

**Independent Test**: repetir aciertos, inventar respuestas o tiempos y pedir pistas concurrentes no altera los totales indebidamente

### Tests

- [X] T019 [P] [US2] Tests de integración en src/test/integration/guess-and-economy.int.test.ts: `startWord` registra entrega una sola vez; `submitGuess` correcto 100 veces → +10/+1 una vez (SC-003); incorrecto → sin cambios; `invalid_guess` por largo/caracteres; palabra de otro nivel o salteada → `not_in_play`; `timeTaken` ≈ tiempo desde entrega; 2 pistas simultáneas con saldo para una → una cobrada y saldo ≥ 0; 3ª pista → rechazada sin cobro; pista/salto sobre palabra acertada → rechazado sin cobro; `getNextWord`/`getInitialGameState` devuelven `PublicWord` sin solución; `moveToNextLevel` rechaza con nivel incompleto

### Implementation

- [X] T020 [US2] Implementar en src/app/actions/game.ts los helpers `isWordInPlay(tx, user, wordId)` y `registerDelivery(tx, userId, wordId)` (INSERT ... ON CONFLICT DO NOTHING sobre Progress)
- [X] T021 [US2] Implementar la action `startWord(wordId)` en src/app/actions/game.ts según contracts/server-actions.md
- [X] T022 [US2] Implementar la action `submitGuess(wordId, guess)` en src/app/actions/game.ts: validación de formato, comparación case-insensitive, transacción con UPDATE condicional RETURNING timeTaken, incremento de usuario solo si hubo fila, estado de nivel; eliminar `recordWordSolved`
- [X] T023 [US2] Reescribir `useHint` en src/app/actions/game.ts: validar palabra en juego, cobro con `updateMany neuronas gte cost`, revealedIndices optimista con `equals`, salto condicional, todo en `$transaction`; la pista devuelve `revealed: { index, letter }`
- [X] T024 [US2] Convertir `getRandomWordFromLevel` en `getNextWord` (devuelve `PublicWord`, registra entrega, usa `user.levelId`), adaptar `getInitialGameState` (devuelve `{ word: PublicWord, levelId, neuronas, needsUsername }`, registra entrega, `null` en lugar de TypeError si no hay palabras) y `moveToNextLevel` (exige nivel completo, devuelve `PublicWord`), y eliminar `getWordsByLevel` en src/app/actions/game.ts
- [X] T025 [US2] Adaptar src/app/page.tsx para pasar `PublicWord` (`initialWord: PublicWord`) al GameEngine
- [X] T026 [US2] Adaptar src/components/game/GameEngine.tsx: estado con `wordLength`, `pool`, `revealed` (map índice→letra) y `solution` post-acierto en lugar de `currentWord`; llamar `startWord` al montar y por palabra nueva; validar con `submitGuess` al completar los casilleros; pista de letra usa `revealed` del servidor; pool viene del servidor; mismas animaciones, sonidos y mensajes
- [X] T027 [US2] Adaptar los tests de componente a las nuevas props y actions manteniendo sus aserciones de experiencia en src/components/game/GameEngine.test.tsx, src/test/smoke-progression-50.test.tsx y src/components/game/Modals.test.tsx
- [X] T028 [US2] Reemplazar los tests con Prisma mockeado de `recordWordSolved` por los equivalentes de `submitGuess` en src/app/actions/game.server.test.ts y src/test/actions-progression.test.ts

**Checkpoint**: US1 + US2 = MVP de seguridad

---

## Phase 5: User Story 3 - Loguearme con Google no rompe ni duplica mi progreso (P2)

**Goal**: migración invitado→Google atómica, idempotente y sin pérdidas

**Independent Test**: invitado con progreso + login + 10 recargas → totales correctos y estables

### Tests

- [X] T029 [P] [US3] Tests de integración en src/test/integration/guest-migration.int.test.ts: invitado con aciertos y saltos migra completo y se borra; 10 llamadas secuenciales y 2 concurrentes → mismos totales; neuronas del invitado < 50 → la cuenta no pierde; palabra resuelta por ambos cuenta una vez; invitado sin progreso; cookie de invitado inexistente/borrado (FR-018..FR-022)

### Implementation

- [X] T030 [US3] Reescribir `migrateGuestProgress` en src/app/actions/sync.ts como transacción: `SELECT ... FOR UPDATE` del invitado con `email IS NULL`, merge de Progress, borrado de Progress e invitado, recálculo de `points`/`totalTime` desde Progress, `neuronas += max(0, guest - 50)`, `levelId = max`; borrar cookies después del commit; devolver `{ success, migrated }`
- [X] T031 [US3] Actualizar src/app/actions/sync.test.ts al nuevo contrato (o eliminar los casos cubiertos por T029 que dependen de Prisma mockeado)

**Checkpoint**: US3 verificable con T029

---

## Phase 6: User Story 4 - La app se puede volver a instalar desde cero (P3)

**Goal**: migraciones equivalentes a producción y datos históricos corregidos

**Independent Test**: base vacía + migraciones + carga de datos → partida completa; diff contra producción vacío

- [X] T032 [US4] Comparar en modo solo lectura la estructura de producción contra las migraciones con `prisma migrate diff` y documentar el resultado en specs/001-hardening/research.md (R6); ajustar la migración de T006 si aparecen diferencias
- [X] T033 [P] [US4] Implementar scripts/recalculate-points.ts: dry-run por defecto (usuarios afectados, diferencia de puntos y tiempo), `--apply` crea `User_backup_20261115` y recalcula `points` y `totalTime` desde Progress en una transacción; neuronas sin tocar
- [X] T034 [US4] Test de integración del script en src/test/integration/recalculate-points.int.test.ts: dry-run no escribe; apply crea respaldo y deja `points = count(guessed)` y `totalTime = sum(timeTaken)`; neuronas intactas
- [X] T035 [US4] Ejecutar de punta a punta el quickstart §1 (reset + carga de niveles/palabras + seed) sobre una base nueva y jugar una partida en `npm run dev` — base desde cero OK (37 niveles, 486 palabras), página real sin la solución; la partida manual en navegador queda para el dueño (quickstart §3)

**Checkpoint**: todas las historias completas

---

## Phase 7: Polish & cross-cutting

- [X] T036 [P] Agregar a specs/001-hardening/quickstart.md los comandos exactos finales de Prisma 7 usados en T006/T032
- [X] T037 Correr `npm test`, `npm run test:integration` y `npm run typecheck` en verde; corregir lo que falle
- [X] T038 Verificar con grep que ningún archivo bajo src/components ni respuesta de action devuelve `word.word` antes de un acierto ni `id` de usuario
- [X] T039 [P] Actualizar el estado de los RF/RNF afectados en CODE_REVIEW.md (sección de estado) y anotar en specs/001-hardening/quickstart.md §5 los pasos de producción pendientes de confirmación (migrate resolve, variables en Hostinger, recálculo)

---

## Dependencies & Execution Order

- **Setup (T001–T004)** → **Foundational (T005–T013)** → historias.
- **US1 (T014–T018)** y **US2 (T019–T028)** dependen de Foundational. US2 reutiliza `getUser/ensureUser` de US1 (T015) → hacer T015 antes de T020.
- **US3 (T029–T031)** depende de T018 (lectura de cookie en sync.ts).
- **US4 (T032–T035)** depende de T006/T007; T033–T034 son independientes del resto.
- **Polish** al final.

```text
Setup → Foundational ─┬─ US1 ─┬─ US2 ─┐
                      │       └─ US3 ─┼─ Polish
                      └─ US4 ─────────┘
```

## Parallel Opportunities

- Foundational: T010/T011 (public-word) en paralelo con T012/T013 (guest-cookie).
- US1: T014 (tests) y T017 (RankingBoard) en paralelo con T015/T016.
- US2: T019 en paralelo con T020–T024; T027 y T028 en paralelo entre sí tras T026.
- US4: T033 en paralelo con cualquier historia.

## Implementation Strategy

1. **MVP**: Setup + Foundational + US1 + US2. Cierra la suplantación y el puntaje trucho, que es lo que bloquea lo social.
2. US3 (migración Google) antes de anunciar el juego en el grupo de Facebook, porque ahí entran los registros.
3. US4 + recálculo en producción como último paso, con confirmación del dueño en cada escritura en prod.

## Production steps (NO ejecutar sin confirmación explícita del dueño)

- `pg_dump` de producción.
- Variables `GUEST_COOKIE_SECRET` y `GUEST_LEGACY_COOKIE_UNTIL` en Hostinger.
- `prisma migrate resolve --applied <timestamp>_sync_schema` en producción.
- Deploy.
- `scripts/recalculate-points.ts` dry-run y luego `--apply` en producción.
