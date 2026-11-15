# Data Model: 002-social

Migración: `prisma/migrations/20261115200000_social`. Solo agrega tablas; no modifica las existentes.

## Comment

| Campo | Tipo | Notas |
| --- | --- | --- |
| id | String (cuid) | PK. Se expone al cliente (no es un id de usuario). |
| wordId | Int | FK → Word |
| authorId | String | FK → User (cascade) |
| body | String | 1..280 tras normalizar |
| status | String | `visible` \| `hidden_reports` \| `hidden_admin` \| `deleted` (default `visible`) |
| reportCount | Int | reportes desde la última revisión de admin (default 0) |
| createdAt | DateTime | default now() |

Índices: `(wordId, status, createdAt)`, `(authorId, createdAt)`, `(status)`.

Transiciones:

- `visible → hidden_reports`: el 3.er reporte (automático).
- `visible | hidden_reports → hidden_admin`: admin oculta.
- `hidden_reports | hidden_admin → visible`: admin restaura (`reportCount = 0`).
- `* → deleted`: el autor borra. Terminal.

## CommentReport

| Campo | Tipo | Notas |
| --- | --- | --- |
| commentId | String | FK → Comment (cascade) |
| reporterId | String | FK → User (cascade) |
| createdAt | DateTime | default now() |

PK `(commentId, reporterId)`.

## Follow

| Campo | Tipo | Notas |
| --- | --- | --- |
| followerId | String | FK → User (cascade) |
| followeeId | String | FK → User (cascade) |
| createdAt | DateTime | default now() |

PK `(followerId, followeeId)`; índice `(followeeId)`. Regla: `followerId ≠ followeeId` (validado en la action).

## Challenge

| Campo | Tipo | Notas |
| --- | --- | --- |
| id | String (cuid) | PK, interno |
| code | String | único, 10 caracteres base62; va en el link |
| creatorId | String | FK → User (cascade) |
| wordId | Int | FK → Word |
| creatorTime | Float | `Progress.timeTaken` del creador al crear |
| creatorHints | Int | `cardinality(Progress.revealedIndices)` del creador |
| createdAt | DateTime | default now() |

Únicos: `code`, `(creatorId, wordId)`. Índice `(creatorId, createdAt)`.

## ChallengeAttempt

| Campo | Tipo | Notas |
| --- | --- | --- |
| id | String (cuid) | PK, interno |
| challengeId | String | FK → Challenge (cascade) |
| userId | String | FK → User (cascade) |
| startTime | DateTime | default now(); inicio del tiempo |
| endTime | DateTime? | null = en curso |
| timeTaken | Float? | segundos, medido en servidor |
| solved | Boolean | default false |
| gaveUp | Boolean | default false |
| knewWord | Boolean | default false; fijado al crear el intento |

Único `(challengeId, userId)`; índice `(userId, startTime)`.

Estados: en curso (`endTime` null) → resuelto (`solved`) o rendido (`gaveUp`). Ambos terminales.

## Relaciones nuevas en User y Word

- `User`: `comments`, `commentReports`, `following` (Follow.follower), `followers` (Follow.followee), `challenges`, `challengeAttempts`.
- `Word`: `comments`, `challenges`.

## Configuración

| Variable | Uso |
| --- | --- |
| `ADMIN_EMAILS` | emails de Google con acceso a moderación, separados por coma |

Límites (en `src/lib/social-rules.ts`): 280 caracteres, 30 s entre comentarios, 20 comentarios/24 h, 3 reportes para ocultar, 20 por página, 500 seguidos, 10 resultados de búsqueda, 20 desafíos/24 h.
