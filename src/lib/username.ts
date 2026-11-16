// Reglas del nombre de fantasía (alias). Una sola fuente para el modal y el servidor.

export const USERNAME_PATTERN = /^[a-zA-Z0-9_]{3,15}$/

/** Consultas de disponibilidad permitidas por jugador y por minuto (USER-07). */
export const USERNAME_CHECKS_PER_MINUTE = 30

export function isValidUsername(value: unknown): value is string {
  return typeof value === 'string' && USERNAME_PATTERN.test(value)
}
