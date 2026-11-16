import { describe, it, expect } from 'vitest'
import pg from 'pg'
import prisma from '@/lib/prisma'
import {
  getInitialGameState,
  getGameCompletion,
  startWord,
  submitGuess,
  requestHint,
  moveToNextLevel,
  checkUsername,
  updateUsername,
  getLevelRanking,
} from '@/app/actions/game'
import { migrateGuestProgress } from '@/app/actions/sync'
import { authOptions } from '@/lib/auth'
import { GUEST_COOKIE, verifyGuestToken } from '@/lib/guest-cookie'
import { USERNAME_CHECKS_PER_MINUTE } from '@/lib/username'
import { SUCCESS_MESSAGES, FAIL_MESSAGES } from '@/lib/messages'
import { seed } from '../../../scripts/seed-db'
import { jar, loginAs, newBrowser, createRegisteredUser, solveWord, WORDS } from './harness'

// Casos que estaban abiertos en tests/CASOS_DE_PRUEBA.md v2.0 (🔴, ⚠️ y ❌).

const W1 = WORDS.L1[0]

function guestId() {
  const c = jar.get(GUEST_COOKIE)
  return c ? verifyGuestToken(c.value) : null
}

async function guestWithAlias(alias: string) {
  await startWord(W1.id)
  expect(await updateUsername(alias)).toEqual({ success: true })
  return guestId()!
}

describe('USER — alias validado en el servidor', () => {
  it.each(['ab', 'a'.repeat(500), '<script>alert(1)</script>', 'nombre con espacios', '', 'admin '])(
    '[USER-01] rechaza %j',
    async (alias) => {
      await startWord(W1.id)
      const r = await updateUsername(alias)
      expect(r.success).toBe(false)
      const u = await prisma.user.findUniqueOrThrow({ where: { id: guestId()! } })
      expect(u.username).toBeNull()
    }
  )

  it('[USER-01] checkUsername no da por disponible un formato inválido', async () => {
    await startWord(W1.id)
    expect(await checkUsername('a b')).toMatchObject({ available: false })
  })

  it('[USER-02] el alias no se cambia una vez elegido', async () => {
    const id = await guestWithAlias('Primero')
    expect(await updateUsername('Otro')).toMatchObject({ success: false })
    expect((await prisma.user.findUniqueOrThrow({ where: { id } })).username).toBe('Primero')
  })

  it('[USER-03] alias duplicado devuelve un error propio', async () => {
    await createRegisteredUser('t@example.com', { username: 'Tomado' })
    await startWord(W1.id)
    expect(await updateUsername('Tomado')).toEqual({ success: false, error: 'Ese nombre ya está en uso.' })
  })

  it('[USER-07] checkUsername tiene límite por jugador y exige identidad', async () => {
    expect(await checkUsername('Alguien')).toMatchObject({ available: false, error: 'no_identity' })

    await startWord(W1.id)
    const results = []
    for (let i = 0; i < USERNAME_CHECKS_PER_MINUTE + 5; i++) results.push(await checkUsername(`Libre${i}`))
    expect(results.slice(0, USERNAME_CHECKS_PER_MINUTE).every((r) => r.available)).toBe(true)
    expect(results.at(-1)).toMatchObject({ available: false, error: 'rate_limited' })
  })
})

describe('SYNC — migración invitado → Google', () => {
  it('[SYNC-09] la cuenta sin alias hereda el nombre de fantasía del invitado', async () => {
    await guestWithAlias('ZorroAzul')
    const g = await createRegisteredUser('g@example.com')
    loginAs('g@example.com')
    expect(await migrateGuestProgress()).toEqual({ success: true, migrated: true })
    expect((await prisma.user.findUniqueOrThrow({ where: { id: g.id } })).username).toBe('ZorroAzul')
  })

  it('[SYNC-09] si la cuenta ya tenía alias se conserva el de la cuenta', async () => {
    await guestWithAlias('ZorroAzul')
    const g = await createRegisteredUser('g@example.com', { username: 'Propio' })
    loginAs('g@example.com')
    await migrateGuestProgress()
    expect((await prisma.user.findUniqueOrThrow({ where: { id: g.id } })).username).toBe('Propio')
  })

  it('[SYNC-08] un fallo a mitad de la migración no deja nada aplicado', async () => {
    const guest = await guestWithAlias('Boom_guest')
    await submitGuess(W1.id, W1.word)
    const g = await createRegisteredUser('g@example.com')

    await prisma.$executeRawUnsafe(`
      CREATE OR REPLACE FUNCTION matete_test_boom() RETURNS trigger AS $$
      BEGIN IF OLD.username = 'Boom_guest' THEN RAISE EXCEPTION 'boom'; END IF; RETURN OLD; END
      $$ LANGUAGE plpgsql`)
    await prisma.$executeRawUnsafe(
      'CREATE TRIGGER matete_test_boom BEFORE DELETE ON "User" FOR EACH ROW EXECUTE FUNCTION matete_test_boom()'
    )
    try {
      loginAs('g@example.com')
      await expect(migrateGuestProgress()).rejects.toThrow()
    } finally {
      await prisma.$executeRawUnsafe('DROP TRIGGER IF EXISTS matete_test_boom ON "User"')
      await prisma.$executeRawUnsafe('DROP FUNCTION IF EXISTS matete_test_boom()')
    }

    expect(await prisma.progress.count({ where: { userId: g.id } })).toBe(0)
    expect(await prisma.user.findUniqueOrThrow({ where: { id: g.id } })).toMatchObject({ points: 0, neuronas: 50, username: null })
    expect(await prisma.progress.count({ where: { userId: guest } })).toBe(1)
    expect(guestId()).toBe(guest) // la cookie sigue: se puede reintentar
  })
})

describe('RANK', () => {
  it('[RANK-03] el ranking por nivel ordena y limita en la base', async () => {
    for (let i = 0; i < 25; i++) {
      const u = await createRegisteredUser(`u${i}@example.com`, { username: `jugador${i}` })
      await solveWord(u.id, WORDS.L1[0].id)
    }
    const campeon = await createRegisteredUser('c@example.com', { username: 'campeon' })
    for (const w of WORDS.L1) await solveWord(campeon.id, w.id)

    const top = await getLevelRanking(1, 10)
    expect(top).toHaveLength(10)
    expect(top[0]).toMatchObject({ username: 'campeon', levelPoints: 3 })
    expect(top[0]).not.toHaveProperty('id')
  })
})

describe('HINT — costos desde una sola fuente', () => {
  it('[HINT-09] el estado inicial informa los costos que después se cobran', async () => {
    await prisma.setting.update({ where: { id: 'costo_pista' }, data: { value: '8' } })
    await prisma.setting.create({ data: { id: 'costo_salto', value: '20' } })
    await startWord(W1.id)

    const state = await getInitialGameState()
    expect(state?.costs).toEqual({ letter: 8, skip: 20 })

    const letter = await requestHint(W1.id, 'letter')
    expect(letter).toMatchObject({ success: true, remainingNeuronas: 42 })
    const skip = await requestHint(W1.id, 'skip')
    expect(skip).toMatchObject({ success: true, remainingNeuronas: 22 })
  })

  it('[HINT-09] sin configuración usa 5 y 15', async () => {
    await prisma.setting.deleteMany()
    await startWord(W1.id)
    expect((await getInitialGameState())?.costs).toEqual({ letter: 5, skip: 15 })
  })
})

describe('PROG — fin del juego', () => {
  async function finishEverything() {
    await startWord(W1.id)
    const id = guestId()!
    for (const w of [...WORDS.L1, ...WORDS.L2]) await solveWord(id, w.id)
    await prisma.user.update({ where: { id }, data: { levelId: 2, points: 5 } })
    return id
  }

  it('[PROG-09] terminar el último nivel informa fin de juego (servidor)', async () => {
    await finishEverything()
    expect(await moveToNextLevel()).toEqual({ success: false, error: 'No hay más niveles.', gameOver: true })
    expect(await getInitialGameState()).toBeNull()
    expect(await getGameCompletion()).toEqual({ completed: true, points: 5 })
  })

  it('[PROG-09] un jugador con palabras pendientes o sin identidad no está en fin de juego', async () => {
    expect(await getGameCompletion()).toEqual({ completed: false, points: 0 })
    await startWord(W1.id)
    expect(await getGameCompletion()).toEqual({ completed: false, points: 0 })
  })
})

describe('AUTH', () => {
  it('[AUTH-08] la sesión no expone User.id', async () => {
    const u = await createRegisteredUser('s@example.com')
    const session = await authOptions.callbacks!.session!({ session: { user: { email: 's@example.com' } }, user: { id: u.id } } as never)
    expect((session as { user: Record<string, unknown> }).user).not.toHaveProperty('id')
  })
})

describe('DATA — seed', () => {
  async function runSeed() {
    const pool = new pg.Pool({ connectionString: process.env.DATABASE_URL })
    try {
      await seed(pool)
    } finally {
      await pool.end()
    }
  }

  it('[DATA-05] correrlo dos veces deja los mismos conteos y no pisa costos configurados', async () => {
    await prisma.setting.update({ where: { id: 'costo_pista' }, data: { value: '8' } })
    await runSeed()
    await runSeed()
    expect(await prisma.gameMessage.count({ where: { type: 'success' } })).toBe(SUCCESS_MESSAGES.length)
    expect(await prisma.gameMessage.count({ where: { type: 'fail' } })).toBe(FAIL_MESSAGES.length)
    expect((await prisma.setting.findUniqueOrThrow({ where: { id: 'costo_pista' } })).value).toBe('8')
    expect((await prisma.setting.findUniqueOrThrow({ where: { id: 'costo_salto' } })).value).toBe('15')
  })

  it('[DATA-06] el seed carga exactamente los mensajes de src/lib/messages.ts', async () => {
    await runSeed()
    const rows = await prisma.gameMessage.findMany()
    expect(rows.filter((r) => r.type === 'success').map((r) => r.content).sort()).toEqual([...SUCCESS_MESSAGES].sort())
    expect(rows.filter((r) => r.type === 'fail').map((r) => r.content).sort()).toEqual([...FAIL_MESSAGES].sort())
  })
})

describe('INI', () => {
  it('[INI-01] otro navegador ve el alias como tomado', async () => {
    await guestWithAlias('ZorroAzul')
    newBrowser()
    await startWord(W1.id)
    expect(await checkUsername('ZorroAzul')).toMatchObject({ available: false })
  })
})

describe('SYNC-10 — alias migrado y modal abierto', () => {
  it('[SYNC-10] tras migrar el alias, un updateUsername tardío informa alreadySet y la home ya no lo pide', async () => {
    await guestWithAlias('ZorroAzul')
    await createRegisteredUser('g@example.com')
    loginAs('g@example.com')
    // La home se renderizó antes de migrar: la cuenta todavía no tenía alias.
    expect((await getInitialGameState())?.needsUsername).toBe(true)
    await migrateGuestProgress()
    expect(await updateUsername('OtroNombre')).toMatchObject({ success: false, alreadySet: true })
    expect((await getInitialGameState())?.needsUsername).toBe(false)
  })
})
