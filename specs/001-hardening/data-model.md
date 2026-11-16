# Data Model: 001-hardening

Esta feature **no agrega tablas ni columnas**. Cambia invariantes, el uso de campos existentes y la sincronización de migraciones (R6).

## User

| Campo | Tipo | Cambio en esta feature |
| --- | --- | --- |
| `id` | String (cuid) | Nunca sale del servidor (FR-005). Se firma dentro de la cookie de invitado, que es `httpOnly`. |
| `email` | String? | `NULL` ⇔ invitado. La cookie de invitado solo resuelve usuarios con `email IS NULL` (FR-003). |
| `points` | Int | **Invariante**: `points = count(Progress WHERE userId = id AND guessed)`. Se mantiene por incremento atómico en el acierto, y por recálculo en la migración de invitado y en el script R7. |
| `totalTime` | Float (segundos) | **Invariante**: `totalTime = sum(Progress.timeTaken WHERE guessed)`. |
| `neuronas` | Int, default 50 | Nunca < 0 (cobro condicional). Default único 50 (FR-025): se corrige el fallback `?? 150` en `auth.ts`. |
| `levelId` | Int | Sin cambios. Define qué palabras están "en juego". |
| `username` | String? @unique | Sin cambios funcionales; entra a las migraciones (R6). |

## Progress (jugador × palabra)

| Campo | Tipo | Cambio en esta feature |
| --- | --- | --- |
| `startTime` | DateTime, default now() | Pasa a significar **momento de entrega**. Se fija al crear la fila al entregar la palabra y nunca se actualiza (create-only). |
| `guessed` | Boolean | Transición `false → true` una sola vez (UPDATE condicional). |
| `skipped` | Boolean | Transición `false → true` una sola vez, solo si `guessed = false`. |
| `timeTaken` | Float? (segundos) | Calculado en el servidor: `now() - startTime` al acertar. |
| `endTime` | DateTime? | `now()` al acertar. |
| `revealedIndices` | Int[] | Máx. 2. Actualización optimista (`equals` al valor leído). Entra a las migraciones (R6). |

**Unique**: `(userId, wordId)` (existente) — garantiza una fila por jugador y palabra.

### Estados de una palabra para un jugador

```text
(sin fila) ──entrega──▶ EN JUEGO ──acierto──▶ ACERTADA   (+10 neuronas, +1 punto, una vez)
                          │
                          └──salto───▶ SALTEADA   (−15 neuronas, una vez)

EN JUEGO admite pistas de letra (≤ 2, cada una cobra 1 vez).
ACERTADA y SALTEADA son terminales: rechazan intentos, pistas y saltos sin cobrar.
"En juego" exige además word.levelId = user.levelId.
```

## Word

Sin cambios de schema. `word.word` solo se lee en el servidor; al cliente llega `PublicWord` (ver [contracts/server-actions.md](./contracts/server-actions.md)). La solución se envía solo después de un acierto.

## Credencial de invitado (no persistida)

| Elemento | Valor |
| --- | --- |
| Cookie nueva | `matete-guest` = `v1.<userId>.<exp>.<hmac-sha256-base64url>` |
| Atributos | `httpOnly`, `sameSite=lax`, `secure` en prod, `path=/`, 30 días |
| Secreto | `GUEST_COOKIE_SECRET` |
| Cookie vieja | `matete-guest-id` = `<userId>` en claro; aceptada solo si `now < GUEST_LEGACY_COOKIE_UNTIL` y el usuario es invitado; se reemplaza y se borra al aceptarla |

## Respaldo del recálculo (R7)

`User_backup_20261115 (id, points, totalTime, neuronas)`: tabla creada por el script con `--apply`, fuera del schema de Prisma. Se conserva hasta validar el recálculo y luego se borra a mano.

## Migraciones (R6)

| Migración | Estado |
| --- | --- |
| `20260318005350_init` | Existente |
| `20260318012531_remove_unique_word` | Existente |
| `<timestamp>_sync_schema` | **Nueva**: `User.username` + unique, `Progress.revealedIndices`, tablas `Setting` y `GameMessage`, default `neuronas` = 50. En producción se marca como aplicada (`migrate resolve`), no se ejecuta. |
