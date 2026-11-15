# Constitution — Matete Web

## Principios

1. **El servidor es la única autoridad sobre lo que tiene valor.** Respuestas, puntos, neuronas, tiempos y resultados de desafíos se calculan y validan en el servidor. El cliente nunca recibe la solución de una palabra antes de resolverla ni decide cuánto acreditar.
2. **Identidad no falsificable.** Toda identidad (invitado o registrado) se deriva de una sesión o cookie firmada. Nunca se exponen `User.id` internos al cliente. Las funciones sociales son solo para usuarios registrados con Google.
3. **Operaciones de economía idempotentes y atómicas.** Todo lo que suma o resta puntos/neuronas va en transacción o en `UPDATE` condicional, y repetir la misma acción no vuelve a acreditar.
4. **Una sola fuente de verdad.** Cada parámetro de juego (costos, saldo inicial, mensajes) vive en un único lugar y el cliente lo lee de ahí.
5. **Schema reproducible.** Todo cambio de BD va por `prisma migrate dev` con migración commiteada. Nada de `db push` contra producción.
6. **Tests que tocan la base.** La lógica de server actions que depende de constraints (FK, unique, transacciones) se testea contra Postgres real, no solo con Prisma mockeado. Los tests citan el ID de caso de `tests/CASOS_DE_PRUEBA.md` o de RF/RNF.
7. **Sin tiempo real persistente.** El hosting (Hostinger) tiene límite de procesos concurrentes: nada de websockets ni polling agresivo. Lo social es asincrónico.
8. **Contenido de usuarios moderable.** Todo texto escrito por usuarios es reportable, ocultable por el admin y validado en servidor (longitud, rate limit).
9. **Idioma.** Interfaz en español rioplatense.

## Stack

- Next.js 16 (App Router, Server Actions), React, Tailwind, Framer Motion
- Prisma + PostgreSQL 16 (VPS Hostinger)
- NextAuth (Google) + invitados por cookie
- Vitest + React Testing Library

## Contexto de requerimientos

- Requerimientos actuales (RF/RNF): https://claude.ai/artifact/4W9efNiheeeNZq6SD3Bu9p
- Defectos conocidos y su corrección: `CODE_REVIEW.md` (IDs S-x, B-x, C-x)
- Casos de prueba: `tests/CASOS_DE_PRUEBA.md`

Leer estos antes de ejecutar /speckit-specify.

## Roadmap acordado (2026-11-15)

1. `001-hardening`: S-1..S-4 (identidad, validación server-side, idempotencia, tiempo en servidor) + B-1/B-2 (migración invitado) + B-3 (migraciones Prisma).
2. Social (objetivo: comunidad, viene de un grupo de Facebook de la app mobile): comentarios por palabra visibles tras resolverla, desafíos entre amigos por link, compartir resultado, seguir jugadores y ranking de amigos.
3. Postergado: palabras creadas por la comunidad.

## Modo de trabajo: Spec-Anchored

- La spec de cada feature (specs/NNN-feature/spec.md) es la fuente de verdad viva, no un documento de arranque descartable.
- Si el comportamiento de un feature cambia después de implementado, el flujo es: editar spec.md primero, correr /speckit-plan de nuevo, recién ahí tocar código.
- Nunca editar código de un feature sin reflejar el cambio en su spec correspondiente, salvo fixes triviales.
