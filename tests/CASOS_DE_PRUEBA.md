# Casos de prueba — Matete Web

**Versión:** 2.1 · **Fecha:** 2026-11-16 · **Base:** branch `002-social` (incluye `001-hardening`)
**Documentos relacionados:** [specs/001-hardening](../specs/001-hardening/spec.md) · [specs/002-social](../specs/002-social/spec.md) · `CODE_REVIEW.md` (local, **no se versiona**: el repo es público y ese archivo describe infraestructura de producción)

Catálogo de casos de prueba de la app. Cada caso tiene un ID estable que se cita en el nombre del test, para que el mapeo sobreviva a los refactors:

```ts
it('[GAME-03] no acredita puntaje al re-enviar una palabra ya resuelta', async () => { ... })
```

Para ver qué tests cubren un caso: `grep -rn "\[GAME-03\]" src`.

El historial de corridas de prueba y corrección, con los issues de cada ciclo, está en [HISTORIAL_CICLOS.md](./HISTORIAL_CICLOS.md).

**Cambios de la v2.1 (2026-11-16):** tres ciclos de prueba y corrección cerraron los 12 defectos abiertos y los parciales/sin cobertura que tenían defecto detrás (AUTH-08, SYNC-08, USER-07, DATA-05, DATA-06, UI-16). Casos nuevos: SYNC-10, RANK-07, UI-21, UI-22 y SOC-25. La Server Action `useHint` pasó a llamarse `requestHint` (el prefijo `use` la hacía pasar por un hook de React). Queda abierto solo AUTH-09 (login real con Google, manual).

**Cambios respecto de la v1.0 (2026-08-06):** los defectos que corrigió `001-hardening` pasan a ✅ con su test; se agregan el caso de inicio (INI), los de identidad firmada (AUTH-10..12), entrega y formato (GAME-12, GAME-13), fin de juego (PROG-09), alias en la migración (SYNC-09), UI (UI-17..19), infraestructura pública (INFRA-06) y las funciones sociales (SOC). Los casos que describían código que ya no existe (`recordWordSolved`, `getRandomWordFromLevel`, `getUser`, cookie `matete-guest-id` como única identidad) se reescribieron sobre el código actual.

---

## Cómo leer este documento

### Estado

| Símbolo | Significado |
|---|---|
| ✅ | Cubierto por un test que pasa (o por una verificación manual registrada, cuando el tipo es `manual`) |
| ⚠️ | Cubierto parcialmente |
| ❌ | Sin cobertura; no se sabe si falla |
| 🔴 | **Defecto abierto.** El comportamiento esperado falla hoy (reproducido o verificado en el código). No hay test en la suite porque fallaría: escribirlo junto con la corrección |
| ➖ | No aplica con la configuración actual |

### Tipo

| Tipo | Entorno | Corre con |
|---|---|---|
| `unit` | Vitest + jsdom, dependencias mockeadas | `npm test` |
| `int` | Vitest + **Postgres real** (ver Anexo A) | `npm run test:integration` |
| `e2e` | Navegador real contra la app levantada | Playwright (a incorporar) |
| `manual` | Verificación humana o de línea de comandos | — |

### Prioridad

- **P0** — Bloquea el despliegue público. Datos incorrectos, pérdida de progreso o acceso indebido.
- **P1** — Defecto funcional visible para el jugador.
- **P2** — Robustez, borde, calidad.

---

## Resumen de cobertura

| Área | Casos | ✅ | ⚠️ | ❌ | 🔴 | ➖ |
|---|---|---|---|---|---|---|
| INI — Primer ingreso | 1 | 1 | 0 | 0 | 0 | 0 |
| AUTH — Identidad y sesión | 12 | 11 | 0 | 1 | 0 | 0 |
| GAME — Resolución y puntaje | 13 | 13 | 0 | 0 | 0 | 0 |
| HINT — Pistas y economía | 10 | 9 | 0 | 0 | 0 | 1 |
| PROG — Progresión de niveles | 9 | 9 | 0 | 0 | 0 | 0 |
| SYNC — Migración invitado→cuenta | 10 | 10 | 0 | 0 | 0 | 0 |
| RANK — Rankings | 7 | 7 | 0 | 0 | 0 | 0 |
| USER — Alias | 7 | 7 | 0 | 0 | 0 | 0 |
| DATA — Schema, migraciones, datos | 7 | 7 | 0 | 0 | 0 | 0 |
| UI — Interacción del cliente | 21 | 21 | 0 | 0 | 0 | 0 |
| INFRA — Dependencias, build, repo | 6 | 6 | 0 | 0 | 0 | 0 |
| SOC — Funciones sociales | 26 | 26 | 0 | 0 | 0 | 0 |
| **Total** | **129** | **127** | **0** | **1** | **0** | **1** |

Suites al 2026-11-16: **121 tests unit/componentes** (18 archivos) y **128 de integración** contra Postgres real (11 archivos), todos en verde. `tsc --noEmit`, `npm run lint` (0 errores, 0 warnings), `npm audit --audit-level=high` y `next build` pasan.

El único caso sin cobertura es AUTH-09 (login real con Google), que es manual y queda a cargo del dueño junto con las tareas del Anexo B.

---

## INI — Primer ingreso

### INI-01 — Ingreso sin login con nombre de fantasía
**Tipo:** `int` + `unit` · **Prioridad:** P1 · **Estado:** ✅
**Tests:** `src/test/integration/onboarding.int.test.ts`, `GameEngine.test.tsx` (`[INI-01]`)

- **Precondición:** Navegador sin cookies ni sesión de Google.
- **Pasos y esperado:**
  1. Abrir la home → se muestra la primera palabra del nivel 1 con 50 neuronas, **sin** pedir nombre. La respuesta no contiene la solución. Todavía no se crea ningún usuario.
  2. Al montarse el juego (`startWord`) → se crea un invitado `Explorador Anónimo` (`email: null`, `username: null`, 50 neuronas, nivel 1) y la cookie `matete-guest` firmada, `httpOnly`, `sameSite: lax`.
  3. Recargar → aparece el modal "¡IDENTIFICATE!" (`needsUsername: true`).
  4. Escribir un nombre de fantasía (`Zorro Azul!` se filtra a `ZorroAzul`), que figura disponible, y confirmar → el modal se cierra y el juego sigue visible. El invitado queda con `username: 'ZorroAzul'` y `email: null`.
  5. Volver a entrar → no se pide nombre de nuevo y es el mismo jugador (misma cookie, un solo usuario).
  6. Resolver una palabra → +10 neuronas, +1 punto acreditados a ese invitado.
  7. Desde otro navegador, `ZorroAzul` figura como tomado.
  8. Con alias pero sin Google: no aparece en el ranking global y comentar, seguir o desafiar responden `login_required`. Compartir el resultado sí funciona (SOC-03).
- **Observaciones de producto (decisiones abiertas, no son fallas):**
  - El modal **no se puede cerrar**: desde la segunda carga el alias es obligatorio en la práctica, aunque el servidor deja jugar sin él.
  - En la **primera** visita no se pide alias; recién en la siguiente carga.
- **Resuelto el 2026-11-16:** el texto del modal ya no promete el ranking a invitados ("Elegí tu nombre de fantasía para jugar. Si después entrás con Google, lo conservás y aparece en el ranking"), y el alias pasa a la cuenta al entrar con Google (SYNC-09, SYNC-10).

---

## AUTH — Identidad y sesión

> Corregido en `001-hardening` (S-1): la identidad del invitado es una cookie firmada con HMAC (`matete-guest`), `httpOnly`, que solo puede resolver a usuarios sin email. Ninguna respuesta expone `User.id`.

### AUTH-01 — Un visitante sin cookie ni sesión recibe usuario nuevo
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `identity.int.test.ts`

- **Pasos:** Invocar cualquier action que cree identidad (`startWord`) sin cookie ni sesión.
- **Esperado:** Se crea un `User` con `name: 'Explorador Anónimo'`, `neuronas: 50`, `levelId: 1`, `email: null` y se emite `matete-guest`. Otro navegador recibe otro usuario.

---

### AUTH-02 — La cookie de invitado no da acceso a una cuenta registrada
**Tipo:** `int` · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** S-1 · **Tests:** `identity.int.test.ts`

- **Pasos:** Sin sesión, presentar una cookie firmada válida —o la cookie vieja `matete-guest-id`— con el `id` de una cuenta de Google.
- **Esperado:** Se rechaza y el visitante se trata como nuevo; la cuenta no recibe ningún cambio.

---

### AUTH-03 — Los rankings no exponen identificadores internos
**Tipo:** `int` · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** S-1 · **Tests:** `identity.int.test.ts`, `follows.int.test.ts`

- **Esperado:** Ningún objeto de `getGlobalRanking`, `getLevelRanking` ni `getFriendsRanking` tiene `id`. `RankingBoard` usa la posición como `key`.

---

### AUTH-04 — La cookie de invitado no es legible por JavaScript
**Tipo:** `int` + `unit` · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** S-1 · **Tests:** `identity.int.test.ts`, `guest-cookie.test.ts`

- **Esperado:** `httpOnly: true`, `sameSite: 'lax'`, `path: '/'`, y `secure: true` solo con `NODE_ENV === 'production'`.

---

### AUTH-05 — Una cookie con valor arbitrario no rompe la app
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`

- **Pasos:** Setear `matete-guest` y `matete-guest-id` (con la ventana legacy abierta) a `'no-existe'`, `''`, 10.000 caracteres y `"'; DROP TABLE ..."`. Renderizar la home y jugar.
- **Esperado:** Se trata como visitante nuevo, sin excepción.

---

### AUTH-06 — Con sesión de Google la cookie de invitado se ignora
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`

- **Esperado:** Con sesión de `a` y cookie de invitado `b`, todo opera sobre `a`.

---

### AUTH-07 — Sesión válida cuyo email ya no existe en la base
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`

- **Esperado:** Nunca resuelve a otra cuenta registrada, aunque la cookie apunte a una. Cae al camino de invitado (crea o usa un invitado sin email). Desde 001 esto es seguro porque la cookie solo resuelve invitados.

---

### AUTH-08 — La sesión expone los campos extendidos
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`, `fixes.int.test.ts`

- **Esperado:** El callback `session` agrega `points`, `neuronas` y `levelId` de la base; si no encuentra el usuario usa `0 / 50 / 1`. **No** agrega `session.user.id` (constitución, principio 2).
- **Corregido el 2026-11-16 (ciclos de prueba):** se quitó `session.user.id` del callback y del tipo `Session`.

---

### AUTH-09 — Login con Google de punta a punta
**Tipo:** `e2e` / `manual` · **Prioridad:** P1 · **Estado:** ❌

- **Pasos:** Click en "Ingresar" → OAuth de Google → volver a la home. Repetir desde el link de un desafío (vuelve al desafío).
- **Esperado:** El header muestra el nombre; si había progreso de invitado, se migra (SYNC-01).
- **Nota:** requiere actualizar `next-auth` (INFRA-01). Prueba manual a cargo del dueño (quickstart de 001 §3 y de 002 §2).

---

### AUTH-10 — Cookie alterada, vencida o firmada con otra clave
**Tipo:** `int` + `unit` · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** S-1 · **Tests:** `identity.int.test.ts`, `guest-cookie.test.ts`

- **Esperado:** Cualquier carácter cambiado, firma reutilizada con otro `id`, otra clave, token vencido (30 días) o el `id` en claro → se rechaza y se crea un visitante nuevo, sin tocar al jugador original. Sin `GUEST_COOKIE_SECRET` (o con menos de 16 caracteres) no se emiten ni aceptan cookies (falla cerrado).

---

### AUTH-11 — Transición de la cookie vieja (30 días)
**Tipo:** `int` + `unit` · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** FR-004 de 001 · **Tests:** `identity.int.test.ts`, `guest-cookie.test.ts`

- **Esperado:** Mientras `now < GUEST_LEGACY_COOKIE_UNTIL`, la cookie `matete-guest-id` de un **invitado** se acepta y en la siguiente Server Action se reemplaza por una firmada (en un Server Component no se puede escribir la cookie: no rompe el render). Vencido el plazo, o sin la variable, se rechaza.

---

### AUTH-12 — La resolución de identidad no está expuesta como Server Action
**Tipo:** `int` · **Prioridad:** **P0** · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`

- **Esperado:** `src/app/actions/game.ts` y `sync.ts` no exportan `getUser`, `ensureUser`, `getCurrentUser` ni `ensureCurrentUser` (viven en `src/lib/identity.ts`, que no es `'use server'`). Antes de 001, `getUser` era invocable desde el navegador y devolvía el usuario completo.

---

## GAME — Resolución de palabras y puntaje

> Corregido en `001-hardening` (S-2, S-3, S-4): el navegador recibe una `PublicWord` (definición, largo, pool de 14 letras y letras reveladas) y `submitGuess` valida y acredita en el servidor una sola vez.

### GAME-01 — La solución no viaja al cliente
**Tipo:** `int` + `unit` · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** S-2 · **Tests:** `guess-and-economy.int.test.ts`, `public-word.test.ts`, `GameEngine.test.tsx`

- **Esperado:** `getInitialGameState`, `getNextWord` y `moveToNextLevel` devuelven `PublicWord`, sin la solución en ningún campo. Verificado también en el HTML real de la home (001, quickstart).

---

### GAME-02 — La validación de la respuesta ocurre en el servidor
**Tipo:** `int` · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** S-2 · **Tests:** `guess-and-economy.int.test.ts`

- **Esperado:** `submitGuess(id, incorrecta)` → `{ correct: false }` sin cambios y sin revelar qué letras fallaron. Con la correcta (sin distinguir mayúsculas) → `{ correct: true, solution, ... }`.

---

### GAME-03 — Acertar la misma palabra varias veces acredita una sola vez
**Tipo:** `int` · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** S-3 · **Tests:** `guess-and-economy.int.test.ts`

- **Esperado:** 100 `submitGuess` correctos → `+10` neuronas y `+1` punto en total. Los siguientes responden `alreadySolved: true`.

---

### GAME-04 — Aciertos concurrentes no duplican el premio
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** S-3 · **Tests:** `guess-and-economy.int.test.ts`

- **Esperado:** `Promise.all` de varios aciertos de la misma palabra → una sola acreditación (`UPDATE ... WHERE guessed = false`).

---

### GAME-05 — El tiempo lo calcula el servidor
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** S-4 · **Tests:** `guess-and-economy.int.test.ts`

- **Esperado:** `timeTaken` = tiempo desde la entrega de la palabra (`Progress.startTime`), medido por Postgres. El cliente no envía tiempo.

---

### GAME-06 — Palabra inexistente, de otro nivel o salteada
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `guess-and-economy.int.test.ts`

- **Esperado:** `submitGuess` responde `{ error: 'not_in_play' }` sin efectos en la base.

---

### GAME-07 — El acierto registra el progreso completo
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`

- **Esperado:** `Progress` con `guessed: true`, `endTime` y `timeTaken` no nulos; `User.totalTime` suma ese tiempo.

---

### GAME-08 — `getInitialGameState` sin palabras disponibles
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** B-6 · **Tests:** `guess-and-economy.int.test.ts`, `catalog.int.test.ts`

- **Esperado:** Con un nivel vacío sin siguiente nivel, o con todos los niveles completos, devuelve `null` en lugar de lanzar. Lo que ve el jugador en ese caso está en PROG-09.

---

### GAME-09 — Reanuda en la primera palabra sin resolver
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`

- **Precondición:** Nivel 1 con la 1.ª acertada, la 2.ª salteada y la 3.ª con una letra revelada.
- **Esperado:** Devuelve la 3.ª, con su letra revelada.

---

### GAME-10 — Las palabras salteadas o acertadas no vuelven a aparecer
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`

- **Esperado:** 30 llamadas a `getNextWord` devuelven siempre la única palabra pendiente.

---

### GAME-11 — Progresión completa de un nivel largo
**Tipo:** `int` + `unit` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `guess-and-economy.int.test.ts`, `smoke-progression-50.test.tsx`

- **Esperado:** Un nivel de 50 palabras se completa exactamente en la 50.ª, con el estado persistido en la base.
- **Nota:** en producción los niveles tienen entre 5 y 34 palabras; el caso de 50 es un límite superior holgado, y los tests de integración usan además niveles de 2 y 3 palabras.

---

### GAME-12 — La entrega de la palabra se registra una sola vez
**Tipo:** `int` + `unit` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `guess-and-economy.int.test.ts`, `GameEngine.test.tsx`

- **Esperado:** `GameEngine` llama a `startWord` al montar; repetirlo no crea otra fila ni cambia el `startTime` original (el tiempo no se puede reiniciar recargando).

---

### GAME-13 — Formato del intento
**Tipo:** `int` + `unit` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `guess-and-economy.int.test.ts`, `public-word.test.ts`

- **Esperado:** Vacío, con caracteres fuera de `[A-Za-zÑñ]` o de largo distinto → `{ error: 'invalid_guess' }`. La ñ se acepta.

---

## HINT — Pistas y economía de neuronas

### HINT-01 — Pista de letra cobra y revela una posición
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `guess-and-economy.int.test.ts`

- **Esperado:** Descuenta el costo de `Setting['costo_pista']` (respaldo 5) y devuelve `revealed: { index, letter }` con un índice válido.

---

### HINT-02 — Máximo 2 pistas por palabra
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `guess-and-economy.int.test.ts`

- **Esperado:** La tercera se rechaza sin cobrar.

---

### HINT-03 — Sin saldo no se cobra ni se revela
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`, `guess-and-economy.int.test.ts`

- **Esperado:** Con 3 neuronas y costo 5 → `{ success: false, error: 'Not enough neuronas' }`, saldo y letras reveladas intactos. Lo mismo para el salto.

---

### HINT-04 — Pedidos concurrentes no dejan saldo negativo
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** S-5 · **Tests:** `guess-and-economy.int.test.ts`

- **Esperado:** Con saldo para una sola, dos pistas simultáneas → se cobra una y el saldo queda ≥ 0.

---

### HINT-05 — No se puede pedir pista ni saltar una palabra ya acertada
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Ref:** B-5 · **Tests:** `guess-and-economy.int.test.ts`

---

### HINT-06 — No se puede pedir pista ni saltar una palabra de otro nivel
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Ref:** B-5 · **Tests:** `catalog.int.test.ts`

---

### HINT-07 — Todas las letras ya reveladas
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ➖

- **Esperado:** `{ success: false, error: 'No hay más letras para revelar.' }` sin cobrar.
- **No aplica hoy:** el límite de 2 pistas se evalúa antes y todas las palabras tienen 4 letras o más, así que el camino es inalcanzable. Pasa a aplicar si el límite se vuelve configurable.

---

### HINT-08 — Saltar cobra 15 y marca la palabra
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `guess-and-economy.int.test.ts`

- **Esperado:** `neuronas -= 15`, `Progress.skipped = true`; saltos simultáneos cobran una sola vez.

---

### HINT-09 — El costo mostrado coincide con el cobrado
**Tipo:** `int` + `unit` · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** B-4 · **Tests:** `fixes.int.test.ts`, `GameEngine.test.tsx`, `page.test.tsx`

- **Precondición:** `Setting['costo_pista'] = '8'`, `Setting['costo_salto'] = '20'`.
- **Esperado:** `getInitialGameState` devuelve `costs: { letter: 8, skip: 20 }`; los botones dicen "LETRA (-8 N)" y "SALTAR (-20 N)", se deshabilitan por debajo de esos valores y el servidor cobra exactamente eso. Sin configuración: 5 y 15.
- **Corregido el 2026-11-16 (ciclos de prueba):** los costos viven solo en `Setting` (constitución, principio 4) y el cliente los recibe del servidor. **Decisión abierta:** los costos por nivel de `Level` (`costJump`, `costLetter`, `costHelp`) siguen sin usarse; adoptarlos cambiaría la economía (en producción valen 35/25/30).

---

### HINT-10 — Un error al saltar no deja el juego trabado
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ✅ · **Ref:** B-8 · **Tests:** `GameEngine.test.tsx`

- **Esperado:** Si `requestHint` falla, los botones se rehabilitan y aparece "No pudimos saltar la palabra"; ante `UnrecognizedActionError`, "Sincronización requerida".
- **Corregido el 2026-11-16 (ciclos de prueba):** `try/catch/finally` en `handleSkip`.

---

## PROG — Progresión de niveles

### PROG-01 — El nivel se completa al procesar la última palabra
**Tipo:** `int` + `unit` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `guess-and-economy.int.test.ts`, `smoke-progression-50.test.tsx`

---

### PROG-02 — Las salteadas cuentan para completar el nivel
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `guess-and-economy.int.test.ts`

- **Esperado:** Saltar la última palabra pendiente devuelve `isLevelComplete: true`.

---

### PROG-03 — `moveToNextLevel` avanza y entrega la primera palabra
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `guess-and-economy.int.test.ts`

---

### PROG-04 — `moveToNextLevel` en el último nivel
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`

- **Esperado:** `{ success: false, error: 'No hay más niveles.' }` y `levelId` sin cambios.

---

### PROG-05 — El modal de nivel completado muestra las estadísticas
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `GameEngine.test.tsx`, `Modals.test.tsx`

---

### PROG-06 — El botón del modal avanza de nivel
**Tipo:** `unit` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `GameEngine.test.tsx`

---

### PROG-07 — `getInitialGameState` avanza solo si el nivel está completo
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`

- **Esperado:** Con el nivel 1 completo (acertadas y salteadas) queda en `levelId: 2` y recibe la primera palabra del nivel 2.

---

### PROG-08 — No se puede saltear un nivel manipulando la llamada
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `guess-and-economy.int.test.ts`

- **Esperado:** `moveToNextLevel()` con el nivel incompleto → `{ success: false }`.

---

### PROG-09 — Terminar el último nivel muestra un final de juego
**Tipo:** `int` + `unit` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `fixes.int.test.ts`, `GameEngine.test.tsx`, `page.test.tsx`

- **Esperado:** Al completar el último nivel, `moveToNextLevel` devuelve `gameOver: true` y el juego muestra "¡COMPLETASTE MATETE!". Al recargar, la home usa `getGameCompletion()` y muestra el mismo final con los puntos, no "Cargando base de datos...". Con la base sin palabras, la home lo dice en lugar de cargar para siempre.
- **Corregido el 2026-11-16 (ciclos de prueba):** `GameOverPanel`, `getGameCompletion` y el campo `gameOver`.

---

## SYNC — Migración de invitado a cuenta de Google

> Corregido en `001-hardening` (B-1, B-2): una transacción bloquea al invitado, fusiona su progreso, lo borra, recalcula puntos y tiempo desde `Progress` y suma solo las neuronas por encima del saldo inicial.

### SYNC-01 — Migrar un invitado con progreso
**Tipo:** `int` · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** B-1 · **Tests:** `guest-migration.int.test.ts`

- **Esperado:** El progreso pasa a la cuenta, el invitado se borra, las cookies de invitado se eliminan, `points` y `totalTime` se recalculan desde `Progress` y `levelId = max(cuenta, invitado)`. Un invitado sin progreso también se borra sin error.

---

### SYNC-02 — La migración no se aplica dos veces
**Tipo:** `int` · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** B-1 · **Tests:** `guest-migration.int.test.ts`

- **Esperado:** Recargar 10 veces después de migrar no cambia los totales.

---

### SYNC-03 — Un invitado que gastó neuronas no le resta a la cuenta
**Tipo:** `int` · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** B-2 · **Tests:** `guest-migration.int.test.ts`

- **Esperado:** `neuronas += max(0, invitado − 50)`. Un invitado con 40 no resta; uno con 60 suma 10.

---

### SYNC-04 — Palabra resuelta por el invitado y por la cuenta
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `guest-migration.int.test.ts`

- **Esperado:** Queda una sola fila y cuenta una sola vez. **Política:** se conserva el registro de la cuenta (su tiempo), aunque el invitado haya sido más rápido.

---

### SYNC-05 — Sin sesión de Google no se migra
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `guest-migration.int.test.ts`

---

### SYNC-06 — Sin cookie de invitado no se hace nada
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `guest-migration.int.test.ts`

---

### SYNC-07 — Cookie que apunta a un invitado ya borrado
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`

- **Esperado:** `{ success: true, migrated: false }`, la cookie se elimina y la cuenta no cambia.

---

### SYNC-08 — La migración es atómica
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** B-1 · **Tests:** `guest-migration.int.test.ts`, `fixes.int.test.ts`

- **Esperado:** Un fallo a mitad de camino no aplica nada; migraciones concurrentes (dos pestañas) ocurren una sola vez.
- **Cubierto:** concurrencia (`SELECT ... FOR UPDATE`) y fallo inyectado con un trigger de Postgres que aborta el borrado del invitado: la cuenta queda sin progreso ni puntos, el invitado intacto y la cookie sigue para reintentar.

---

### SYNC-09 — El nombre de fantasía del invitado se conserva al entrar con Google
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `fixes.int.test.ts`

- **Precondición:** Invitado con `username: 'ZorroAzul'` (INI-01); cuenta de Google sin `username`.
- **Esperado:** Tras la migración, la cuenta queda con `username: 'ZorroAzul'`. Si la cuenta ya tenía alias, se conserva el de la cuenta.
- **Corregido el 2026-11-16 (ciclos de prueba):** `migrateGuestProgress` copia el alias después de borrar al invitado (dentro de la misma transacción).

---

### SYNC-10 — Después de migrar no se vuelve a pedir el nombre
**Tipo:** `int` + `unit` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `fixes.int.test.ts`, `AuthSync.test.tsx`, `Modals.test.tsx`

- **Contexto:** al volver del login, la home se renderiza **antes** de que `AuthSync` migre, con `needsUsername: true`. Con SYNC-09 y USER-02 juntos, el modal quedaba abierto y cualquier nombre nuevo era rechazado: el modal no se podía cerrar (detectado en el ciclo 3).
- **Esperado:** tras migrar, `AuthSync` actualiza la sesión y pide de nuevo la página (`router.refresh`), y el juego se reinicia con el estado nuevo. Si igual llega un `updateUsername` tardío, responde `alreadySet: true` y el modal se cierra.

---

## RANK — Rankings

### RANK-01 — El ranking global excluye invitados
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `identity.int.test.ts`, `onboarding.int.test.ts`

- **Esperado:** Solo usuarios con `email` (Google), aunque un invitado tenga alias y más puntos.

---

### RANK-02 — El ranking global ordena por puntos, tiempo y neuronas
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`

- **Esperado:** `points desc`, luego `totalTime asc`, luego `neuronas desc`.

---

### RANK-03 — El ranking por nivel devuelve el top real
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** B-7 · **Tests:** `fixes.int.test.ts`

- **Precondición:** 25 usuarios con progreso en el nivel 1; `campeon` tiene más palabras que el resto y se crea último.
- **Esperado:** `getLevelRanking(1, 10)` devuelve 10 filas con `campeon` primero y sin `id`.
- **Corregido el 2026-11-16 (ciclos de prueba):** una consulta SQL agrupa, ordena (`levelPoints desc, levelTime asc`) y limita en la base; el límite se acota a 1..50.

---

### RANK-04 — El ranking por nivel cuenta solo aciertos
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`

---

### RANK-05 — Ranking vacío
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `RankingBoard.test.tsx`

- **Esperado:** "NO HAY DATOS EN ESTE SECTOR", sin errores.

---

### RANK-06 — El ranking no se rompe con datos incompletos
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `RankingBoard.test.tsx`

- **Esperado:** Con `username`, `name` e `image` nulos muestra "Anónimo" y el ícono por defecto.

---

### RANK-07 — Una fila sin tiempo no muestra NaN
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `RankingBoard.test.tsx`

- **Esperado:** Si a una fila le falta `totalTime`/`levelTime`, se muestra `0:00`. Detectado al tipar `RankingBoard` en el ciclo 2.

---

## USER — Alias

### USER-01 — El servidor valida el formato del alias
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** S-6 · **Tests:** `fixes.int.test.ts`

- **Pasos:** Invocar `updateUsername()` directamente con `'ab'`, `'a'.repeat(500)`, `'<script>alert(1)</script>'`, `'nombre con espacios'`, `''`, `'admin '`.
- **Esperado:** Todos rechazados; solo `[a-zA-Z0-9_]{3,15}` (`src/lib/username.ts`). `checkUsername` tampoco da por disponible un formato inválido.

---

### USER-02 — El alias no se puede cambiar una vez establecido
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** S-6 · **Tests:** `fixes.int.test.ts`

- **Esperado:** Con alias ya definido, `updateUsername('Otro')` → `{ success: false, alreadySet: true }`. Se implementa con un `UPDATE ... WHERE username IS NULL`, así que dos pestañas no pueden pisarse.

---

### USER-03 — Alias duplicado devuelve un error específico
**Tipo:** `int` + `unit` · **Prioridad:** P2 · **Estado:** ✅ · **Ref:** S-6 · **Tests:** `fixes.int.test.ts`, `Modals.test.tsx`

- **Esperado:** `P2002` → "Ese nombre ya está en uso.", distinto de un fallo de base; el modal muestra ese texto.

---

### USER-04 — Dos jugadores piden el mismo alias a la vez
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`

- **Esperado:** Uno lo obtiene; el unique de la base impide el duplicado aunque `checkUsername` haya dicho "disponible" a ambos. (El mensaje del perdedor es USER-03.)

---

### USER-05 — `needsUsername` refleja si falta el alias
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `catalog.int.test.ts`, `onboarding.int.test.ts`

- **Esperado:** `true` para invitados y registrados sin alias (salvo en la primera visita, cuando todavía no existe el invitado); `false` con alias.

---

### USER-06 — El modal de alias envía y cierra
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `Modals.test.tsx`, `GameEngine.test.tsx`

---

### USER-07 — `checkUsername` con límite de uso
**Tipo:** `int` + `unit` · **Prioridad:** P2 · **Estado:** ✅ · **Ref:** S-7 · **Tests:** `fixes.int.test.ts`, `Modals.test.tsx`

- **Esperado:** Sin identidad (sin cookie ni sesión) responde `error: 'no_identity'`. Con identidad, 30 consultas por minuto por jugador; después `error: 'rate_limited'` y el modal muestra "Demasiadas consultas. Esperá un minuto."
- **Límite conocido:** el contador vive en memoria del proceso; con varios procesos el tope es por proceso.

---

## DATA — Schema, migraciones y datos

### DATA-01 — Las migraciones reconstruyen el schema
**Tipo:** `manual` · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** B-3

- **Comando:** `prisma migrate diff --from-migrations prisma/migrations --to-schema prisma/schema.prisma --script` (con `SHADOW_DATABASE_URL` local).
- **Resultado 2026-11-15:** `-- This is an empty migration.` También contra producción en solo lectura (001, research R6).
- **Pendiente:** correrlo en CI en cada PR (INFRA-04).

---

### DATA-02 — La app arranca sobre una base recién migrada
**Tipo:** `int` + `manual` · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** B-3

- **Esperado:** Base vacía → `migrate deploy` → carga de niveles y seed → la home funciona. Verificado a mano (37 niveles, 486 palabras; 001 quickstart §1) y en cada corrida de `npm run test:integration`, que aplica las migraciones antes de los tests.

---

### DATA-03 — Borrar un usuario con progreso
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** B-1 · **Tests:** `catalog.int.test.ts`

- **Decisión (001):** `Progress → User` sigue en `RESTRICT`, sin cascada: borrar un usuario exige borrar antes su progreso de forma explícita, lo que evita perder progreso por accidente. La migración de invitados lo hace dentro de su transacción (SYNC-01). Las tablas sociales sí borran en cascada.
- **Esperado:** Borrar directo falla; borrando antes el progreso, funciona.

---

### DATA-04 — Saldo inicial de neuronas consistente
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Ref:** B-3 · **Tests:** `catalog.int.test.ts`

- **Esperado:** 50 en el schema, en el default de la base (migración `sync_schema`) y en el respaldo de `auth.ts` (AUTH-08).

---

### DATA-05 — El seed es idempotente
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Tests:** `fixes.int.test.ts`

- **Pasos:** Ejecutar `seed()` de `scripts/seed-db.ts` dos veces sobre la base `_test`.
- **Esperado:** Mismos conteos de `GameMessage`; no pisa un `costo_pista` ya configurado; crea `costo_salto = 15` si falta. Todo en una transacción.
- **Nota:** el script ya no carga `dotenv/config` (el `.env` local apunta a producción): se corre con `npx dotenv -e .env.test -- npx tsx scripts/seed-db.ts`.

---

### DATA-06 — Las listas de mensajes no divergen
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ✅ · **Ref:** C-7 · **Tests:** `fixes.int.test.ts`

- **Esperado:** El seed carga exactamente los mensajes de `src/lib/messages.ts`, que ahora importa en lugar de copiarlos.

---

### DATA-07 — Reparación del puntaje inflado
**Tipo:** `int` · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** S-3 · **Tests:** `recalculate-points.int.test.ts`

- **Esperado:** `scripts/recalculate-points.ts` en modo informe no escribe nada; con `--apply` crea `User_backup_20261115` y deja `points = aciertos` y `totalTime = suma de tiempos`, sin tocar neuronas (decisión de 001). No se aplica dos veces.
- **Pendiente:** ejecutarlo en producción, con confirmación del dueño (001 quickstart §5).

---

## UI — Interacción del cliente

| ID | Caso | Estado | Test |
|---|---|---|---|
| UI-01 | Renderiza la definición y el nivel inicial | ✅ | `GameEngine.test.tsx` |
| UI-02 | Escribir con el teclado llena las casillas | ✅ | `GameEngine.test.tsx` |
| UI-03 | Acierto muestra estado de éxito y "SIGUIENTE" | ✅ | `GameEngine.test.tsx` |
| UI-04 | "SIGUIENTE" pide una palabra nueva | ✅ | `GameEngine.test.tsx` |
| UI-05 | Backspace borra la última letra escrita | ✅ | `GameEngine.test.tsx` |
| UI-06 | Backspace con la palabra llena borra la última letra | ✅ | `GameEngine.test.tsx` |
| UI-07 | "Limpiar" vuelve el cursor a la primera posición editable | ✅ | `GameEngine.test.tsx` |
| UI-08 | "Limpiar" borra todas las letras no reveladas | ✅ | `GameEngine.test.tsx` |
| UI-09 | Comprar letra descuenta neuronas y muestra la letra | ✅ | `GameEngine.test.tsx` |
| UI-10 | Saltar descuenta neuronas en pantalla | ✅ | `GameEngine.test.tsx` |
| UI-11 | Enter tras un acierto pasa a la palabra siguiente | ✅ | `GameEngine.test.tsx` |
| UI-12 | Alterna entre modo pool y modo teclado | ✅ | `GameEngine.test.tsx` |
| UI-13 | El botón de sonido activa y desactiva | ✅ | `GameEngine.test.tsx` |
| UI-14 | El modal de instrucciones abre y cierra | ✅ | `Modals.test.tsx` |
| UI-15 | Las letras reveladas no se editan (cursor, backspace, click, limpiar) | ✅ | `GameEngine.test.tsx` |
| UI-16 | La detección de móvil no rompe el render en servidor | ✅ | `GameEngine.test.tsx` |
| UI-17 | Un intento incorrecto muestra el mensaje de error y no avanza | ✅ | `GameEngine.test.tsx` |
| UI-18 | El pool de 14 letras lo envía el servidor | ✅ | `GameEngine.test.tsx` |
| UI-19 | Backspace repetido con el teclado físico | ✅ | `GameEngine.test.tsx` |
| UI-21 | Las neuronas de una sesión ya cargada al montar se muestran | ✅ | `GameEngine.test.tsx` |
| UI-22 | El modal de instrucciones respeta `isOpen` | ✅ | `Modals.test.tsx` |

### UI-16 — La detección de móvil no rompe el render en servidor
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ✅ · **Ref:** B-9

- **Esperado:** `renderToString(<GameEngine />)` sin `navigator` global no lanza.
- **Corregido el 2026-11-16 (ciclos de prueba):** `useSyncExternalStore` con valor de servidor `false`. La primera corrección (ciclo 1, `setState` dentro de un efecto) la marcó el lint en el ciclo 2 y se rehízo.

### UI-19 — Backspace repetido con el teclado físico
**Tipo:** `unit` · **Prioridad:** P1 · **Estado:** ✅

- **Pasos:** Con el teclado físico, escribir `PLA` y apretar Backspace dos veces; después escribir `X`.
- **Esperado:** Queda `P`, y la `X` va a la segunda casilla.
- **Corregido el 2026-11-16 (ciclos de prueba):** el handler de `window` calcula el próximo estado desde una referencia y aplica `setUserInput` y `setCursorPos` por separado (antes había un `setState` dentro del updater de otro). Los handlers de Enter también se leen por referencia, para usar siempre la versión vigente. Conviene confirmarlo en un navegador.

### UI-21 — Las neuronas de una sesión ya cargada al montar se muestran
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ✅

- **Esperado:** Si al montar `useSession` ya trae `neuronas: 77`, el contador muestra 77 aunque el servidor haya pasado 50. Regresión introducida y corregida dentro de los ciclos (ciclo 2 → ciclo 3).

### UI-22 — El modal de instrucciones respeta `isOpen`
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ✅

- **Esperado:** Con `isOpen={false}` no renderiza nada (antes la prop se ignoraba).

---

## INFRA — Dependencias, build y repositorio

### INFRA-01 — Sin vulnerabilidades altas o críticas
**Tipo:** CI · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** S-9

- **Comando:** `npm audit --audit-level=high` → exit 0.
- **2026-11-16:** `npm audit fix` (sin `--force`) llevó `next` a 16.4.6 y `next-auth` a 4.24.15. Quedan 7 moderadas, todas en herramientas de lint (`minimatch` dentro de `eslint`/`typescript-eslint`), que no llegan a producción.

---

### INFRA-02 — El typecheck pasa limpio
**Tipo:** `manual` · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** C-2

- **Comando:** `npx tsc --noEmit` → exit 0 (2026-11-15). Pendiente: correrlo en CI.

---

### INFRA-03 — El lint corre
**Tipo:** CI · **Prioridad:** P1 · **Estado:** ✅ · **Ref:** C-1

- **Comando:** `npm run lint` (ahora `eslint .` con `eslint.config.mjs` y `eslint-config-next`) → 0 errores, 0 warnings.
- **2026-11-16:** al habilitarlo aparecieron 36 errores (hooks, `setState` en efectos, refs en el render, `any`) que se corrigieron en el ciclo 2, y 28 warnings que se limpiaron en el ciclo 3.

---

### INFRA-04 — El build de producción funciona
**Tipo:** `manual` · **Prioridad:** P1 · **Estado:** ✅

- **Comando:** `npm run build` (`prisma generate` + `next build`) → exit 0 el 2026-11-15, con `/`, `/desafio/[code]`, `/desafios` y `/admin/comentarios` dinámicas. Pendiente: CI que corra build, typecheck, tests y DATA-01 en cada PR.

---

### INFRA-05 — No hay secretos versionados
**Tipo:** `unit` · **Prioridad:** **P0** · **Estado:** ✅ · **Ref:** S-8 · **Tests:** `src/test/infra.test.ts`

- **Verificar:** `git ls-files` no devuelve `.env*`, `*.keystore`, `*.jks`, `*.db` ni `*.sqlite`.
- **2026-11-16:** se sacaron del índice los 2 keystores, `prisma/dev.db`, los `.sqlite` y los `Thumbs.db` (siguen en disco) y se agregaron a `.gitignore`. El cambio se aplica al hacer commit.
- **A cargo del dueño:** los keystores **ya se publicaron** y siguen en el historial del repo público. Hay que considerarlos comprometidos: rotarlos y, si se decide, purgar el historial (requiere force-push).

---

### INFRA-06 — La documentación interna de infraestructura no se publica
**Tipo:** `unit` · **Prioridad:** **P0** · **Estado:** ✅ · **Tests:** `src/test/infra.test.ts`

- **Verificar:** `CODE_REVIEW.md` no está en `git ls-files` y `git check-ignore` lo reconoce.
- **2026-11-16:** agregado a `.gitignore`.

---

## SOC — Funciones sociales (feature 002-social)

Spec: [specs/002-social/spec.md](../specs/002-social/spec.md). Todos ✅.

| ID | Caso | Tipo | Ref | Test |
|---|---|---|---|---|
| SOC-01 | El texto compartido tiene nivel, casilleros, tiempo, pistas y link, sin la palabra | `unit` | FR-002 | `src/lib/share.test.ts` |
| SOC-02 | `submitGuess` devuelve el tiempo y las letras reveladas que registró el servidor | `int` | FR-003 | `share-result.int.test.ts` |
| SOC-03 | El panel social aparece solo tras acertar (también para invitados); Enter dentro del panel no pasa de palabra | `unit` | FR-001, FR-004 | `GameEngine.test.tsx` |
| SOC-04 | Compartir: menú nativo → portapapeles; cancelar no copia | `unit` | FR-001 | `ShareButton.test.tsx` |
| SOC-10 | Normalización del comentario (vacío, 280, saltos, control, HTML literal) | `unit` | FR-007 | `src/lib/comment-text.test.ts` |
| SOC-11 | Comentarios bloqueados (ni la cantidad) sin acierto, con salteo u otra palabra; paginación | `int` | FR-005, FR-009 | `comments.int.test.ts` |
| SOC-12 | Publicar exige Google + alias + acierto; cuerpo inválido rechazado | `int` | FR-006, FR-007 | `comments.int.test.ts` |
| SOC-13 | Límite: simultáneos → uno; 30 s; 20 por día | `int` | FR-008 | `comments.int.test.ts` |
| SOC-14 | Reporte único por jugador; 3 distintos (simultáneos) ocultan | `int` | FR-010 | `comments.int.test.ts` |
| SOC-15 | Restaurar exige 3 reportes nuevos | `int` | FR-013 | `comments.int.test.ts` |
| SOC-16 | Borrado por el autor; moderación solo para admin | `int` | FR-011, FR-012 | `comments.int.test.ts` |
| SOC-17 | UI de comentarios: invitación, publicar, errores, HTML como texto, reportar/borrar, "ver más" | `unit` | FR-009 | `CommentsPanel.test.tsx` |
| SOC-25 | Al cambiar de palabra se descarta la respuesta de comentarios de la palabra anterior | `unit` | FR-005 | `CommentsPanel.test.tsx` |
| SOC-20 | Seguir idempotente; rechazos (self, inexistente, invitado, sin alias); tope 500 concurrente | `int` | FR-014, FR-015 | `follows.int.test.ts` |
| SOC-21 | Búsqueda por prefijo con `%`/`_` literales | `int` | FR-016 | `follows.int.test.ts` |
| SOC-22 | Ranking de amigos; `isMe`/`isFollowed` en rankings, sin `id` | `int` | FR-017..FR-019 | `follows.int.test.ts` |
| SOC-23 | UI del ranking: Seguir solo con sesión y en otros con alias; pestaña Amigos (invitación o lista con fila propia) | `unit` | FR-017, FR-018 | `RankingBoard.test.tsx` |
| SOC-30 | Código de desafío aleatorio y validación de formato | `unit` | FR-021 | `src/lib/challenge-code.test.ts` |
| SOC-31 | Crear desafío: idempotente (también concurrente), solo palabra acertada, tope 20/día | `int` | FR-020, FR-021 | `challenges.int.test.ts` |
| SOC-32 | Página pública sin definición, solución ni ids; estados del visitante | `int` | FR-022, FR-023 | `challenges.int.test.ts` |
| SOC-33 | Tiempo de servidor desde la primera apertura; un intento; creador no juega; rendirse; orden | `int` | FR-024..FR-029 | `challenges.int.test.ts` |
| SOC-34 | "Ya la conocía" no gana | `int` | FR-027 | `challenges.int.test.ts` |
| SOC-35 | Jugar un desafío no cambia neuronas, puntos, tiempo, nivel ni progreso | `int` | FR-028 | `challenges.int.test.ts` |
| SOC-36 | Mis desafíos (creados y jugados), sin ids | `int` | FR-030 | `challenges.int.test.ts` |
| SOC-37 | UI del desafío: login con regreso, creador, pool + teclado, error, rendirse, terminado | `unit` | FR-023..FR-025 | `ChallengePlay.test.tsx` |
| SOC-38 | "Mis desafíos" permite crear un desafío desde las últimas palabras acertadas (solo las propias, sin ids; exige Google + alias) | `int` + `unit` | FR-020 | `challenges.int.test.ts`, `ChallengeWordList.test.tsx` |

Verificación por mutación (2026-11-15): quitar el chequeo de acierto en `getWordComments` o la condición `"endTime" IS NULL` al cerrar un intento hace fallar SOC-11 y SOC-33. En 001, quitar `guessed = false` del acreditado hace fallar GAME-03/GAME-04.

---

## Anexo A — Infraestructura de tests

Implementada en 001 (sin Docker, con el Postgres local de Homebrew):

- `vitest.integration.config.ts`: entorno `node`, archivos `src/test/integration/**/*.int.test.ts`, sin paralelismo entre archivos.
- `src/test/integration/global-setup.ts`: **aborta si la base no termina en `_test`** y aplica `prisma migrate deploy`.
- `src/test/integration/setup.ts`: simula `next/headers` (cookies) y `next-auth` (sesión); Prisma es real.
- `src/test/integration/harness.ts`: cookie jar, `loginAs`, `as(email, fn)`, `newBrowser`, `resetDb` (TRUNCATE + 2 niveles y 5 palabras fijas), `createRegisteredUser`, `createGuestUser`, `createSocialUser`, `solveWord`.
- `.env.test` (no versionado; ver `.env.test.example`): `DATABASE_URL` y `SHADOW_DATABASE_URL` locales. **El `.env` local apunta a producción**: todo comando contra una base va con `npx dotenv -e .env.test -- …`.

---

## Anexo B — Pendientes (a cargo del dueño)

Los 12 defectos de la v2.0 quedaron corregidos el 2026-11-16. Lo que sigue no se puede resolver desde el código:

| Paso | Casos | Acción |
|---|---|---|
| 1 | `INFRA-05` | Rotar los keystores de Android publicados; decidir si se purga el historial del repo |
| 2 | `AUTH-09`, `UI-19` | Prueba manual en navegador: login con Google (con y sin progreso de invitado, verificando SYNC-09/10) y Backspace con teclado físico |
| 3 | `HINT-09` | Decidir si se usan los costos por nivel de `Level` |
| 4 | `INI-01` | Decidir si el modal de alias se puede cerrar y si se pide en la primera visita |
| 5 | `INFRA-02`, `INFRA-04`, `DATA-01` | CI que corra lint, typecheck, tests, build y el chequeo de migraciones en cada PR |
| 6 | `DATA-07` | Producción: `pg_dump`, variables de entorno, `migrate resolve`/`deploy`, deploy y recálculo de puntos (ver quickstarts de 001 y 002) |

---

## Anexo C — Verificado contra producción (2026-08-06, histórico)

Consultas de solo lectura de la v1.0. Los defectos que confirmaron ya están corregidos en el código (001); los datos dañados se reparan con DATA-07 al desplegar.

| Caso | Confirmación de agosto | Hoy |
|---|---|---|
| `DATA-01` | Drift total: producción armada con `db push` | Baseline `sync_schema`; diff contra producción vacío (2026-11-15). Falta registrarlo en producción |
| `DATA-03` / `SYNC-01` | 35 invitados con progreso rompían la migración | Corregido (SYNC-01) |
| `GAME-03` / `SYNC-02` | 364 puntos fantasma sobre 949 (38 %) | Código corregido; datos pendientes de DATA-07 en producción |
| `HINT-09` | Costos de `Level` configurados (35/25/30) e ignorados | Costo mostrado = cobrado (2026-11-16); usar los de `Level` queda como decisión |
| `HINT-04` | Sin saldos negativos (defecto latente) | Corregido |
| `GAME-05` | Sin `timeTaken` en 0 (defecto latente) | Corregido |
| `USER-01` | Los 22 alias cumplían el formato | Validación en servidor (2026-11-16) |
| `GAME-08` / `PROG-04` | No alcanzables (nadie supera el nivel 37) | Servidor corregido; UI en PROG-09 |
| `RANK-01` | Solo 3 de 49 usuarios tenían email; 22 invitados eligieron alias | Regla vigente; ver la observación de INI-01 sobre el texto del modal |
