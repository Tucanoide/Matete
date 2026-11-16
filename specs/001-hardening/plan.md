# Implementation Plan: Hardening de seguridad y bugs de producción

**Branch**: `001-hardening` | **Date**: 2026-11-15 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-hardening/spec.md`

## Summary

Mover al servidor toda decisión que da valor en el juego y cerrar la suplantación de cuentas, sin cambiar la experiencia del jugador ni el schema de datos.

- **Identidad**: cookie de invitado firmada con HMAC y `httpOnly`, que solo resuelve invitados. Transición de 30 días para la cookie vieja. El ranking deja de exponer `id`.
- **Respuesta**: el cliente recibe una `PublicWord` (definición, largo, pool, letras reveladas) y una nueva action `submitGuess` valida el intento y es la única que acredita.
- **Economía**: acierto, pistas y saltos con `UPDATE` condicionales dentro de transacciones. Son idempotentes y el saldo nunca queda negativo.
- **Tiempo**: se mide en el servidor desde `Progress.startTime`, que pasa a registrarse en la entrega de la palabra.
- **Migración invitado→Google**: una transacción con lock sobre el invitado y recálculo de puntos desde `Progress`.
- **Migraciones**: migración `sync_schema` baselinada en producción, más un script de recálculo de puntos y tiempo con respaldo.

Detalle de cada decisión en [research.md](./research.md).

## Technical Context

**Language/Version**: TypeScript 5.9, Node (runtime de Next.js 16)

**Primary Dependencies**: Next.js 16.1 (App Router, Server Actions), React 19, Prisma 7.5 con `@prisma/adapter-pg`, NextAuth 4.24 (Google), Framer Motion. Sin dependencias de runtime nuevas (HMAC con `node:crypto`). Dev nuevas: `tsx` y `dotenv-cli` (scripts y test de integración).

**Storage**: PostgreSQL 16 en VPS Hostinger (producción); Postgres 18 local para tests.

**Testing**: Vitest 4 + React Testing Library (jsdom) para unit/componentes; nueva suite Vitest de integración (environment `node`) contra Postgres real.

**Target Platform**: Web (desktop y mobile), servidor Node en Hostinger.

**Project Type**: Aplicación web full-stack Next.js (un único proyecto).

**Performance Goals**: respuesta a un intento < 1 s percibido (SC-007). Agrega 1 round-trip por intento completo y 1 por palabra entregada (`startWord`); no por tecla.

**Constraints**: límite de procesos concurrentes de Hostinger (sin servicios nuevos, sin tiempo real); `cookies().set()` no disponible al renderizar Server Components; producción no puede quedar con datos alterados fuera de lo especificado.

**Scale/Scope**: ~486 palabras en 37 niveles; decenas de usuarios (46 invitados al 2026-08-06). Archivos afectados: `src/app/actions/game.ts`, `src/app/actions/sync.ts`, `src/lib/auth.ts`, `src/app/page.tsx`, `src/components/game/GameEngine.tsx`, `src/components/game/RankingBoard.tsx`, más nuevos `src/lib/guest-cookie.ts`, `src/lib/public-word.ts`, `scripts/recalculate-points.ts` y una migración Prisma.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| # | Principio | Evaluación | Estado |
| --- | --- | --- | --- |
| 1 | El servidor es la única autoridad sobre lo que tiene valor | Es el objetivo central: `submitGuess`, tiempo de servidor, `PublicWord` sin solución | PASS |
| 2 | Identidad no falsificable, sin `User.id` al cliente | Cookie HMAC `httpOnly`; ranking sin `id`. El `userId` va dentro de la cookie firmada, pero es `httpOnly` e inalterable | PASS |
| 3 | Economía idempotente y atómica | `UPDATE` condicionales + `$transaction` (R4, R5) | PASS |
| 4 | Una sola fuente de verdad | Saldo inicial 50 unificado. Los costos siguen duplicados (B-4), declarado fuera de alcance en la spec | PASS (con excepción declarada) |
| 5 | Schema reproducible, nada de `db push` | Migración `sync_schema` + baseline (R6) | PASS |
| 6 | Tests que tocan la base | Suite de integración contra Postgres real (R8) | PASS |
| 7 | Sin tiempo real persistente | Nada de websockets ni polling | PASS |
| 8 | Contenido de usuarios moderable | No aplica (no hay contenido nuevo de usuarios) | N/A |
| 9 | Interfaz en español rioplatense | Sin textos nuevos visibles salvo errores, que siguen el tono actual | PASS |

**Re-check post-diseño (Phase 1)**: sin cambios. El diseño no agrega columnas, servicios ni dependencias de runtime. La excepción del principio 4 (costos) sigue documentada y acotada a B-4.

## Project Structure

### Documentation (this feature)

```text
specs/001-hardening/
├── plan.md                      # Este archivo
├── research.md                  # Phase 0: decisiones R1..R8
├── data-model.md                # Phase 1: invariantes y estados
├── quickstart.md                # Phase 1: guía de validación
├── contracts/
│   └── server-actions.md        # Phase 1: contrato UI ↔ server actions
├── checklists/
│   └── requirements.md          # Calidad de la spec
└── tasks.md                     # Phase 2 (/speckit-tasks)
```

### Source Code (repository root)

```text
src/
├── app/
│   ├── page.tsx                         # MOD: pasa PublicWord al GameEngine
│   └── actions/
│       ├── game.ts                      # MOD: getUser/ensureUser, submitGuess, startWord,
│       │                                #      getNextWord, requestHint, moveToNextLevel, rankings
│       └── sync.ts                      # MOD: migración atómica e idempotente
├── lib/
│   ├── auth.ts                          # MOD: fallback de neuronas 150 → 50
│   ├── guest-cookie.ts                  # NEW: firmar/verificar cookie, transición legacy
│   └── public-word.ts                   # NEW: PublicWord + pool de letras en servidor
├── components/game/
│   ├── GameEngine.tsx                   # MOD: trabaja con length/pool/revealed, llama submitGuess/startWord
│   └── RankingBoard.tsx                 # MOD: key por posición
└── test/
    └── integration/                     # NEW: suites contra Postgres real
        ├── setup.ts                     #   reset de BD, mocks de cookies/sesión
        ├── identity.int.test.ts
        ├── guess-and-economy.int.test.ts
        └── guest-migration.int.test.ts

prisma/
├── schema.prisma                        # sin cambios de modelo
└── migrations/<timestamp>_sync_schema/  # NEW

scripts/
└── recalculate-points.ts                # NEW: dry-run / --apply con respaldo

vitest.integration.config.ts             # NEW
```

**Structure Decision**: un solo proyecto Next.js, igual que hoy. La lógica nueva va en `src/lib/` (funciones puras, testeables en unit) y las server actions existentes se modifican en su lugar. Los tests de integración se separan en `src/test/integration/` con config propia, para que `npm test` siga corriendo rápido y sin base de datos.

## Riesgos y mitigaciones

| Riesgo | Mitigación |
| --- | --- |
| Romper la experiencia del GameEngine al quitarle la palabra | Cambios mínimos y localizados (no refactor). Los tests de componente existentes se adaptan a las nuevas props manteniendo las aserciones. Prueba manual del quickstart §3 |
| Invitados con cookie vieja pierden progreso | Transición de 30 días (R1); SC-008 |
| `migrate resolve` o recálculo mal aplicados en producción | `pg_dump` previo, dry-run, respaldo en tabla, confirmación explícita del dueño en cada paso con escritura en prod |
| Divergencia real entre producción y `schema.prisma` mayor a la conocida | `migrate diff` contra producción (lectura) antes de generar la migración; si hay más diferencias se documentan y se ajusta la migración |
| `GUEST_COOKIE_SECRET` ausente en Hostinger | Fail closed + log de error; checklist de deploy (quickstart §5) |

## Complexity Tracking

Sin violaciones de la constitución que justificar.
