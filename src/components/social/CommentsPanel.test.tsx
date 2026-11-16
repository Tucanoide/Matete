import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import '@testing-library/jest-dom'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import CommentsPanel from './CommentsPanel'
import { getWordComments, postComment, reportComment, deleteComment } from '@/app/actions/comments'

vi.mock('next-auth/react', () => ({ signIn: vi.fn() }))
vi.mock('@/app/actions/comments', () => ({
  getWordComments: vi.fn(),
  postComment: vi.fn(),
  reportComment: vi.fn(),
  deleteComment: vi.fn(),
}))

const comment = (id: string, body: string, extra: Record<string, unknown> = {}) => ({
  id,
  body,
  createdAt: new Date().toISOString(),
  author: { username: 'ana', image: null },
  mine: false,
  reportedByMe: false,
  ...extra,
})

beforeEach(() => vi.clearAllMocks())

describe('[SOC-17] CommentsPanel', () => {
  it('bloqueado: no muestra comentarios', async () => {
    vi.mocked(getWordComments).mockResolvedValue({ locked: true })
    render(<CommentsPanel wordId={1} />)
    expect(await screen.findByText(/después de acertar/i)).toBeInTheDocument()
  })

  it('invitado: lee, y ve invitación a ingresar en lugar del formulario', async () => {
    vi.mocked(getWordComments).mockResolvedValue({ locked: false, comments: [comment('c1', 'Genial')], nextCursor: null, canPost: false, postBlockedReason: 'login_required' })
    render(<CommentsPanel wordId={1} />)
    expect(await screen.findByText('Genial')).toBeInTheDocument()
    expect(screen.getByText(/Ingresá con Google para comentar/)).toBeInTheDocument()
    expect(screen.queryByRole('textbox')).not.toBeInTheDocument()
  })

  it('sin username: ofrece elegirlo', async () => {
    const onNeedUsername = vi.fn()
    vi.mocked(getWordComments).mockResolvedValue({ locked: false, comments: [], nextCursor: null, canPost: false, postBlockedReason: 'username_required' })
    render(<CommentsPanel wordId={1} onNeedUsername={onNeedUsername} />)
    await userEvent.click(await screen.findByText(/Elegí un nombre de usuario/))
    expect(onNeedUsername).toHaveBeenCalled()
  })

  it('publica y lo muestra primero; muestra el error de rate limit', async () => {
    vi.mocked(getWordComments).mockResolvedValue({ locked: false, comments: [comment('c1', 'viejo')], nextCursor: null, canPost: true })
    vi.mocked(postComment)
      .mockResolvedValueOnce({ ok: true, comment: comment('c2', 'nuevo', { mine: true }) })
      .mockResolvedValueOnce({ ok: false, error: 'too_fast' })
    render(<CommentsPanel wordId={7} />)
    const box = await screen.findByRole('textbox')
    await userEvent.type(box, 'nuevo')
    await userEvent.click(screen.getByRole('button', { name: /PUBLICAR/ }))
    await waitFor(() => expect(postComment).toHaveBeenCalledWith(7, 'nuevo'))
    const items = await screen.findAllByRole('listitem')
    expect(items[0]).toHaveTextContent('nuevo')

    await userEvent.type(box, 'otro')
    await userEvent.click(screen.getByRole('button', { name: /PUBLICAR/ }))
    expect(await screen.findByText(/Esperá unos segundos/)).toBeInTheDocument()
  })

  it('muestra HTML como texto plano', async () => {
    vi.mocked(getWordComments).mockResolvedValue({ locked: false, comments: [comment('c1', '<b>negrita</b>')], nextCursor: null, canPost: true })
    const { container } = render(<CommentsPanel wordId={1} />)
    expect(await screen.findByText('<b>negrita</b>')).toBeInTheDocument()
    expect(container.querySelector('b')).toBeNull()
  })

  it('reportar oculta el botón; si queda oculto lo saca; borrar el propio lo saca', async () => {
    vi.mocked(getWordComments).mockResolvedValue({
      locked: false,
      comments: [comment('c1', 'ajeno'), comment('c2', 'otro ajeno'), comment('c3', 'mío', { mine: true })],
      nextCursor: null,
      canPost: true,
    })
    vi.mocked(reportComment).mockResolvedValueOnce({ ok: true, hidden: false }).mockResolvedValueOnce({ ok: true, hidden: true })
    vi.mocked(deleteComment).mockResolvedValue({ ok: true })
    render(<CommentsPanel wordId={1} />)
    await screen.findByText('ajeno')

    const reports = screen.getAllByRole('button', { name: /Reportar comentario/ })
    await userEvent.click(reports[0])
    await waitFor(() => expect(screen.getAllByRole('button', { name: /Reportar comentario/ })).toHaveLength(1))
    await userEvent.click(screen.getByRole('button', { name: /Reportar comentario/ }))
    await waitFor(() => expect(screen.queryByText('otro ajeno')).not.toBeInTheDocument())

    await userEvent.click(screen.getByRole('button', { name: /Borrar comentario/ }))
    await waitFor(() => expect(screen.queryByText('mío')).not.toBeInTheDocument())
  })

  it('pagina con "Ver más"', async () => {
    vi.mocked(getWordComments)
      .mockResolvedValueOnce({ locked: false, comments: [comment('c1', 'uno')], nextCursor: 'c1', canPost: true })
      .mockResolvedValueOnce({ locked: false, comments: [comment('c0', 'cero')], nextCursor: null, canPost: true })
    render(<CommentsPanel wordId={1} />)
    await userEvent.click(await screen.findByText('Ver más comentarios'))
    expect(await screen.findByText('cero')).toBeInTheDocument()
    expect(getWordComments).toHaveBeenLastCalledWith(1, 'c1')
    expect(screen.queryByText('Ver más comentarios')).not.toBeInTheDocument()
  })
})

describe('[SOC-25] CommentsPanel al cambiar de palabra', () => {
  it('descarta la respuesta de la palabra anterior', async () => {
    let resolveOld: (v: unknown) => void = () => {}
    vi.mocked(getWordComments)
      .mockImplementationOnce(() => new Promise((r) => { resolveOld = r }) as never)
      .mockResolvedValueOnce({ locked: false, comments: [comment('n1', 'De la nueva')], nextCursor: null, canPost: false, postBlockedReason: 'login_required' })
    const { rerender } = render(<CommentsPanel wordId={1} />)
    rerender(<CommentsPanel wordId={2} />)
    expect(await screen.findByText('De la nueva')).toBeInTheDocument()
    resolveOld({ locked: false, comments: [comment('o1', 'De la vieja')], nextCursor: null, canPost: false, postBlockedReason: 'login_required' })
    await new Promise((r) => setTimeout(r, 20))
    expect(screen.queryByText('De la vieja')).not.toBeInTheDocument()
    expect(screen.getByText('De la nueva')).toBeInTheDocument()
  })
})
