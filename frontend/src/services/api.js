const API_URL = import.meta.env.VITE_API_URL || (import.meta.env.DEV
  ? 'http://localhost:8080/api'
  : '/api');

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    ...options
  });

  if (!response.ok) {
    let message = 'Não foi possível concluir a requisição.';
    try {
      const data = await response.json();
      message = data.message || message;
    } catch {
      message = response.statusText || message;
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export const api = {
  listarLivros: () => request('/livros'),
  listarLivrosDisponiveis: () => request('/livros/disponiveis'),
  listarHistoricoLivro: (id) => request(`/livros/${id}/historico`),
  criarLivro: (payload) => request('/livros', { method: 'POST', body: JSON.stringify(payload) }),
  atualizarLivro: (id, payload) => request(`/livros/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  excluirLivro: (id) => request(`/livros/${id}`, { method: 'DELETE' }),

  listarLeitores: () => request('/leitores'),
  listarNotificacoesLeitor: (id) => request(`/leitores/${id}/notificacoes`),
  listarHistoricoLeitor: (id) => request(`/leitores/${id}/historico`),
  criarLeitor: (payload) => request('/leitores', { method: 'POST', body: JSON.stringify(payload) }),
  atualizarLeitor: (id, payload) => request(`/leitores/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  excluirLeitor: (id) => request(`/leitores/${id}`, { method: 'DELETE' }),

  listarEmprestimos: () => request('/emprestimos'),
  listarHistoricoEmprestimo: (id) => request(`/emprestimos/${id}/historico`),
  registrarEmprestimo: (payload) => request('/emprestimos', { method: 'POST', body: JSON.stringify(payload) }),
  registrarDevolucao: (id) => request(`/emprestimos/${id}/devolucao`, { method: 'POST' })
};
