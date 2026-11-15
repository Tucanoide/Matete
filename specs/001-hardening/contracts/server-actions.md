# Contract: Server Actions (UI ↔ servidor)

Interfaz entre `GameEngine`/`RankingBoard`/`AuthSync` y `src/app/actions/*.ts` después de esta feature. Toda action resuelve la identidad en el servidor (sesión de Google o cookie de invitado firmada); ninguna recibe ni devuelve `User.id`.

## Tipo compartido

```ts
type PublicWord = {
  wordId: number
  description: string
  length: number                                   // 4..11
  pool: string[]                                   // 14 letras en mayúscula, desordenadas
  revealed: { index: number; letter: string }[]    // letras ya reveladas por pista
}
```

`PublicWord` nunca contiene la solución.

## getInitialGameState() — Server Component (`page.tsx`)

```ts
() => Promise<{
  word: PublicWord
  levelId: number
  neuronas: number
  needsUsername: boolean
} | null>
```

- Con usuario: primera palabra pendiente de su nivel. Registra la entrega (create-only).
- Sin usuario: primera palabra del nivel 1. No registra nada (no hay usuario; ver `startWord`).
- Si todos los niveles están completos: devuelve `null` en lugar de lanzar (corrige el `TypeError` de B-6 solo en lo necesario para no romper el render).

## startWord(wordId) — **nueva**

```ts
(wordId: number) => Promise<{ ok: true } | { ok: false; error: 'not_in_play' }>
```

- La llama `GameEngine` al montar (las palabras siguientes ya registran su entrega en `getNextWord` / `moveToNextLevel`).
- `ensureUser()` (crea invitado y cookie firmada si hace falta; reemite cookie legacy).
- Registra la entrega si la palabra está en juego. Idempotente.

## submitGuess(wordId, guess) — **nueva**, reemplaza `recordWordSolved`

```ts
(wordId: number, guess: string) => Promise<
  | { correct: false }
  | { correct: true
      solution: string
      alreadySolved: boolean        // true: ya estaba acertada, no se acreditó nada
      neuronas: number
      points: number
      isLevelComplete: boolean
      levelStats: { solved: number; total: number; levelId: number } }
  | { error: 'not_in_play' | 'invalid_guess' }
>
```

- `invalid_guess`: `guess` vacío, con caracteres fuera de `[A-Za-zÑñ]` o de largo distinto al de la palabra.
- Comparación sin distinguir mayúsculas.
- `correct: true` acredita +10 neuronas / +1 punto / tiempo de servidor **una sola vez** (R4).
- `correct: false` no modifica nada y no revela qué letras fallaron.
- Una palabra ya acertada responde `correct: true, alreadySolved: true` (idempotente). Una salteada o de otro nivel: `not_in_play`.

## useHint(wordId, type) — modificada

```ts
(wordId: number, type: 'letter' | 'skip') => Promise<
  | { success: true; remainingNeuronas: number; revealed: { index: number; letter: string } }   // letter
  | { success: true; remainingNeuronas: number; isLevelComplete: boolean
      levelStats?: { solved: number; total: number; levelId: number } }                        // skip
  | { success: false; error: string }
>
```

- Cambio de contrato: la pista de letra devuelve `revealed: { index, letter }` en lugar de `revealedIndices`.
- Rechaza sin cobrar si la palabra no está en juego, si se alcanzó el límite de 2 o si el saldo no alcanza.
- Cobro atómico: el saldo nunca queda negativo con pedidos simultáneos.

## getNextWord(levelId) — renombre de `getRandomWordFromLevel`

```ts
(levelId: number) => Promise<PublicWord | null>
```

- Palabra al azar en juego del nivel del usuario (ignora `levelId` si no coincide con `user.levelId`). Registra la entrega. `null` = nivel completo.

## moveToNextLevel() — modificada

```ts
() => Promise<{ success: true; nextLevelId: number; nextWord: PublicWord | null; neuronas: number }
            | { success: false; error: string }>
```

- Solo avanza si el nivel actual está completo (todas sus palabras acertadas o salteadas).
- Registra la entrega de `nextWord`.

## getGlobalRanking(limit) / getLevelRanking(levelId, limit) — modificadas

```ts
type RankingEntry = { name: string | null; username: string | null; image: string | null; points: number; totalTime: number }
```

- Sin `id`. `RankingBoard` usa la posición como `key`.

## migrateGuestProgress() — modificada (`sync.ts`)

```ts
() => Promise<{ success: true; migrated: boolean } | { success: false; error: string }>
```

- Atómica e idempotente (R5). `migrated: false` cuando no había invitado que migrar.

## Sin cambios

`getRandomMessage`, `getGameSettings`, `getLevels`, `checkUsername`, `updateUsername` (sus correcciones S-6/S-7 están fuera de alcance).

## Eliminadas

- `recordWordSolved(wordId, timeTaken)` → reemplazada por `submitGuess`.
- `getWordsByLevel(levelId)`: devolvía soluciones al cliente y no tiene uso en la UI.
