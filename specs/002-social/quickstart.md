# Quickstart: validar 002-social

Prerrequisitos: los mismos de [001](../001-hardening/quickstart.md) (Postgres local, `.env.test`). El `.env` local apunta a producción: todo comando contra una base va con `npx dotenv -e .env.test -- …`.

## 1. Suites automáticas

```bash
npm test -- --run            # unit + componentes
npm run test:integration     # incluye share-result, comments, follows, challenges
npx tsc --noEmit
npx next build
```

| Escenario | Requisito |
| --- | --- |
| `submitGuess` devuelve tiempo y letras reveladas del servidor | FR-003 |
| Texto compartido sin solución ni definición | FR-002, SC-002 |
| Comentarios bloqueados (ni cantidad) sin acierto, con salteo o sin usuario | FR-005 |
| Invitado / sin username: lee, no publica | FR-006 |
| Cuerpo vacío, > 280, HTML guardado como texto | FR-007, FR-009 |
| 2 comentarios simultáneos y < 30 s → uno; tope 20/día | FR-008 |
| 3 reportes distintos → oculto; reporte repetido no suma; restaurar exige 3 nuevos | FR-010, FR-013, SC-003 |
| Moderación: admin sí, otro no | FR-012 |
| Seguir idempotente, self / inexistente / invitado rechazados, tope 500 | FR-014, FR-015 |
| Ranking de amigos y marcas `isFollowed` sin `id` | FR-017..FR-019 |
| Desafío: crear idempotente, solo sobre palabra acertada, tope 20/día | FR-020, FR-021 |
| Tiempo desde `startChallenge`, un intento, creador no juega | FR-024, FR-026, SC-004 |
| `knewWord`, victoria, orden de resultados | FR-027, FR-029 |
| Desafío no cambia neuronas/puntos/tiempo/nivel/progreso | FR-028, SC-005 |

**Resultado del 2026-11-15**: 90 tests unit/componentes (14 archivos), 78 de integración (8 archivos: 39 de 001 + 39 de 002), `tsc` limpio, `next build` OK (`/desafio/[code]`, `/desafios` y `/admin/comentarios` dinámicas).

## 2. Prueba manual

```bash
npx dotenv -e .env.test -- npx next dev
```

Con `ADMIN_EMAILS=<tu email>` en `.env.test` y dos cuentas de Google (o una cuenta y una ventana privada):

1. Resolver una palabra → "Compartir": pegar el texto; no tiene la palabra.
2. "Comentarios": publicar uno. Desde la otra cuenta, antes de resolver esa palabra, no se ve nada; tras resolverla aparece. Reportarlo.
3. Ranking → "Amigos": buscar al otro usuario, seguirlo, verlo en la tabla. Dejar de seguir.
4. "Desafiar" → abrir el link en la otra cuenta (y en una ventana sin sesión: pide ingresar). Jugar, ver el resultado en ambas cuentas y en "Mis desafíos".
5. `/admin/comentarios` con la cuenta admin: ver el reportado, ocultarlo y restaurarlo. Con la otra cuenta: 404.
6. Compartir el link del desafío en WhatsApp/Facebook: la vista previa muestra desafiante y tiempo.

## 3. Producción — PENDIENTE de confirmación del dueño

Requiere 001 desplegado (baseline registrado).

1. `pg_dump` completo.
2. Variable `ADMIN_EMAILS` en Hostinger.
3. `npx prisma migrate deploy` (aplica `20261115200000_social`: solo crea tablas).
4. Deploy.
5. Verificación: punto 2 de la prueba manual con una cuenta real.
