# Quickstart: validar 001-hardening

Guía para comprobar de punta a punta que la feature cumple la [spec](./spec.md). Los detalles de datos y contratos están en [data-model.md](./data-model.md) y [contracts/server-actions.md](./contracts/server-actions.md).

## Prerrequisitos

- Node y dependencias instaladas (`npm install`).
- Postgres local (Homebrew): `brew services start postgresql@18` o `pg_ctl -D /opt/homebrew/var/postgresql@18 start`.
- Bases locales: `createdb matete_test` y `createdb matete_shadow` (la shadow la usa `prisma migrate diff`).
- `.env.test` a partir de `.env.test.example` (no se commitea). **Ojo**: el `.env` local apunta a producción; todo comando contra una base se corre con `npx dotenv -e .env.test -- …`. La suite de integración aborta si la base no termina en `_test`.

## 1. Base reproducible desde cero (US4, FR-023, SC-006)

```bash
createdb matete_e2e_test                                                   # base nueva y vacía
npx dotenv -e <env con DATABASE_URL=…/matete_e2e_test> -- npx prisma migrate deploy
npx dotenv -e <mismo env> -- npx tsx scripts/migrate_db.ts                  # 37 niveles y 486 palabras desde la base legacy
npx dotenv -e <mismo env> -- npx tsx scripts/seed-db.ts                     # settings y mensajes
```

No se usa `prisma migrate reset`: Prisma lo bloquea cuando lo ejecuta un agente y no hace falta sobre una base nueva.

**Esperado**: termina sin errores; `\d "User"` muestra `username` y `neuronas DEFAULT 50`; existen `Setting` y `GameMessage`; `\d "Progress"` muestra `revealedIndices`.

Equivalencia con producción (solo lectura, introspección; la shadow es local):

```bash
SHADOW_DATABASE_URL="postgresql://localhost:5432/matete_shadow" \
  npx prisma migrate diff --from-config-datasource --to-migrations prisma/migrations --script
```

Resultado del 2026-11-15 contra producción: `-- This is an empty migration.` (sin diferencias).

Generación de la migración `sync_schema` (referencia):

```bash
npx dotenv -e .env.test -- npx prisma migrate diff \
  --from-migrations prisma/migrations --to-schema prisma/schema.prisma --script
```

## 2. Suites automáticas

```bash
npm test                    # unit + componentes (jsdom)
npm run test:integration    # server actions contra Postgres real
npx tsc --noEmit
```

**Esperado**: todo en verde. Las suites de integración cubren:

| Escenario | Requisito |
| --- | --- |
| Cookie alterada, expirada o de otra clave → visitante nuevo | FR-001, US1-1 |
| Cookie (nueva o legacy) que apunta a cuenta registrada → rechazada | FR-003, US1-3, US1-5 |
| Cookie legacy de invitado dentro del plazo → aceptada y reemitida firmada | FR-004, US1-4 |
| Cookie legacy fuera de plazo → rechazada | FR-004 |
| Ranking sin `id` | FR-005, US1-2 |
| `PublicWord` sin solución | FR-006, SC-002 |
| 100 `submitGuess` correctos → +10/+1 una sola vez | FR-011, SC-003 |
| `submitGuess` incorrecto → sin cambios | FR-007, US2-3 |
| `timeTaken` = tiempo desde la entrega, ignora cliente | FR-016, US2-5 |
| Dos pistas simultáneas con saldo para una → una cobrada, saldo ≥ 0 | FR-012, US2-6 |
| Pista/salto sobre palabra acertada → rechazado sin cobro | FR-013 |
| Migración de invitado con progreso → completa, invitado borrado | FR-018, US3-1 |
| Migración repetida 10 veces / concurrente → totales iguales | FR-020, SC-005 |
| Invitado con neuronas < 50 → la cuenta no pierde neuronas | FR-021, US3-3 |
| Palabra resuelta por ambos → cuenta una vez | FR-022, US3-5 |

## 3. Prueba manual en el navegador (FR-028, SC-007)

```bash
npx dotenv -e .env.test -- npx next dev
```

1. Abrir `http://localhost:3000` en ventana privada. DevTools → Network: ninguna respuesta contiene la palabra en juego en claro (buscar la palabra en las respuestas tras resolverla en otra pestaña).
2. Application → Cookies: `matete-guest` aparece con `HttpOnly`. `document.cookie` en la consola no la muestra.
3. Jugar en modo pool y en modo teclado: acertar, errar, pedir 2 pistas, saltar, completar un nivel. Mismo comportamiento, sonidos y mensajes que antes; la respuesta a cada intento llega en menos de 1 segundo.
4. Editar un carácter de la cookie y recargar: se arranca como visitante nuevo, sin error.
5. Loguearse con Google con progreso de invitado y recargar varias veces: puntos estables.

## 4. Recálculo de datos (FR-026, FR-027)

```bash
npx dotenv -e .env.test -- npx tsx scripts/recalculate-points.ts          # dry-run: muestra usuarios y diferencia total
npx dotenv -e .env.test -- npx tsx scripts/recalculate-points.ts --apply  # crea User_backup_20261115 y recalcula
```

**Esperado**: después de `--apply`, para todo usuario `points = count(guessed)` y `totalTime = sum(timeTaken guessed)`; neuronas sin cambios; existe la tabla de respaldo.

**Producción**: solo con confirmación explícita del dueño, después del deploy y tras un `pg_dump` completo. Dry-run primero; diferencia esperada ≈ 364 puntos.

## 5. Deploy a producción — PENDIENTE de confirmación del dueño

Ninguno de estos pasos se ejecutó. Cada uno escribe en producción y requiere confirmación explícita.

1. **Backup**: `pg_dump` completo de la base de producción.
2. **Variables en Hostinger**:
   - `GUEST_COOKIE_SECRET`: generar con `openssl rand -base64 48`. Sin ella, los invitados no pueden jugar (fail closed).
   - `GUEST_LEGACY_COOKIE_UNTIL`: fecha del deploy + 30 días (ISO, ej. `2026-12-20`).
3. **Baseline de migraciones**: `npx prisma migrate resolve --applied 20261115120000_sync_schema` contra producción. Solo registra la migración en `_prisma_migrations` y no ejecuta SQL. El diff de solo lectura del 2026-11-15 confirmó que la estructura ya coincide.
4. **Deploy** de la app (branch `001-hardening` mergeado).
5. **Recálculo de datos**:
   - `npx tsx scripts/recalculate-points.ts` (dry-run; diferencia esperada ≈ −364 puntos).
   - `npx tsx scripts/recalculate-points.ts --apply` (crea `User_backup_20261115`).
6. **Verificación**: abrir el juego en una ventana privada, revisar que la cookie `matete-guest` tenga `HttpOnly` y jugar una palabra. Volver a correr el dry-run: debe dar 0 diferencias.
7. **A los 30 días o más**: borrar la tabla `User_backup_20261115` una vez validado. La variable `GUEST_LEGACY_COOKIE_UNTIL` puede quedar, porque vencida no tiene efecto.
