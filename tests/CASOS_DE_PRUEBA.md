# Casos de prueba — Matete Web

**Versión:** 1.0 · **Fecha:** 2026-08-06 · **Base:** commit `52f9c16`
**Documento hermano:** [CODE_REVIEW.md](../CODE_REVIEW.md)

Catálogo de casos de prueba para guiar la escritura de tests automáticos. Cada caso tiene un ID estable que debe citarse en el nombre del test, para que el mapeo sobreviva a los refactors:

```ts
it('[GAME-03] no acredita puntaje al re-enviar una palabra ya resuelta', async () => { ... })
```

---

## Cómo leer este documento

### Estado

| Símbolo | Significado |
|---|---|
| ✅ | Cubierto por un test existente que pasa |
| ⚠️ | Cubierto parcialmente, o cubierto con mocks que no validan lo que importa |
| ❌ | Sin cobertura |
| 🔴 | **Sin cobertura y el comportamiento esperado falla hoy.** Escribir el test primero: debe fallar, y pasar recién cuando se corrija el defecto |

### Tipo

| Tipo | Entorno | Corre con |
|---|---|---|
| `unit` | Vitest + jsdom, dependencias mockeadas | `npm test` |
| `int` | Vitest + **Postgres real** (ver Anexo A) | `npm run test:int` (a crear) |
| `e2e` | Navegador real contra la app levantada | Playwright (a incorporar) |
| `manual` | Verificación humana o de línea de comandos | — |

### Prioridad

- **P0** — Bloquea el despliegue público. Datos incorrectos, pérdida de progreso o acceso indebido.
- **P1** — Defecto funcional visible para el jugador.
- **P2** — Robustez, borde, calidad.

---

## Resumen de cobertura

| Área | Casos | ✅ | ⚠️ | ❌ | 🔴 |
|---|---|---|---|---|---|
| AUTH — Identidad y sesión | 9 | 0 | 1 | 4 | 4 |
| GAME — Resolución y puntaje | 11 | 1 | 2 | 4 | 4 |
| HINT — Pistas y economía | 10 | 3 | 1 | 4 | 2 |
| PROG — Progresión de niveles | 8 | 3 | 1 | 3 | 1 |
| SYNC — Migración invitado→cuenta | 8 | 2 | 1 | 2 | 3 |
| RANK — Rankings | 6 | 1 | 0 | 3 | 2 |
| USER — Alias | 7 | 1 | 0 | 3 | 3 |
| DATA — Schema, migraciones, seed | 6 | 0 | 0 | 3 | 3 |
| UI — Interacción del cliente | 16 | 14 | 0 | 2 | 0 |
| INFRA — Dependencias y build | 5 | 0 | 0 | 3 | 2 |
| **Total** | **86** | **25** | **6** | **31** | **24** |

Los 36 tests que existen hoy se concentran en UI (14 de 16 casos cubiertos). La brecha está en todo lo que toca la base de datos: las Server Actions se testean con Prisma mockeado, lo que verifica que el código llama a Prisma como el autor espera, no que la base acepte esas operaciones.

---

## AUTH — Identidad y sesión

> Los casos AUTH-02 a AUTH-05 son la prioridad más alta del catálogo. Hoy la identidad del invitado se deriva de un `id` de usuario sin firmar que además se publica en el ranking.

### AUTH-01 — Un visitante sin cookie ni sesión recibe usuario nuevo
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ❌

- **Precondición:** Sin cookie `matete-guest-id`, sin sesión de NextAuth.
- **Pasos:** Invocar `ensureUser()`.
- **Esperado:** Se crea un `User` con `name: 'Explorador Anónimo'`, `neuronas: 50`, `levelId: 1`, `email: null`. Se emite la cookie de invitado.

---

### AUTH-02 — La cookie de invitado **no** debe dar acceso a una cuenta autenticada 🔴
**Tipo:** `int` · **Prioridad:** **P0** · **Ref:** S-1

- **Precondición:** Existe un usuario `victima` con `email: 'victima@example.com'` (cuenta de Google) e `id` conocido.
- **Pasos:**
  1. Sin sesión de NextAuth, setear la cookie `matete-guest-id` con el `id` de `victima`.
  2. Invocar `getUser()`.
- **Esperado:** Devuelve `null`. Una cookie de invitado nunca debe resolver a una cuenta con `email`.
- **Comportamiento actual:** Devuelve el usuario `victima` completo. A partir de ahí `useHint`, `updateUsername` y `moveToNextLevel` operan sobre esa cuenta.

---

### AUTH-03 — Los rankings no exponen identificadores internos 🔴
**Tipo:** `int` · **Prioridad:** **P0** · **Ref:** S-1

- **Pasos:** Invocar `getGlobalRanking(10)` y `getLevelRanking(1, 10)`.
- **Esperado:** Ningún objeto del resultado contiene la propiedad `id`.
- **Comportamiento actual:** `getGlobalRanking` hace `select: { id: true }` y el `id` viaja al navegador, que es lo que hace explotable a AUTH-02.
- **Nota de implementación:** al quitar `id`, `RankingBoard` necesita otra `key` de React (el índice sirve; la lista es de solo lectura y de tamaño fijo).

---

### AUTH-04 — La cookie de invitado no debe ser legible por JavaScript 🔴
**Tipo:** `int` · **Prioridad:** P1 · **Ref:** S-1

- **Pasos:** Invocar `ensureUser()` sobre un visitante nuevo e inspeccionar las opciones con las que se setea la cookie.
- **Esperado:** `httpOnly: true`, `sameSite: 'lax'`, y `secure: true` cuando `NODE_ENV === 'production'`.
- **Comportamiento actual:** Solo se pasan `maxAge` y `path`.

---

### AUTH-05 — Una cookie de invitado con un `id` inexistente no rompe la app
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ❌

- **Pasos:** Setear `matete-guest-id` con un valor arbitrario (`'no-existe'`, string vacío, 10.000 caracteres, `"'; DROP TABLE ..."`), cargar la home.
- **Esperado:** La página renderiza normalmente tratando al visitante como nuevo. Sin excepción no capturada.

---

### AUTH-06 — Con sesión autenticada, la cookie de invitado se ignora
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ❌

- **Precondición:** Sesión válida de `usuario-a`; cookie `matete-guest-id` apuntando a `usuario-b`.
- **Pasos:** Invocar `getUser()`.
- **Esperado:** Devuelve `usuario-a`. La sesión siempre gana.

---

### AUTH-07 — Sesión válida cuyo email ya no existe en la base
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ❌

- **Precondición:** Sesión con `email` que no corresponde a ningún `User` (cuenta borrada).
- **Esperado:** No cae al camino de la cookie de invitado. Devuelve `null` o fuerza re-login.
- **Nota:** Hoy `getUser()` cae al bloque de la cookie, encadenando con AUTH-02.

---

### AUTH-08 — La sesión expone los campos extendidos
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ⚠️

- **Pasos:** Ejecutar el callback `session` de `authOptions` con un usuario conocido.
- **Esperado:** `session.user` incluye `id`, `points`, `neuronas` y `levelId` leídos de la base.
- **Nota:** Verificar también el fallback cuando `dbUser` es `null`. Hoy usa `neuronas: 150`, que no coincide ni con el schema (50) ni con la migración (150) — ver DATA-04.

---

### AUTH-09 — Login con Google end-to-end
**Tipo:** `e2e` · **Prioridad:** P1 · **Estado:** ❌

- **Pasos:** Click en "Ingresar" → completar OAuth de Google → volver a la home.
- **Esperado:** El header muestra el nombre del jugador; `getUser()` resuelve por email.
- **Nota:** Requiere `next-auth >= 4.24.15` por [GHSA-x445-f3h2-j279](https://github.com/advisories/GHSA-x445-f3h2-j279) (cookies de `state`/`nonce`/PKCE no ligadas al provider). Ver INFRA-01.

---

## GAME — Resolución de palabras y puntaje

### GAME-01 — La palabra correcta no viaja al cliente 🔴
**Tipo:** `int` · **Prioridad:** **P0** · **Ref:** S-2

- **Pasos:** Invocar `getInitialGameState()` y `getRandomWordFromLevel(1)`.
- **Esperado:** El objeto devuelto expone `id`, `description` y `length`, pero **no** `word`.
- **Comportamiento actual:** Se devuelve la fila `Word` completa y `page.tsx` la serializa en el HTML como `initialWord`.
- **Verificación complementaria (`e2e`):** cargar la home y comprobar que la solución no aparece en el HTML inicial ni en la respuesta de la Server Action.

---

### GAME-02 — La validación de la respuesta ocurre en el servidor 🔴
**Tipo:** `int` · **Prioridad:** **P0** · **Ref:** S-2

- **Precondición:** Existe una action `submitGuess(wordId, guess)` (a implementar).
- **Pasos:** Invocar `submitGuess(1, 'RESPUESTA_INCORRECTA')`.
- **Esperado:** `{ correct: false }`, sin acreditar neuronas ni puntos.
- **Y:** `submitGuess(1, '<respuesta correcta>')` → `{ correct: true }` y acredita **una sola vez**.

---

### GAME-03 — Resolver la misma palabra dos veces acredita una sola vez 🔴
**Tipo:** `int` · **Prioridad:** **P0** · **Ref:** S-3

- **Precondición:** `usuario` con `neuronas: 50`, `points: 0`. Palabra `1` sin resolver.
- **Pasos:** Invocar `recordWordSolved(1, 30)` **dos veces** seguidas.
- **Esperado:** Al final, `neuronas === 60` y `points === 1`.
- **Comportamiento actual:** `neuronas === 70`, `points === 2`. El `progress.upsert` es idempotente pero el `user.update` con `increment` corre siempre.
- **Variante P0:** repetir 100 veces en un bucle → sigue siendo `+10` y `+1`.

---

### GAME-04 — Llamadas concurrentes a `recordWordSolved` no duplican el premio
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ❌ · **Ref:** S-3

- **Pasos:** `await Promise.all([recordWordSolved(1, 30), recordWordSolved(1, 30)])`.
- **Esperado:** Una sola acreditación. Requiere que la corrección de GAME-03 use `$transaction`, no un check-then-act.

---

### GAME-05 — `timeTaken` lo calcula el servidor 🔴
**Tipo:** `int` · **Prioridad:** P1 · **Ref:** S-4

- **Pasos:** Entregar una palabra, esperar ~2 s, resolverla enviando `timeTaken: 0` desde el cliente.
- **Esperado:** El `Progress` persistido tiene `timeTaken >= 2`, derivado de `startTime`. El valor del cliente se ignora.
- **Comportamiento actual:** Se persiste el `0`, lo que gana todos los desempates del ranking global (`orderBy: totalTime asc`).

---

### GAME-06 — `recordWordSolved` con un `wordId` inexistente
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ❌

- **Pasos:** `recordWordSolved(999999, 10)`.
- **Esperado:** `{ success: false, error: 'Word not found' }`, sin efectos en la base.

---

### GAME-07 — `recordWordSolved` acredita y registra el progreso
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ⚠️

- **Esperado:** `+10` neuronas, `+1` punto, `+timeTaken` en `totalTime`, y una fila `Progress` con `guessed: true` y `endTime` no nulo.
- **Nota:** cubierto por `game.server.test.ts` con Prisma mockeado — verifica la intención, no el efecto. Reescribir contra base real.

---

### GAME-08 — `getInitialGameState` con un nivel sin palabras 🔴
**Tipo:** `int` · **Prioridad:** P1 · **Ref:** B-6

- **Precondición:** `usuario.levelId = 99`; el nivel 99 existe pero no tiene palabras.
- **Pasos:** Invocar `getInitialGameState()`.
- **Esperado:** Devuelve `null` o un estado vacío manejable. La home renderiza el fallback "Cargando base de datos...".
- **Comportamiento actual:** `words[words.length - 1]` es `undefined` y `lastWord.id` lanza `TypeError`, rompiendo el render del Server Component: la página entera devuelve error.

---

### GAME-09 — `getInitialGameState` reanuda en la primera palabra sin resolver
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ❌

- **Precondición:** Nivel 1 con 5 palabras; las palabras 1 y 2 con `guessed: true`, la 3 con `skipped: true`.
- **Esperado:** Devuelve la palabra 4, con `revealedIndices` correspondiente a su `Progress` si lo tuviera.

---

### GAME-10 — Las palabras saltadas no vuelven a aparecer
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ❌

- **Precondición:** Palabra 3 con `skipped: true`.
- **Pasos:** Invocar `getRandomWordFromLevel(1)` 50 veces.
- **Esperado:** La palabra 3 no sale nunca.

---

### GAME-11 — Progresión completa de 50 palabras
**Tipo:** `unit` · **Prioridad:** P1 · **Estado:** ✅

- Cubierto por `smoke-progression-50.test.tsx` y `actions-progression.test.ts`.
- **Pendiente:** replicarlo como `int` contra base real, para que valide también el estado persistido y no solo las llamadas mockeadas.

---

## HINT — Pistas y economía de neuronas

### HINT-01 — Pista de letra descuenta y revela una posición
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ⚠️

- **Esperado:** `neuronas -= costo`, `Progress.revealedIndices` gana un índice válido dentro del rango de la palabra.
- **Nota:** cubierto con mocks en `game.server.test.ts`.

---

### HINT-02 — Límite de 2 pistas por palabra
**Tipo:** `unit` · **Prioridad:** P1 · **Estado:** ✅

- Cubierto: `useHint (letter) should fail if limit of 2 is reached`.

---

### HINT-03 — Sin neuronas suficientes no se descuenta ni se revela
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ❌

- **Precondición:** `usuario.neuronas = 3`, costo de letra 5.
- **Esperado:** `{ success: false, error: 'Not enough neuronas' }`. `neuronas` sigue en 3 y `revealedIndices` no cambia.

---

### HINT-04 — Dos pistas concurrentes no dejan saldo negativo 🔴
**Tipo:** `int` · **Prioridad:** P1 · **Ref:** S-5

- **Precondición:** `usuario.neuronas = 5`, costo de letra 5.
- **Pasos:** `await Promise.all([useHint(1, 'letter'), useHint(2, 'letter')])`.
- **Esperado:** Exactamente una tiene éxito. `neuronas === 0`.
- **Comportamiento actual:** Ambas pasan el `if (user.neuronas < cost)` antes de que ninguna descuente → `neuronas === -5`.

---

### HINT-05 — No se puede pistar una palabra ya resuelta
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ❌ · **Ref:** B-5

- **Precondición:** Palabra 1 con `guessed: true`.
- **Esperado:** `{ success: false }` sin descontar neuronas.

---

### HINT-06 — No se puede pistar una palabra de otro nivel
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ❌ · **Ref:** B-5

- **Precondición:** `usuario.levelId = 1`; `wordId` pertenece al nivel 5.
- **Esperado:** `{ success: false }` sin descontar neuronas.

---

### HINT-07 — Todas las letras ya reveladas
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ❌

- **Precondición:** Palabra de 2 letras con ambas posiciones en `revealedIndices`.
- **Esperado:** `{ success: false, error: 'No hay más letras para revelar.' }` sin descontar.
- **Nota:** hoy el límite de 2 pistas enmascara este camino para palabras cortas; se vuelve alcanzable si el límite se hace configurable.

---

### HINT-08 — Saltar descuenta 15 y marca la palabra
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ⚠️

- **Esperado:** `neuronas -= 15` y `Progress.skipped = true`.
- **Nota:** cubierto con mocks.

---

### HINT-09 — El costo mostrado coincide con el cobrado 🔴
**Tipo:** `int` + `e2e` · **Prioridad:** P1 · **Ref:** B-4

- **Precondición:** `Setting['costo_pista'] = '8'`.
- **Esperado:** El botón dice "LETRA (-8 N)", se deshabilita con `neuronas < 8`, y el servidor cobra 8.
- **Comportamiento actual:** El botón sigue diciendo "-5 N" y se habilita con 5 neuronas (hardcodeado en `GameEngine.tsx`); el servidor cobra 8. El jugador clickea y la operación falla.
- **Verificar también:** `Level.costJump` / `costLetter` / `costHelp` existen en el schema y **no se leen nunca**; el costo de salto está hardcodeado en 15 en el servidor.

---

### HINT-10 — Fallo de `useHint` al saltar no deja el juego trabado
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ❌ · **Ref:** B-8

- **Pasos:** Mockear `useHint` para que rechace. Click en "SALTAR".
- **Esperado:** El error se captura, `isLoadingNext` vuelve a `false` y los botones se rehabilitan.
- **Comportamiento actual:** `handleSkip` no tiene `try/catch` (a diferencia de `handleGiveHint`): la excepción se propaga, `isLoadingNext` queda en `true` y el juego se congela.
- **Y:** ante `UnrecognizedActionError`, debe aparecer el modal "Sincronización requerida".

---

## PROG — Progresión de niveles

### PROG-01 — El nivel se completa al procesar la última palabra
**Tipo:** `unit` · **Prioridad:** P1 · **Estado:** ✅

- Cubierto: `should complete the level exactly on the 50th word`.

---

### PROG-02 — Las saltadas cuentan para completar el nivel
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ❌

- **Precondición:** Nivel con 5 palabras: 4 `guessed`, 1 `skipped`.
- **Esperado:** `isLevelComplete === true`, `levelStats.solved === 5`.

---

### PROG-03 — `moveToNextLevel` avanza y entrega la primera palabra
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ⚠️

- **Esperado:** `user.levelId` incrementa y `nextWord` es la de menor `id` del nuevo nivel.
- **Nota:** cubierto con mocks.

---

### PROG-04 — `moveToNextLevel` en el último nivel
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ❌

- **Precondición:** `usuario.levelId` es el máximo existente.
- **Esperado:** No avanza a un nivel inexistente. Devuelve un estado de "juego completado" manejable.
- **Comportamiento actual:** Incrementa `levelId` a un nivel que no existe y devuelve `nextWord: null`; en la siguiente carga, `getInitialGameState` no encuentra el nivel y devuelve `null`.

---

### PROG-05 — El modal de nivel completado muestra las estadísticas
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ✅

- Cubierto: `should show LevelUpModal when current level is completed` y el test de `Modals.test.tsx`.

---

### PROG-06 — El botón del modal avanza de nivel
**Tipo:** `unit` · **Prioridad:** P1 · **Estado:** ✅

- Cubierto: `should call moveToNextLevel and update state when level up modal button is clicked`.

---

### PROG-07 — `getInitialGameState` auto-avanza si el nivel está completo
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ❌

- **Precondición:** Todas las palabras del nivel 1 procesadas; existe nivel 2.
- **Esperado:** El usuario queda en `levelId: 2` y recibe la primera palabra del nivel 2.

---

### PROG-08 — No se puede saltear un nivel manipulando la llamada 🔴
**Tipo:** `int` · **Prioridad:** P1

- **Precondición:** `usuario.levelId = 1` con el nivel sin terminar.
- **Pasos:** Invocar `moveToNextLevel()` directamente.
- **Esperado:** `{ success: false }`. Solo se avanza con el nivel completo.
- **Comportamiento actual:** Avanza sin verificar nada. Invocable desde la consola del navegador.

---

## SYNC — Migración de invitado a cuenta autenticada

> Los casos SYNC-01 y SYNC-02 son los dos defectos más graves fuera de seguridad: hoy la migración falla justo cuando hay algo que migrar.

### SYNC-01 — Migrar un invitado **con progreso** 🔴
**Tipo:** `int` · **Prioridad:** **P0** · **Ref:** B-1

- **Precondición:** Invitado con 3 filas de `Progress`, `points: 3`, `neuronas: 80`, `levelId: 2`. Cuenta autenticada recién creada.
- **Pasos:** Invocar `migrateGuestProgress()`.
- **Esperado:**
  1. Las 3 filas de `Progress` quedan asociadas a la cuenta autenticada.
  2. El usuario invitado se borra sin error.
  3. La cookie `matete-guest-id` se elimina.
  4. `points += 3`, `neuronas += 30`, `levelId = max(1, 2) = 2`.
- **Comportamiento actual:** `prisma.user.delete` lanza **P2003**, porque `Progress.user` no tiene `onDelete: Cascade` y el FK es `ON DELETE RESTRICT`. La excepción ocurre **después** de incrementar las stats y **antes** de borrar la cookie.
- **Nota:** el test existente usa `progress: []` — el único caso que no falla.

---

### SYNC-02 — La migración no se aplica dos veces 🔴
**Tipo:** `int` · **Prioridad:** **P0** · **Ref:** B-1

- **Precondición:** La de SYNC-01.
- **Pasos:** Invocar `migrateGuestProgress()`, luego invocarlo **de nuevo** simulando una recarga de página.
- **Esperado:** La segunda invocación no encuentra cookie y no hace nada. Las stats se sumaron **una sola vez**.
- **Comportamiento actual:** Como la primera lanzó antes de borrar la cookie, la segunda vuelve a sumar los mismos puntos. `AuthSync` protege con un `useRef` que se reinicia en cada montaje, así que se repite en cada recarga, indefinidamente.

---

### SYNC-03 — Un invitado que gastó neuronas no le resta a la cuenta 🔴
**Tipo:** `int` · **Prioridad:** **P0** · **Ref:** B-2

- **Precondición:** Invitado con `neuronas: 20` (empezó con 50 y gastó 30). Cuenta autenticada con `neuronas: 100`.
- **Esperado:** La cuenta queda con `neuronas >= 100`.
- **Comportamiento actual:** `increment: 20 - 50 = -30` → la cuenta queda con 70. La migración **castiga** al jugador.

---

### SYNC-04 — Progreso duplicado entre invitado y cuenta
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ❌

- **Precondición:** Ambos resolvieron la palabra 1. La cuenta la resolvió en 10 s, el invitado en 40 s.
- **Esperado:** Queda una sola fila `Progress` para `(authUser, 1)`. Definir y verificar la política de desempate — se sugiere conservar el mejor tiempo.
- **Nota:** el `update` del upsert hoy pisa con los valores del invitado sin comparar.

---

### SYNC-05 — Sin sesión autenticada no se migra
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ✅

- Cubierto: `should return error if no authenticated session is found`.

---

### SYNC-06 — Sin cookie de invitado no se hace nada
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ✅

- Cubierto: `should return success but do nothing if no guest cookie is found`.

---

### SYNC-07 — Cookie apuntando a un invitado ya borrado
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ⚠️

- **Esperado:** La cookie se limpia y se devuelve un resultado no exitoso sin efectos colaterales.
- **Nota:** el camino existe en el código (`if (!guestUser) cookieStore.delete(...)`) pero no está testeado.

---

### SYNC-08 — La migración es atómica
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ❌ · **Ref:** B-1

- **Pasos:** Forzar un fallo a mitad de la transferencia (por ejemplo, un `wordId` que ya no existe).
- **Esperado:** Nada se aplica: ni el incremento de stats, ni el borrado del invitado, ni la eliminación de la cookie. El jugador puede reintentar.
- **Comportamiento actual:** Los pasos no están envueltos en `$transaction`; un fallo parcial deja las stats ya incrementadas.

---

## RANK — Rankings

### RANK-01 — El ranking global excluye invitados
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ✅ (con mocks)

- Cubierto: `getGlobalRanking should filter out users without email (guests)`. Conviene reescribirlo como `int`.

---

### RANK-02 — El ranking global ordena por puntos, tiempo y neuronas
**Tipo:** `int` · **Prioridad:** P1 · **Estado:** ❌

- **Precondición:** 4 usuarios con combinaciones que ejerciten los tres criterios, incluidos empates.
- **Esperado:** Orden `points desc`, luego `totalTime asc`, luego `neuronas desc`.

---

### RANK-03 — El ranking por nivel devuelve el top real 🔴
**Tipo:** `int` · **Prioridad:** P1 · **Ref:** B-7

- **Precondición:** 25 usuarios con progreso en el nivel 1. El usuario `campeon` tiene 40 palabras resueltas; el resto entre 1 y 5.
- **Pasos:** Invocar `getLevelRanking(1, 10)`.
- **Esperado:** `campeon` está en el primer puesto.
- **Comportamiento actual:** `take: 10` se aplica **antes** de ordenar, y el orden se hace en JS sobre esos 10. Postgres sin `ORDER BY` no garantiza orden: se toman 10 usuarios arbitrarios. Con 25 usuarios, `campeon` tiene ~60 % de probabilidad de ni aparecer.
- **Nota:** el test debe correr con un dataset > `limit`; con 10 o menos usuarios el defecto es invisible.

---

### RANK-04 — El ranking por nivel solo cuenta palabras adivinadas
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ❌

- **Precondición:** Usuario con 3 `guessed` y 5 `skipped` en el nivel 1.
- **Esperado:** `levelPoints === 3`. Las saltadas no suman.

---

### RANK-05 — Ranking vacío
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ❌

- **Esperado:** El componente muestra "NO HAY DATOS EN ESTE SECTOR" sin errores de consola.

---

### RANK-06 — El ranking no rompe con datos incompletos
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ❌

- **Precondición:** Usuarios con `username: null`, `name: null` e `image: null`.
- **Esperado:** Se muestra "Anónimo" y el ícono por defecto. Sin excepciones.
- **Nota:** `RankingBoard` usa `useState<any[]>`, así que TypeScript no protege este camino.

---

## USER — Alias

### USER-01 — El servidor valida el formato del alias 🔴
**Tipo:** `int` · **Prioridad:** P1 · **Ref:** S-6

- **Pasos:** Invocar `updateUsername()` directamente (sin pasar por el modal) con: `'ab'` (corto), `'a'.repeat(500)`, `'<script>alert(1)</script>'`, `'nombre con espacios'`, `''`, `'admin '`.
- **Esperado:** Todos rechazados. Solo se acepta `[a-zA-Z0-9_]{3,15}`.
- **Comportamiento actual:** Todos se aceptan y persisten. La única validación vive en el `onChange` del modal, y una Server Action es un endpoint HTTP público.

---

### USER-02 — El alias no se puede cambiar una vez establecido 🔴
**Tipo:** `int` · **Prioridad:** P1 · **Ref:** S-6

- **Precondición:** `usuario.username = 'Matetero'`.
- **Pasos:** Invocar `updateUsername('OtroNombre')`.
- **Esperado:** `{ success: false }`. El alias es la identidad en el ranking.
- **Comportamiento actual:** Se cambia sin restricción, tantas veces como se quiera.

---

### USER-03 — Alias duplicado devuelve un error específico 🔴
**Tipo:** `int` · **Prioridad:** P2 · **Ref:** S-6

- **Precondición:** Ya existe `'Matetero'`.
- **Esperado:** Error distinguible de un fallo de base — el código `P2002` de Prisma debe mapearse a un mensaje propio.
- **Comportamiento actual:** El `catch` genérico devuelve `'Error updating username'` tanto para un alias tomado como para una caída de Postgres.

---

### USER-04 — Carrera entre dos usuarios por el mismo alias
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ❌

- **Pasos:** Dos usuarios invocan `updateUsername('Mismo')` en paralelo.
- **Esperado:** Uno tiene éxito, el otro recibe el error de duplicado. El unique de la base debe sostenerlo aunque `checkUsername` haya dicho "disponible" a ambos.

---

### USER-05 — Un usuario sin alias ve el modal
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ❌

- **Esperado:** `getInitialGameState()` devuelve `needsUsername: true` para un autenticado sin `username`, y `false` una vez seteado.

---

### USER-06 — El modal de alias envía y cierra
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ✅

- Cubierto: `UsernameModal should render and handle submission`.

---

### USER-07 — `checkUsername` con rate limiting
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ❌ · **Ref:** S-7

- **Pasos:** 200 invocaciones seguidas desde el mismo origen.
- **Esperado:** Se aplica algún límite. Hoy es un oráculo de enumeración sin autenticación ni control.

---

## DATA — Schema, migraciones y seed

### DATA-01 — Las migraciones reconstruyen el schema actual 🔴
**Tipo:** `manual` / CI · **Prioridad:** **P0** · **Ref:** B-3

- **Pasos:** Contra una base Postgres vacía:
  ```bash
  npx prisma migrate deploy
  npx prisma migrate diff \
    --from-schema-datasource prisma/schema.prisma \
    --to-schema-datamodel prisma/schema.prisma \
    --exit-code
  ```
- **Esperado:** Exit code 0 — sin drift.
- **Comportamiento actual:** Faltan `User.username` (+ su índice único), `Progress.revealedIndices`, y las tablas `Setting` y `GameMessage`. La base de producción se actualizó con `db push`.
- **Este check debería correr en CI en cada PR.**

---

### DATA-02 — La app arranca sobre una base recién migrada 🔴
**Tipo:** `int` · **Prioridad:** **P0** · **Ref:** B-3

- **Pasos:** Base vacía → `migrate deploy` → seed → `getInitialGameState()`, `getGameSettings()`, `getRandomMessage('success')`.
- **Esperado:** Las tres responden sin error.
- **Comportamiento actual:** Fallan por las tablas faltantes de DATA-01.

---

### DATA-03 — Borrar un usuario borra su progreso 🔴
**Tipo:** `int` · **Prioridad:** P1 · **Ref:** B-1

- **Pasos:** Crear un usuario con filas de `Progress`, invocar `prisma.user.delete`.
- **Esperado:** Se borra en cascada, sin error de FK.
- **Comportamiento actual:** P2003. Es la causa raíz de SYNC-01.

---

### DATA-04 — El valor inicial de neuronas es consistente
**Tipo:** `manual` · **Prioridad:** P2 · **Estado:** ❌ · **Ref:** B-3

- **Verificar:** hoy hay **tres** valores distintos — `schema.prisma` dice `50`, la migración `init` dice `150`, y `auth.ts` usa `?? 150` como fallback.
- **Esperado:** Un solo valor, definido en el schema.

---

### DATA-05 — El seed es idempotente
**Tipo:** `int` · **Prioridad:** P2 · **Estado:** ❌

- **Pasos:** Ejecutar `scripts/seed-db.ts` dos veces.
- **Esperado:** El conteo de `GameMessage` es el mismo tras la segunda corrida (hoy hace `DELETE` previo, así que debería cumplirse — confirmarlo), y `Setting` no duplica filas.

---

### DATA-06 — Las listas de mensajes no divergen
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ❌ · **Ref:** C-7

- **Esperado:** Las listas de `scripts/seed-db.ts` coinciden con las de `src/lib/messages.ts`.
- **Nota:** hoy están **copiadas** en ambos archivos. La mejor corrección es que el seed importe de `src/lib/messages.ts`, con lo cual este caso deja de hacer falta.

---

## UI — Interacción del cliente

Área con buena cobertura. Los casos cubiertos se listan de forma condensada para poder rastrearlos por ID.

| ID | Caso | Estado |
|---|---|---|
| UI-01 | Renderiza la descripción y el nivel inicial | ✅ |
| UI-02 | Escribir con el teclado llena las casillas | ✅ |
| UI-03 | Acierto muestra estado de éxito y botón "SIGUIENTE" | ✅ |
| UI-04 | "SIGUIENTE" pide una palabra nueva | ✅ |
| UI-05 | Backspace borra y retrocede el cursor | ✅ |
| UI-06 | Backspace con la palabra llena borra la última letra | ✅ |
| UI-07 | "Limpiar" resetea el cursor a la primera posición editable | ✅ |
| UI-08 | "Limpiar" borra todas las letras no reveladas | ✅ |
| UI-09 | Comprar letra descuenta neuronas en pantalla | ✅ |
| UI-10 | Saltar descuenta neuronas en pantalla | ✅ |
| UI-11 | Enter tras un acierto pasa a la palabra siguiente | ✅ |
| UI-12 | Alterna entre modo pool y modo teclado | ✅ |
| UI-13 | El botón de sonido activa y desactiva | ✅ |
| UI-14 | El modal de instrucciones abre y cierra | ✅ |

### UI-15 — Las letras reveladas por pista no son editables
**Tipo:** `unit` · **Prioridad:** P1 · **Estado:** ❌

- **Precondición:** `initialRevealedIndices: [1, 3]`.
- **Pasos:** Intentar escribir sobre esas posiciones, hacer backspace hasta pasarlas, clickear la casilla, y usar "Limpiar".
- **Esperado:** El contenido de las posiciones 1 y 3 nunca cambia, y el cursor las saltea en ambas direcciones.
- **Nota:** la lógica de `getValidCursor` es de las más delicadas del componente y solo está cubierta indirectamente.

---

### UI-16 — La detección de móvil no rompe el render en servidor
**Tipo:** `unit` · **Prioridad:** P2 · **Estado:** ❌ · **Ref:** B-9

- **Pasos:** Renderizar `GameEngine` en un entorno sin `navigator` global.
- **Esperado:** No lanza `ReferenceError`.
- **Comportamiento actual:** `navigator.userAgent` se lee durante el render, y Next pre-renderiza los componentes cliente en el servidor.

---

## INFRA — Dependencias y build

### INFRA-01 — Sin vulnerabilidades altas o críticas 🔴
**Tipo:** CI · **Prioridad:** **P0** · **Ref:** S-9

- **Comando:** `npm audit --audit-level=high`
- **Esperado:** Exit code 0.
- **Estado actual:** 26 vulnerabilidades (3 críticas, 17 altas). `next-auth@4.24.13` tiene una crítica que afecta el flujo de OAuth de esta app; `next@16.1.7` arrastra 23 advisories, corregidas en 16.2.11.

---

### INFRA-02 — El typecheck pasa limpio 🔴
**Tipo:** CI · **Prioridad:** P1 · **Ref:** C-2

- **Comando:** `npx tsc --noEmit`
- **Estado actual:** 1 error en `src/app/actions/sync.test.ts:56` (tipo del mock de `findUnique`).

---

### INFRA-03 — El lint corre
**Tipo:** CI · **Prioridad:** P1 · **Estado:** ❌ · **Ref:** C-1

- **Comando:** `npm run lint`
- **Estado actual:** roto. `next lint` fue removido en Next 16 y no hay configuración de ESLint en el repositorio.

---

### INFRA-04 — El build de producción funciona
**Tipo:** CI · **Prioridad:** P1 · **Estado:** ❌

- **Comando:** `npm run build`
- **Esperado:** Exit code 0. Verificar que `prisma generate` corre antes del build de Next, como está definido en el script.

---

### INFRA-05 — No hay secretos versionados
**Tipo:** CI · **Prioridad:** P1 · **Estado:** ❌ · **Ref:** S-8

- **Verificar:** `git ls-files` no devuelve `.env`, `*.keystore`, `*.db` ni `*.sqlite`.
- **Estado actual:** `.env` está correctamente ignorado y nunca fue commiteado ✅, pero sí están versionados los dos keystores de firma de Android, `prisma/dev.db` y cinco `.sqlite` del proyecto legacy.

---

## Anexo A — Infraestructura necesaria

31 de los 86 casos son de tipo `int` y necesitan una Postgres real. Es la inversión que más cobertura desbloquea: **los 24 casos marcados 🔴 son en su mayoría indetectables con Prisma mockeado**, porque lo que falla son restricciones de la base (claves foráneas, unique, atomicidad) que un mock no modela.

Setup sugerido:

```yaml
# docker-compose.test.yml
services:
  db:
    image: postgres:17-alpine
    environment:
      POSTGRES_PASSWORD: test
      POSTGRES_DB: matete_test
    ports: ['5433:5432']
```

```jsonc
// package.json
"scripts": {
  "test:int": "DATABASE_URL=postgresql://postgres:test@localhost:5433/matete_test vitest run --config vitest.int.config.ts"
}
```

Con un `beforeEach` que trunque las tablas y siembre el mínimo necesario. Alternativa sin Docker: [Testcontainers](https://node.testcontainers.org/), que levanta y descarta el contenedor desde el propio test.

---

## Anexo C — Verificado contra producción (2026-08-06)

Consultas de solo lectura contra la base real. Los casos 🔴 dejaron de ser hipótesis derivadas del código: están confirmados, y varios ya causaron daño en los datos.

| Caso | Confirmación |
|---|---|
| `DATA-01` | La base tiene `username`, `revealedIndices`, `Setting` y `GameMessage`; `_prisma_migrations` solo registra las 2 migraciones del repo, que no los contienen. El drift es real y total. |
| `DATA-03` / `SYNC-01` | `Progress_userId_fkey` es `RESTRICT`. **35 invitados tienen progreso** — cada uno rompe la migración al loguearse. |
| `GAME-03` / `SYNC-02` | **364 puntos fantasma sobre 949** (38 %). Caso extremo: 349 puntos y 2.420 neuronas contra 21 palabras adivinadas. |
| `HINT-09` | Los costos de `Level` son datos configurados a propósito (35/25/30 en 32 niveles) que el código ignora por completo. |
| `HINT-04` | Sin explotar: ningún saldo negativo. El defecto sigue latente. |
| `GAME-05` | Sin explotar: ningún `timeTaken` en 0 entre 585 adivinadas. |
| `USER-01` | Sin explotar: los 22 alias cumplen el formato. |
| `GAME-08` / `PROG-04` | No alcanzables hoy: todos los niveles tienen palabras y nadie supera el nivel 37. Los defectos siguen en el código. |

### Ajustes al catálogo que surgen de los datos

**`GAME-11` — el escenario de 50 palabras no existe.** En producción los niveles tienen entre **5 y 34 palabras** (promedio 13,1); el nivel 1, donde está el 73 % de los usuarios, tiene **9**. Los tests de progresión deberían parametrizarse por tamaño de nivel y cubrir al menos un nivel corto (5) y uno largo (34), en vez de un 50 fijo que nadie atraviesa.

**`RANK-01` — el ranking excluye al 94 % de los usuarios.** Solo 3 de 49 tienen `email`. Pero el modal de alias se le muestra también a los invitados: 22 eligieron alias y 19 nunca podrán figurar. Agregar un caso que fije la decisión de producto, cualquiera sea:

### DATA-07 — Reparación del puntaje inflado
**Tipo:** `int` · **Prioridad:** **P0** · **Estado:** ❌ · **Ref:** S-3

- **Precondición:** Corregida la idempotencia de `recordWordSolved` (GAME-03).
- **Pasos:** Ejecutar el script de reparación que recalcula `points` como el conteo real de `Progress.guessed` por usuario.
- **Esperado:** `sum(points) === count(Progress WHERE guessed)`. Definir aparte qué hacer con las neuronas infladas: recalcularlas rompe saldos que los jugadores ya creen tener.
- **Nota:** corregir el código no repara los datos existentes. Son dos tareas distintas y esta es la que el jugador va a notar.

---

## Anexo B — Orden de ataque sugerido

Los casos 🔴 escritos **antes** de la corrección sirven como verificación de que el arreglo es real. Este orden agrupa los que se resuelven con el mismo cambio:

| Paso | Casos | Corrección asociada |
|---|---|---|
| 1 | `DATA-01`, `DATA-02` | Generar la migración faltante |
| 2 | `DATA-03`, `SYNC-01`, `SYNC-02`, `SYNC-03`, `SYNC-08` | Arreglar `migrateGuestProgress` + cascada en `Progress` |
| 3 | `AUTH-02`, `AUTH-03`, `AUTH-04` | Firmar la cookie de invitado y dejar de exponer `id` |
| 4 | `INFRA-01` | `npm audit fix` |
| 5 | `GAME-03`, `GAME-04`, `GAME-07` | Idempotencia de `recordWordSolved` |
| 6 | `GAME-01`, `GAME-02`, `GAME-05` | Mover la validación al servidor |
| 7 | `RANK-03` | Agregación en la base para el ranking por nivel |
| 8 | `HINT-04`, `USER-01`, `USER-02`, `USER-03` | Descuento atómico y validación de alias |
| 9 | `GAME-08`, `HINT-10`, `UI-16`, `PROG-04`, `PROG-08` | Robustez |
| 10 | `INFRA-02`, `INFRA-03`, `INFRA-04`, `INFRA-05` | Gates de CI |

El paso 2 solo se puede verificar de verdad con la infraestructura del Anexo A, así que conviene montarla junto con el paso 1.
