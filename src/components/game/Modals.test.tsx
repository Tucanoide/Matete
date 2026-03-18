import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import '@testing-library/jest-dom'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import UsernameModal from './UsernameModal'
import LevelUpModal from './LevelUpModal'
import InstructionsModal from './InstructionsModal'

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
    it('should render and handle submission', async () => {
      const onSuccess = vi.fn()
      render(<UsernameModal onSuccess={onSuccess} />)
      
      const input = screen.getByPlaceholderText(/Matetero_Alpha/i)
      await user.type(input, 'testuser')
      
      // We need to wait for the debounced checkUsername to complete and set status to 'available'
      await waitFor(() => {
        const checkIcon = screen.queryByTestId('CheckCircle2') // Wait, does it have an id? No.
        // It has a CheckCircle2 component from lucide.
        // The button becomes enabled when status is 'available'.
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
    it('should render stats and handle next level click', async () => {
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
    it('should render and handle close', async () => {
      const onClose = vi.fn()
      render(<InstructionsModal isOpen={true} onClose={onClose} />)
      
      expect(screen.getByText(/Cómo jugar/i)).toBeInTheDocument()
      
      const closeBtn = screen.getByRole('button', { name: /¡LISTO PARA EL MATETE!/i })
      await user.click(closeBtn)
      
      expect(onClose).toHaveBeenCalled()
    })
  })
})
