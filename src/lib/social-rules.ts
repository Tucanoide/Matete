// Límites de las funciones sociales (spec 002-social). Única fuente de verdad (constitución, principio 4).

export const COMMENT_MAX_LENGTH = 280
export const COMMENT_MIN_INTERVAL_SECONDS = 30
export const COMMENT_DAILY_LIMIT = 20
export const COMMENT_REPORTS_TO_HIDE = 3
export const COMMENTS_PAGE_SIZE = 20

export const FOLLOW_LIMIT = 500
export const PLAYER_SEARCH_MIN = 2
export const PLAYER_SEARCH_MAX_RESULTS = 10

export const CHALLENGE_DAILY_LIMIT = 20
export const CHALLENGE_CODE_LENGTH = 10

export type CommentStatus = 'visible' | 'hidden_reports' | 'hidden_admin' | 'deleted'
