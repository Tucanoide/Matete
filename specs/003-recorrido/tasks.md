# Tasks: Mi recorrido (003-recorrido)

**Input**: [plan.md](./plan.md), [spec.md](./spec.md)

## Phase 1: User Story 1 — Ver mi recorrido (P1)

- [X] T001 [US1] Tests de integración de `getMyJourney` (totales, por nivel, tiempos nulos, sin jugador no crea usuario, invitado sin ranking, Google con ranking, sin palabras ni ids) en `src/test/integration/journey.int.test.ts`
- [X] T002 [US1] Implementar `getMyJourney()` en `src/app/actions/journey.ts`
- [X] T003 [US1] Página `src/app/recorrido/page.tsx` (resumen, barras por nivel, invitaciones) y test `src/app/recorrido/page.test.tsx`
- [X] T004 [US1] Botón "Mi recorrido" en `src/components/layout/Header.tsx`

## Phase 2: User Story 2 — Desafíos (P2)

- [X] T005 [US2] Tests de integración de la sección de desafíos (creados, jugados, ganados con "ya la conocía"; null para invitado y sin alias) en `journey.int.test.ts`
- [X] T006 [US2] Agregar `challenges` a `getMyJourney()` y su sección en la página

## Phase 3: Polish

- [X] T007 Casos JOUR en `tests/CASOS_DE_PRUEBA.md`
- [X] T008 `tsc`, lint, unit, integración y `next build --webpack`; captura de la página en escritorio y angosto
