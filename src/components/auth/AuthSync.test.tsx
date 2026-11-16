import { render, waitFor } from '@testing-library/react'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import { AuthSync } from './AuthSync'
import { migrateGuestProgress } from '@/app/actions/sync'

const refresh = vi.fn()
const update = vi.fn(() => Promise.resolve())
vi.mock('next/navigation', () => ({ useRouter: () => ({ refresh }) }))
vi.mock('next-auth/react', () => ({ useSession: () => ({ status: 'authenticated', update }) }))
vi.mock('@/app/actions/sync', () => ({ migrateGuestProgress: vi.fn() }))

beforeEach(() => vi.clearAllMocks())

describe('[SYNC-10] AuthSync', () => {
  it('tras migrar al invitado vuelve a pedir el estado al servidor (alias, neuronas, nivel)', async () => {
    vi.mocked(migrateGuestProgress).mockResolvedValue({ success: true, migrated: true })
    render(<AuthSync />)
    await waitFor(() => expect(refresh).toHaveBeenCalledTimes(1))
    expect(update).toHaveBeenCalled()
  })

  it('sin nada que migrar no recarga', async () => {
    vi.mocked(migrateGuestProgress).mockResolvedValue({ success: true, migrated: false })
    render(<AuthSync />)
    await waitFor(() => expect(migrateGuestProgress).toHaveBeenCalled())
    expect(refresh).not.toHaveBeenCalled()
  })
})
