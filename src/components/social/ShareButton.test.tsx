import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import '@testing-library/jest-dom'
import { vi, describe, it, expect, afterEach } from 'vitest'
import ShareButton, { shareOrCopy } from './ShareButton'

// [SOC-04] compartir: menú nativo → portapapeles (FR-001)

function setNavigator(share: unknown, writeText: unknown) {
  Object.defineProperty(navigator, 'share', { value: share, configurable: true })
  Object.defineProperty(navigator, 'clipboard', { value: writeText ? { writeText } : undefined, configurable: true })
}

afterEach(() => setNavigator(undefined, undefined))

describe('[SOC-04] shareOrCopy', () => {
  it('usa el menú nativo si existe', async () => {
    const share = vi.fn(() => Promise.resolve())
    const writeText = vi.fn()
    setNavigator(share, writeText)
    expect(await shareOrCopy('hola')).toBe('shared')
    expect(share).toHaveBeenCalledWith({ text: 'hola' })
    expect(writeText).not.toHaveBeenCalled()
  })

  it('si el usuario cancela el menú nativo no copia', async () => {
    const abort = Object.assign(new Error('cancel'), { name: 'AbortError' })
    const writeText = vi.fn()
    setNavigator(vi.fn(() => Promise.reject(abort)), writeText)
    expect(await shareOrCopy('hola')).toBe('shared')
    expect(writeText).not.toHaveBeenCalled()
  })

  it('sin menú nativo copia; si falla todo informa failed', async () => {
    const writeText = vi.fn(() => Promise.resolve())
    setNavigator(undefined, writeText)
    expect(await shareOrCopy('hola')).toBe('copied')
    setNavigator(undefined, vi.fn(() => Promise.reject(new Error('denied'))))
    expect(await shareOrCopy('hola')).toBe('failed')
  })
})

describe('ShareButton', () => {
  it('muestra "¡COPIADO!" al copiar', async () => {
    const writeText = vi.fn(() => Promise.resolve())
    setNavigator(undefined, writeText)
    render(<ShareButton getText={() => 'texto'} />)
    await userEvent.click(screen.getByRole('button', { name: /COMPARTIR/i }))
    await waitFor(() => expect(writeText).toHaveBeenCalledWith('texto'))
    expect(await screen.findByText('¡COPIADO!')).toBeInTheDocument()
  })
})
