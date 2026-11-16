# Implementation Plan: Mi recorrido

**Branch**: `003-recorrido` | **Date**: 2026-11-16 | **Spec**: [spec.md](./spec.md)

## Summary

Página `/recorrido` (Server Component) que llama a una Server Action de solo lectura `getMyJourney()` y muestra resumen, avance por nivel y desafíos. Botón "Mi recorrido" en la navegación del header para todos.

## Technical Context

**Stack**: Next.js 16 (App Router, Server Actions, build con webpack), React 19, Prisma 7 + PostgreSQL 16, NextAuth 4, Vitest 4 + RTL. Ver [../002-social/plan.md](../002-social/plan.md).
**Storage**: sin cambios de schema ni migraciones.
**Testing**: integración contra Postgres real para los agregados (constitución 6); componente para la página.
**Constraints**: sin `User.id` ni palabras en la respuesta; no crear invitados al leer (usar `getCurrentUser`, nunca `ensureCurrentUser`).

## Constitution Check

| Principio | Cumple |
|---|---|
| 1. Servidor autoridad / sin soluciones | Solo agregados; el test verifica que no viajan palabras |
| 2. Sin `User.id` | La posición en el ranking se calcula en SQL; no se devuelven ids |
| 4. Una sola fuente | Regla de victoria de desafíos igual a `challenges.ts` |
| 6. Tests contra Postgres | `journey.int.test.ts` |
| 7. Sin tiempo real | Página estática por pedido, sin polling |
| 9. Español rioplatense | Textos de la UI |

## Design

### Contrato: `getMyJourney()` (`src/app/actions/journey.ts`)

```ts
type JourneyLevel = { levelId: number; name: string; totalWords: number; guessed: number; skipped: number; avgTime: number | null; hints: number }
type Journey = {
  isGuest: boolean
  username: string | null
  levelId: number; points: number; neuronas: number
  rank: number | null            // solo Google; posición en el ranking global
  guessed: number; skipped: number; inProgress: number
  totalTime: number; avgTime: number | null; bestTime: number | null   // de aciertos con timeTaken > 0
  hintsUsed: number; solvedWithoutHints: number
  levels: JourneyLevel[]         // niveles con progreso + nivel actual, ascendente
  challenges: { created: number; played: number; won: number } | null  // solo Google + alias
}
getMyJourney(): Promise<{ ok: true; journey: Journey } | { ok: false; error: 'no_player' }>
```

- `totalTime` se suma desde `Progress` (no `User.totalTime`), para que sea coherente con los demás números.
- Ganado = `solved AND NOT knewWord AND timeTaken < creatorTime` (misma regla que `challenges.ts`).
- Ranking: `1 + count(usuarios con email que van adelante)` con el orden de `getGlobalRanking`.

### UI

- `src/app/recorrido/page.tsx` dentro de `PageShell`: tarjetas de resumen, barras por nivel, sección de desafíos, invitación a Google para invitados, invitación a jugar si no hay jugador.
- `Header`: botón "Mi recorrido" (ícono `Map`) visible para todos.

## Project Structure

```text
src/app/actions/journey.ts                 # nuevo
src/app/recorrido/page.tsx                 # nuevo
src/app/recorrido/page.test.tsx            # nuevo
src/test/integration/journey.int.test.ts   # nuevo
src/components/layout/Header.tsx           # botón
tests/CASOS_DE_PRUEBA.md                   # área JOUR
```
