// Fecha relativa en español rioplatense ("hace 5 min").

export function formatRelativeTime(date: Date | string, now: Date = new Date()): string {
  const then = typeof date === 'string' ? new Date(date) : date
  const seconds = Math.max(0, Math.floor((now.getTime() - then.getTime()) / 1000))
  if (seconds < 60) return 'recién'
  const minutes = Math.floor(seconds / 60)
  if (minutes < 60) return `hace ${minutes} min`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `hace ${hours} h`
  const days = Math.floor(hours / 24)
  if (days === 1) return 'ayer'
  if (days < 30) return `hace ${days} días`
  const months = Math.floor(days / 30)
  if (months < 12) return months === 1 ? 'hace 1 mes' : `hace ${months} meses`
  const years = Math.floor(days / 365)
  return years <= 1 ? 'hace 1 año' : `hace ${years} años`
}
