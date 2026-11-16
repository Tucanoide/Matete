# Contract: Server Actions sociales

Todas resuelven la identidad en el servidor con `getCurrentUser()` (001). Ninguna recibe ni devuelve `User.id`. A otros jugadores se los identifica por `username`.

"Usuario social" = sesión de Google (`email` no nulo) **y** `username` definido. Errores comunes:

```ts
type SocialError = 'login_required' | 'username_required'
```

## Tipos compartidos

```ts
type PlayerRef = { username: string; image: string | null }

type CommentView = {
  id: string
  body: string
  createdAt: string          // ISO
  author: PlayerRef
  mine: boolean
  reportedByMe: boolean
}
```

## game.ts — cambios

### submitGuess(wordId, guess)

La rama `correct: true` agrega:

```ts
result: { timeTaken: number; revealedIndices: number[] }   // de Progress, medido en servidor
```

### getGlobalRanking / getLevelRanking

Cada entrada agrega `isMe: boolean` e `isFollowed: boolean` (false si no hay usuario social). Siguen sin `id`.

## comments.ts

### getWordComments(wordId, cursor?)

```ts
(wordId: number, cursor?: string) => Promise<
  | { locked: true }                                  // sin usuario o sin acierto: nada más
  | { locked: false
      comments: CommentView[]                         // solo status 'visible', más nuevos primero, 20
      nextCursor: string | null
      canPost: boolean
      postBlockedReason?: SocialError }
>
```

### postComment(wordId, body)

```ts
(wordId: number, body: string) => Promise<
  | { ok: true; comment: CommentView }
  | { ok: false; error: SocialError | 'not_solved' | 'invalid_body' | 'too_fast' | 'daily_limit' }
>
```

### reportComment(commentId)

```ts
(commentId: string) => Promise<{ ok: true; hidden: boolean } | { ok: false; error: SocialError | 'not_found' | 'own_comment' | 'not_solved' }>
```

Idempotente: el mismo jugador reportando dos veces devuelve `ok: true` sin sumar.

### deleteComment(commentId)

```ts
(commentId: string) => Promise<{ ok: true } | { ok: false; error: 'login_required' | 'not_found' | 'forbidden' }>
```

## follows.ts

```ts
followPlayer(username: string): Promise<{ ok: true } | { ok: false; error: SocialError | 'not_found' | 'self' | 'limit' }>
unfollowPlayer(username: string): Promise<{ ok: true } | { ok: false; error: SocialError }>
searchPlayers(prefix: string): Promise<{ ok: true; players: (PlayerRef & { isFollowed: boolean })[] } | { ok: false; error: SocialError | 'too_short' }>
getFriendsRanking(): Promise<
  | { ok: true; entries: (PlayerRef & { points: number; totalTime: number; isMe: boolean })[] }
  | { ok: false; error: SocialError }
>
```

- `followPlayer` es idempotente. `not_found` incluye usuarios sin Google.
- `searchPlayers`: prefijo de 2 a 15 caracteres, sin distinguir mayúsculas, excluye al propio jugador, máximo 10.

## challenges.ts

```ts
type ChallengeResult = PlayerRef & { solved: boolean; gaveUp: boolean; knewWord: boolean; timeTaken: number | null; won: boolean }

createChallenge(wordId: number): Promise<{ ok: true; code: string; username: string } | { ok: false; error: SocialError | 'not_solved' | 'daily_limit' }>

getChallenge(code: string): Promise<null | {
  code: string
  creator: PlayerRef
  length: number
  creatorTime: number
  creatorHints: number
  results: ChallengeResult[]                         // terminados: aciertos primero, por tiempo
  viewer: 'anonymous' | 'username_required' | 'creator' | 'can_play' | 'in_progress' | 'finished'
  myResult: ChallengeResult | null
  solution: string | null                            // solo si viewer = 'finished'
}>

startChallenge(code: string): Promise<
  | { ok: true; word: { description: string; length: number; pool: string[] }; elapsed: number }
  | { ok: false; error: SocialError | 'not_found' | 'own_challenge' | 'finished' }
>

submitChallengeGuess(code: string, guess: string): Promise<
  | { correct: false }
  | { correct: true; solution: string; timeTaken: number; creatorTime: number; won: boolean; knewWord: boolean }
  | { error: SocialError | 'not_found' | 'not_started' | 'finished' | 'invalid_guess' }
>

giveUpChallenge(code: string): Promise<{ ok: true; solution: string } | { ok: false; error: SocialError | 'not_found' | 'not_started' | 'finished' }>

getMyChallenges(): Promise<
  | { ok: true
      created: { code: string; length: number; creatorTime: number; participants: number; createdAt: string }[]
      played: { code: string; creator: PlayerRef; result: ChallengeResult; playedAt: string }[] }
  | { ok: false; error: SocialError }
>
```

- `createChallenge` sobre una palabra con desafío previo del mismo creador devuelve el mismo `code`.
- `startChallenge` es idempotente: si el intento existe y está en curso, devuelve la palabra y `elapsed` (segundos desde el inicio real).
- Ninguna de estas actions escribe `User` ni `Progress`.

## moderation.ts

```ts
getModerationQueue(): Promise<{ ok: true; items: (CommentView & { status: string; reportCount: number; word: string })[] } | { ok: false; error: 'forbidden' }>
moderateComment(commentId: string, action: 'hide' | 'restore'): Promise<{ ok: true } | { ok: false; error: 'forbidden' | 'not_found' }>
```

Solo para emails en `ADMIN_EMAILS`. La cola incluye comentarios con `status` `hidden_*` o `reportCount > 0` y no borrados.
