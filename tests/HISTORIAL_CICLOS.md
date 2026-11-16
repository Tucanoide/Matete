# Historial de ciclos de prueba — Matete Web

Registro histórico de las corridas de prueba y corrección. Los casos citados están en [CASOS_DE_PRUEBA.md](./CASOS_DE_PRUEBA.md). Cada corrida nueva se agrega al final, sin modificar las anteriores.

**Definiciones**

- **Tests ejecutados:** tests automáticos corridos al inicio del ciclo: `npm test -- --run` (unitarios y de componente) más `npm run test:integration` (Postgres real).
- **Chequeos:** `npx tsc --noEmit`, `npm run lint`, `npm audit --audit-level=high`, `npx next build`.
- **Issue:** defecto distinto detectado en el ciclo. Varios tests o errores de lint con la misma causa cuentan como uno.
- **Repetido:** issue que ya se había dado por corregido en un ciclo anterior y que reapareció o se corrigió mal.

---

## Corrida 2026-11-16: tres ciclos sobre `002-social`

- **Base:** commit `70240c2` (002-social sobre 001-hardening), catálogo v2.0 con 12 defectos 🔴.
- **Resultado:** commit `cc4e9fd`, catálogo v2.1.
- **Procedimiento por ciclo:** ejecución de tests y chequeos → identificación de issues (los casos abiertos se escribieron primero como tests que fallan) → corrección de todos → casos nuevos si hacían falta. Al final, una ejecución sin corrección.

### Resumen

| Ciclo | Tests ejecutados | Tests que fallaron | Issues detectados | Repetidos |
|---|---|---|---|---|
| 1 | 235 (108 unit + 127 int) | 27, más lint roto y audit con 39 vulnerabilidades | 18 | 0 |
| 2 | 235 (108 unit + 127 int) | 0, pero 36 errores de lint | 13 | 1 |
| 3 | 243 (116 unit + 127 int) | 4, escritos para reproducir | 4 | 2 |
| Final, sin corrección | 249 (121 unit + 128 int) | 0 | 0 | — |

| Chequeo | Antes del ciclo 1 | Ejecución final |
|---|---|---|
| `tsc --noEmit` | OK | OK |
| `npm run lint` | Roto (`next lint` no existe en Next 16) | 0 errores, 0 warnings |
| `npm audit --audit-level=high` | 39 vulnerabilidades (5 críticas, 24 altas) | Exit 0 (7 moderadas, todas en herramientas de lint) |
| `next build` | OK | OK |
| Catálogo | 123 casos: 103 ✅, 3 ⚠️, 4 ❌, 12 🔴, 1 ➖ | 128 casos: 126 ✅, 1 ❌ (AUTH-09, manual), 1 ➖ |

### Ciclo 1: casos abiertos del catálogo

Tests nuevos: `src/test/integration/fixes.int.test.ts`, `src/test/infra.test.ts` y tests agregados a `GameEngine.test.tsx`. Corrieron 235 tests y fallaron 27.

| # | Caso | Error encontrado | Corrección |
|---|---|---|---|
| 1 | UI-19 | El segundo Backspace con teclado físico no borraba | Handler sin `setState` anidado |
| 2 | HINT-10 | Un error al saltar dejaba los botones deshabilitados | `try/catch/finally` en `handleSkip` |
| 3 | HINT-09 | El botón decía "-5 N"; el servidor podía cobrar otro valor | Costos desde `Setting` (`costo_pista`, `costo_salto`), enviados por `getInitialGameState` |
| 4 | PROG-09 | Al terminar el último nivel: modal trabado y home en "Cargando..." | `GameOverPanel`, `getGameCompletion`, `gameOver` en `moveToNextLevel` |
| 5 | UI-16 | `navigator` se leía en el render | Lectura fuera del render |
| 6 | USER-01 | El servidor aceptaba cualquier alias | Validación `[a-zA-Z0-9_]{3,15}` (`src/lib/username.ts`) |
| 7 | USER-02 | El alias se podía cambiar | Se fija una vez (`UPDATE ... WHERE username IS NULL`) |
| 8 | USER-03 | Duplicado devolvía un error genérico | "Ese nombre ya está en uso." |
| 9 | USER-07 | `checkUsername` sin límite ni identidad | Exige identidad; 30 por minuto |
| 10 | SYNC-09 | El alias del invitado se perdía al entrar con Google | La migración lo copia a la cuenta |
| 11 | RANK-03 | El ranking por nivel tomaba 10 usuarios arbitrarios | SQL con `ORDER BY` y `LIMIT` |
| 12 | AUTH-08 | La sesión exponía `User.id` | Se quitó el campo |
| 13 | DATA-05 | El seed pisaba el costo configurado | `ON CONFLICT DO NOTHING`, en una transacción |
| 14 | INFRA-05 | Keystores, `.db` y `.sqlite` en el repo público | `git rm --cached` y `.gitignore` |
| 15 | INFRA-06 | `CODE_REVIEW.md` no estaba ignorado | `.gitignore` |
| 16 | INFRA-03 | Lint roto | ESLint 9 con `eslint-config-next` (`eslint.config.mjs`) |
| 17 | INFRA-01 | 39 vulnerabilidades | `npm audit fix`: `next` 16.4.6, `next-auth` 4.24.15 |
| 18 | INI-01 | El modal prometía el ranking a invitados | Texto nuevo |

Sin defecto: SYNC-08 (migración atómica, verificada con un trigger que inyecta un fallo) y DATA-06 (las listas de mensajes coincidían). Pasaron al primer intento; solo les faltaba el test.

Ajustes de contrato en tests existentes: PROG-04 espera `gameOver: true`; en INI-01, el otro navegador primero obtiene identidad antes de consultar el alias.

### Ciclo 2: hallazgos del lint recién habilitado

Corrieron 235 tests, todos en verde. El lint dio 36 errores; se agruparon en 13 issues.

| # | Dónde | Error encontrado | Corrección |
|---|---|---|---|
| 1 | Server Action `useHint` | El prefijo `use` la hacía pasar por un hook (4 errores) | Renombrada a `requestHint` |
| 2 | GameEngine | El listener de teclado usaba handlers de Enter desactualizados | Referencia actualizada en cada render |
| 3 | GameEngine, móvil | **Repetido de UI-16**: la corrección del ciclo 1 usaba `setState` en un efecto | `useSyncExternalStore` |
| 4 | GameEngine, neuronas | `setState` en un efecto | Ajuste durante el render |
| 5 | UsernameModal | `setState` en un efecto | Estado derivado |
| 6 | VictoryParticles | `Math.random` en el render y `setState` en un efecto | Inicializador perezoso |
| 7 | ChallengePlay | Refs escritas durante el render | Se escriben en un efecto |
| 8 | ChallengePlay | `<a>` en lugar de `Link` | `Link` |
| 9 | CommentsPanel (SOC-25) | Al cambiar de palabra podían quedar los comentarios de la anterior | Respuesta vieja descartada |
| 10 | FriendsTab | `setState` en efectos | Promesas y estado derivado |
| 11 | GameEngine, InstructionsModal | Comillas sin escapar | `&ldquo;` y `&rdquo;` |
| 12 | `auth.ts`, `prisma.ts`, RankingBoard, scripts, tests | Tipos `any` | Tipos reales |
| 13 | RankingBoard (RANK-07) | Tiempo `NaN` y `alt` nulo, detectados al tipar | `?? 0` y `?? ''` |

Casos nuevos: `src/app/page.test.tsx` (home en fin de juego, costos), avisos del modal de alias (límite, duplicado, texto), RANK-07 y SOC-25.

### Ciclo 3: regresiones e interacciones entre correcciones

Corrieron 243 tests y los chequeos. Se encontraron 4 issues; los 4 tests escritos para reproducirlos fallaron antes de corregir.

| # | Caso | Error encontrado | Corrección |
|---|---|---|---|
| 1 | SYNC-10, **repetido de SYNC-09** | Tras el login, la home se renderizaba antes de migrar. Con el alias inmutable, el modal no se podía cerrar | `AuthSync` refresca después de migrar; `updateUsername` responde `alreadySet` y el modal se cierra |
| 2 | UI-21, **repetido del ciclo 2** | La corrección de neuronas no aplicaba una sesión ya cargada al montar | El estado arranca en `undefined` |
| 3 | UI-22 | `InstructionsModal` ignoraba `isOpen` | Ahora la respeta |
| 4 | Lint | 28 warnings: imports sin usar, `<img>`, PostCSS | 0 warnings |

Casos nuevos: SYNC-10 (`fixes.int.test.ts`, `AuthSync.test.tsx`, `Modals.test.tsx`), UI-21 y UI-22.

### Ejecución final, sin corrección

| Suite o chequeo | Resultado |
|---|---|
| Unitarios y de componente | 121 de 121 (18 archivos) |
| Integración (Postgres `matete_test`) | 128 de 128 (11 archivos) |
| `tsc --noEmit` | OK |
| `npm run lint` | 0 errores, 0 warnings |
| `npm audit --audit-level=high` | Exit 0 |
| `next build` | OK |

### Pendiente fuera del código

- **INFRA-05:** rotar los keystores; siguen en el historial del repo público.
- **AUTH-09 y UI-19:** prueba manual en el navegador (login con Google, con y sin progreso de invitado; Backspace con teclado físico).
- **Decisiones de producto:** costos por nivel de `Level` (HINT-09); si el modal de alias se puede cerrar y si se pide en la primera visita (INI-01).
- **Producción:** los pasos de los quickstarts de 001 y 002, cada uno con confirmación del dueño.
