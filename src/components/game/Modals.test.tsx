import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import '@testing-library/jest-dom'
import { vi, describe, it, expect } from 'vitest'
import UsernameModal from './UsernameModal'
import LevelUpModal from './LevelUpModal'
import InstructionsModal from './InstructionsModal'
import { checkUsername, updateUsername } from '@/app/actions/game'

// Mock Server Actions
vi.mock('@/app/actions/game', () => ({
  updateUsername: vi.fn(() => Promise.resolve({ success: true })),
  checkUsername: vi.fn(() => Promise.resolve({ available: true })),
  moveToNextLevel: vi.fn(() => Promise.resolve({ success: true })),
}))

// Mock Next-Auth
vi.mock('next-auth/react', () => ({
  useSession: vi.fn(() => ({ data: { user: { name: 'Test' } }, status: 'authenticated', update: vi.fn() })),
}))

describe('Game Modals', () => {
  const user = userEvent.setup()

  describe('UsernameModal', () => {
    it('[USER-06] should render and handle submission', async () => {
      const onSuccess = vi.fn()
      render(<UsernameModal onSuccess={onSuccess} />)
      
      const input = screen.getByPlaceholderText(/Matetero_Alpha/i)
      await user.type(input, 'testuser')
      
      // We need to wait for the debounced checkUsername to complete and set status to 'available'
      await waitFor(() => {
        expect(screen.getByRole('button', { name: /ESTABLECER ALIAS/i })).not.toBeDisabled()
      })

      const submitBtn = screen.getByRole('button', { name: /ESTABLECER ALIAS/i })
      await user.click(submitBtn)
      
      await waitFor(() => {
        expect(onSuccess).toHaveBeenCalled()
      })
    })
  })

  describe('LevelUpModal', () => {
    it('[PROG-05] should render stats and handle next level click', async () => {
      const onNextLevel = vi.fn()
      const stats = { solved: 5, total: 5, levelId: 1 }
      render(<LevelUpModal level={1} stats={stats} onNextLevel={onNextLevel} />)
      
      expect(screen.getByText(/NIVEL 1 COMPLETADO/i)).toBeInTheDocument()
      expect(screen.getByText(/5\/5/i)).toBeInTheDocument()
      
      const nextBtn = screen.getByRole('button', { name: /ACCEDER AL NIVEL 2/i })
      await user.click(nextBtn)
      
      expect(onNextLevel).toHaveBeenCalled()
    })
  })

  describe('InstructionsModal', () => {
    it('[UI-14] should render and handle close', async () => {
      const onClose = vi.fn()
      render(<InstructionsModal isOpen={true} onClose={onClose} />)
      
      expect(screen.getByText(/Cómo jugar/i)).toBeInTheDocument()
      
      const closeBtn = screen.getByRole('button', { name: /¡LISTO PARA EL MATETE!/i })
      await user.click(closeBtn)
      
      expect(onClose).toHaveBeenCalled()
    })
  })
})

describe('[USER-07] UsernameModal ante límites y errores del servidor', () => {
  const user = userEvent.setup()

  it('muestra el aviso de demasiadas consultas y no habilita el envío', async () => {
    vi.mocked(checkUsername).mockResolvedValueOnce({ available: false, error: 'rate_limited' })
    render(<UsernameModal onSuccess={vi.fn()} />)
    await user.type(screen.getByPlaceholderText(/Matetero_Alpha/i), 'ZorroAzul')
    expect(await screen.findByText(/Demasiadas consultas/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /ESTABLECER ALIAS/i })).toBeDisabled()
  })

  it('[USER-03] muestra el error específico del servidor al guardar', async () => {
    vi.mocked(checkUsername).mockResolvedValue({ available: true })
    vi.mocked(updateUsername).mockResolvedValueOnce({ success: false, error: 'Ese nombre ya está en uso.' })
    const onSuccess = vi.fn()
    render(<UsernameModal onSuccess={onSuccess} />)
    await user.type(screen.getByPlaceholderText(/Matetero_Alpha/i), 'ZorroAzul')
    const btn = screen.getByRole('button', { name: /ESTABLECER ALIAS/i })
    await waitFor(() => expect(btn).not.toBeDisabled())
    await user.click(btn)
    expect(await screen.findByText('Ese nombre ya está en uso.')).toBeInTheDocument()
    expect(onSuccess).not.toHaveBeenCalled()
  })

  it('[INI-01] el texto explica el nombre de fantasía sin prometer el ranking a invitados', () => {
    render(<UsernameModal onSuccess={vi.fn()} />)
    expect(screen.getByText(/nombre de fantasía/i)).toBeInTheDocument()
    expect(screen.queryByText(/para el ranking/i)).not.toBeInTheDocument()
  })
})

describe('[SYNC-10] UsernameModal cuando el alias ya quedó fijado', () => {
  const user = userEvent.setup()
  it('si el servidor dice que ya tiene alias (p. ej. migrado desde el invitado), el modal se cierra', async () => {
    vi.mocked(checkUsername).mockResolvedValue({ available: true })
    vi.mocked(updateUsername).mockResolvedValueOnce({ success: false, error: 'Ya elegiste tu nombre y no se puede cambiar.', alreadySet: true })
    const onSuccess = vi.fn()
    render(<UsernameModal onSuccess={onSuccess} />)
    await user.type(screen.getByPlaceholderText(/Matetero_Alpha/i), 'OtroNombre')
    const btn = screen.getByRole('button', { name: /ESTABLECER ALIAS/i })
    await waitFor(() => expect(btn).not.toBeDisabled())
    await user.click(btn)
    await waitFor(() => expect(onSuccess).toHaveBeenCalled())
  })
})

describe('[UI-22] InstructionsModal respeta isOpen', () => {
  it('cerrado no renderiza nada', () => {
    const { container } = render(<InstructionsModal isOpen={false} onClose={vi.fn()} />)
    expect(container).toBeEmptyDOMElement()
  })
})
