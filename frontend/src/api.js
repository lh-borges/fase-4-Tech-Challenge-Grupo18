// Camada de acesso à API do SysFeedback. A base pode ser configurada em build
// via VITE_API_BASE; por padrão aponta ao backend local.
const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080'

/** Decodifica o payload de um JWT (sem validar assinatura) para ler role e email. */
export function decodeJwt(token) {
  try {
    const payload = token.split('.')[1]
    const json = decodeURIComponent(
      escape(atob(payload.replace(/-/g, '+').replace(/_/g, '/')))
    )
    return JSON.parse(json)
  } catch {
    return {}
  }
}

async function request(path, { method = 'GET', token, body } = {}) {
  const headers = { 'Content-Type': 'application/json' }
  if (token) headers['Authorization'] = `Bearer ${token}`

  const res = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined
  })

  if (!res.ok) {
    const text = await res.text().catch(() => '')
    const err = new Error(text || `Erro ${res.status}`)
    err.status = res.status
    throw err
  }

  const ct = res.headers.get('content-type') || ''
  return ct.includes('application/json') ? res.json() : res.text()
}

export const api = {
  login: (email, password) =>
    request('/auth/login', { method: 'POST', body: { email, password } }),

  criarAvaliacao: (token, descricao, nota) =>
    request('/avaliacoes', { method: 'POST', token, body: { descricao, nota } }),

  listarAvaliacoes: (token) => request('/avaliacoes', { token })
}
