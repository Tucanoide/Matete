import { render, screen } from '@testing-library/react'
import '@testing-library/jest-dom'
import { vi, describe, it, expect } from 'vitest'
import ChallengeWordList from './ChallengeWordList'

vi.mock('next-auth/react', () => ({ useSession: () => ({ status: 'authenticated' }), signIn: vi.fn() }))
vi.mock('@/app/actions/challenges', () => ({ createChallenge: vi.fn() }))

describe('[SOC-38] ChallengeWordList', () => {
  it('lista las palabras acertadas con su botón Desafiar', () => {
    render(
      <ChallengeWordList
        words={[
          { wordId: 1, word: 'MUDANZA', length: 7, timeTaken: 42, challenged: true },
          { wordId: 2, word: 'BISOÑE', length: 6, timeTaken: 75, challenged: false },
        ]}
      />
    )
    expect(screen.getByText('MUDANZA')).toBeInTheDocument()
    expect(screen.getByText(/ya lo compartiste/)).toBeInTheDocument()
    expect(screen.getAllByRole('button', { name: /DESAFIAR/ })).toHaveLength(2)
  })

  it('sin palabras acertadas explica cómo y lleva a jugar', () => {
    render(<ChallengeWordList words={[]} />)
    expect(screen.getByText(/necesitás acertar una palabra/)).toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'IR A JUGAR' })).toHaveAttribute('href', '/')
  })
})
