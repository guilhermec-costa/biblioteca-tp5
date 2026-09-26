import { useEffect, useState } from 'react';
import { SectionCard } from './components/SectionCard';
import { api } from './services/api';

const emptyLivro = { titulo: '', autor: '', isbn: '' };
const emptyLeitor = { nome: '', email: '' };
const emptyEmprestimo = { livroId: '', leitorId: '', dataPrevistaDevolucao: '' };
const historyInitialState = { type: '', entityId: null, title: '', items: [] };
const notificacoesInitialState = { leitorId: null, leitorNome: '', items: [] };

export default function App() {
  const [livros, setLivros] = useState([]);
  const [leitores, setLeitores] = useState([]);
  const [emprestimos, setEmprestimos] = useState([]);
  const [livroForm, setLivroForm] = useState(emptyLivro);
  const [leitorForm, setLeitorForm] = useState(emptyLeitor);
  const [emprestimoForm, setEmprestimoForm] = useState(emptyEmprestimo);
  const [editingLivroId, setEditingLivroId] = useState(null);
  const [editingLeitorId, setEditingLeitorId] = useState(null);
  const [feedback, setFeedback] = useState('');
  const [loading, setLoading] = useState(true);
  const [historyPanel, setHistoryPanel] = useState(historyInitialState);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [notificacoesPanel, setNotificacoesPanel] = useState(notificacoesInitialState);
  const [notificacoesLoading, setNotificacoesLoading] = useState(false);
  const [activeSection, setActiveSection] = useState('catalogo');

  async function carregarDados() {
    setLoading(true);
    try {
      const [livrosData, leitoresData, emprestimosData] = await Promise.all([
        api.listarLivros(),
        api.listarLeitores(),
        api.listarEmprestimos()
      ]);
      setLivros(livrosData);
      setLeitores(leitoresData);
      setEmprestimos(emprestimosData);
    } catch (error) {
      setFeedback(error.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    carregarDados();
  }, []);

  function handleLivroChange(event) {
    setLivroForm((current) => ({ ...current, [event.target.name]: event.target.value }));
  }

  function handleLeitorChange(event) {
    setLeitorForm((current) => ({ ...current, [event.target.name]: event.target.value }));
  }

  function handleEmprestimoChange(event) {
    setEmprestimoForm((current) => ({ ...current, [event.target.name]: event.target.value }));
  }

  async function abrirHistorico(type, entityId, title) {
    const loaderByType = {
      livro: api.listarHistoricoLivro,
      leitor: api.listarHistoricoLeitor,
      emprestimo: api.listarHistoricoEmprestimo
    };

    try {
      setHistoryLoading(true);
      const items = await loaderByType[type](entityId);
      setHistoryPanel({ type, entityId, title, items });
    } catch (error) {
      setFeedback(error.message);
    } finally {
      setHistoryLoading(false);
    }
  }

  function fecharHistorico() {
    setHistoryPanel(historyInitialState);
  }

  async function abrirNotificacoes(leitor) {
    try {
      setNotificacoesLoading(true);
      const items = await api.listarNotificacoesLeitor(leitor.id);
      setNotificacoesPanel({ leitorId: leitor.id, leitorNome: leitor.nome, items });
    } catch (error) {
      setFeedback(error.message);
    } finally {
      setNotificacoesLoading(false);
    }
  }

  async function salvarLivro(event) {
    event.preventDefault();
    try {
      if (editingLivroId) {
        await api.atualizarLivro(editingLivroId, livroForm);
        setFeedback('Livro atualizado com sucesso.');
      } else {
        await api.criarLivro(livroForm);
        setFeedback('Livro cadastrado com sucesso.');
      }
      setLivroForm(emptyLivro);
      setEditingLivroId(null);
      await carregarDados();
    } catch (error) {
      setFeedback(error.message);
    }
  }

  async function salvarLeitor(event) {
    event.preventDefault();
    try {
      if (editingLeitorId) {
        await api.atualizarLeitor(editingLeitorId, leitorForm);
        setFeedback('Leitor atualizado com sucesso.');
      } else {
        await api.criarLeitor(leitorForm);
        setFeedback('Leitor cadastrado com sucesso.');
      }
      setLeitorForm(emptyLeitor);
      setEditingLeitorId(null);
      await carregarDados();
    } catch (error) {
      setFeedback(error.message);
    }
  }

  async function salvarEmprestimo(event) {
    event.preventDefault();
    try {
      await api.registrarEmprestimo({
        ...emprestimoForm,
        livroId: Number(emprestimoForm.livroId),
        leitorId: Number(emprestimoForm.leitorId)
      });
      setEmprestimoForm(emptyEmprestimo);
      setFeedback('Emprestimo registrado com sucesso.');
      await carregarDados();
    } catch (error) {
      setFeedback(error.message);
    }
  }

  async function excluirLivro(id) {
    try {
      await api.excluirLivro(id);
      setFeedback('Livro removido com sucesso.');
      await carregarDados();
    } catch (error) {
      setFeedback(error.message);
    }
  }

  async function excluirLeitor(id) {
    try {
      await api.excluirLeitor(id);
      setFeedback('Leitor removido com sucesso.');
      await carregarDados();
    } catch (error) {
      setFeedback(error.message);
    }
  }

  async function devolverEmprestimo(id) {
    try {
      await api.registrarDevolucao(id);
      setFeedback('Devolução registrada com sucesso.');
      await carregarDados();
    } catch (error) {
      setFeedback(error.message);
    }
  }

  const livrosDisponiveis = livros.filter((livro) => livro.status === 'DISPONIVEL');
  const totalEmprestimosAtivos = emprestimos.filter((emprestimo) => emprestimo.ativo).length;

  function formatarDataHora(value) {
    if (!value) return 'Sem registro';
    return new Intl.DateTimeFormat('pt-BR', {
      dateStyle: 'short',
      timeStyle: 'short'
    }).format(new Date(value));
  }

  function renderHistorySummary(item) {
    if (historyPanel.type === 'livro') {
      return `${item.dados.titulo} • ${item.dados.status}`;
    }

    if (historyPanel.type === 'leitor') {
      return `${item.dados.nome} • ${item.dados.email}`;
    }

    return `${item.dados.livroTitulo} • ${item.dados.ativo ? 'ATIVO' : 'ENCERRADO'}`;
  }

  return (
    <div className="app-shell">
      <header className="hero">
        <div>
          <h1>Sistema de Biblioteca</h1>
          <p>
            Catálogo, leitores e circulação em uma interface simples para o dia a dia da
            biblioteca.
          </p>
        </div>
        <div className="hero__metrics">
          <div className="hero__badge">
            <strong>{livrosDisponiveis.length}</strong>
            <span>livros disponíveis agora</span>
          </div>
          <div className="hero__badge hero__badge--secondary">
            <strong>{totalEmprestimosAtivos}</strong>
            <span>empréstimos ativos</span>
          </div>
        </div>
      </header>

      {feedback ? <div className="feedback">{feedback}</div> : null}
      {loading ? <div className="feedback">Carregando dados...</div> : null}

      <nav className="workspace-nav" aria-label="Áreas de trabalho">
        <button
          type="button"
          className={activeSection === 'catalogo' ? 'workspace-nav__item is-active' : 'workspace-nav__item'}
          aria-current={activeSection === 'catalogo' ? 'page' : undefined}
          onClick={() => setActiveSection('catalogo')}
        >
          Catálogo
        </button>
        <button
          type="button"
          className={activeSection === 'leitores' ? 'workspace-nav__item is-active' : 'workspace-nav__item'}
          aria-current={activeSection === 'leitores' ? 'page' : undefined}
          onClick={() => setActiveSection('leitores')}
        >
          Leitores
        </button>
        <button
          type="button"
          className={activeSection === 'circulacao' ? 'workspace-nav__item is-active' : 'workspace-nav__item'}
          aria-current={activeSection === 'circulacao' ? 'page' : undefined}
          onClick={() => setActiveSection('circulacao')}
        >
          Circulação
        </button>
      </nav>

      {historyLoading || historyPanel.entityId ? <section className="history-top">
        <SectionCard
          title="Histórico"
          subtitle="Consulte revisões de livros, leitores e empréstimos conforme a operação selecionada."
        >
          {historyLoading ? <div className="feedback">Carregando histórico...</div> : null}

          {!historyLoading && !historyPanel.entityId ? (
            <div className="history-empty">
              <strong>Nenhum histórico aberto.</strong>
              <p>Use os botões “Ver histórico” nos cards abaixo para inspecionar as revisões.</p>
            </div>
          ) : null}

          {!historyLoading && historyPanel.entityId ? (
            <div className="history-panel">
              <div className="history-panel__header">
                <div>
                  <span className="eyebrow">Auditoria</span>
                  <h3>{historyPanel.title}</h3>
                </div>
                <button type="button" className="button-secondary" onClick={fecharHistorico}>
                  Fechar
                </button>
              </div>

              <div className="history-list">
                {historyPanel.items.map((item) => (
                  <article className="history-item" key={`${item.revisao}-${item.tipoOperacao}`}>
                    <div className="history-item__meta">
                      <span className="status status--history">{item.tipoOperacao}</span>
                      <strong>Revisão {item.revisao}</strong>
                      <small>{formatarDataHora(item.dataHora)}</small>
                    </div>
                    <p>{renderHistorySummary(item)}</p>
                    <pre>{JSON.stringify(item.dados, null, 2)}</pre>
                  </article>
                ))}
              </div>
            </div>
          ) : null}
        </SectionCard>
      </section> : null}

      {notificacoesLoading || notificacoesPanel.leitorId ? <section className="notifications-top">
        <SectionCard
          title="Central de notificações"
          subtitle="Avisos criados pelo microsserviço de notificações a cada empréstimo ou devolução."
        >
          {notificacoesLoading ? <div className="feedback">Consultando notificações...</div> : null}
          {!notificacoesLoading && !notificacoesPanel.leitorId ? (
            <div className="history-empty">
              <strong>Nenhum leitor selecionado.</strong>
              <p>Use “Ver notificações” em um leitor para consultar seus avisos.</p>
            </div>
          ) : null}
          {!notificacoesLoading && notificacoesPanel.leitorId ? (
            <div className="notifications-panel">
              <div className="history-panel__header">
                <div>
                  <h3>Avisos de {notificacoesPanel.leitorNome}</h3>
                  <p>{notificacoesPanel.items.length} registro(s) recebido(s) do serviço.</p>
                </div>
                <button type="button" className="button-secondary" onClick={() => setNotificacoesPanel(notificacoesInitialState)}>
                  Fechar
                </button>
              </div>
              {notificacoesPanel.items.length ? (
                <div className="notifications-list">
                  {notificacoesPanel.items.map((notificacao) => (
                    <article className="notification-item" key={notificacao.id}>
                      <div>
                        <strong>{notificacao.titulo}</strong>
                        <p>{notificacao.mensagem}</p>
                      </div>
                      <small>{formatarDataHora(notificacao.criadaEm)}</small>
                    </article>
                  ))}
                </div>
              ) : <p className="notifications-empty">Este leitor ainda não possui notificações.</p>}
            </div>
          ) : null}
        </SectionCard>
      </section> : null}

      <main className="workspace-content">
        <SectionCard
          title="Catálogo"
          subtitle="Cadastre livros, acompanhe a disponibilidade e abra o histórico de cada exemplar."
          hidden={activeSection !== 'catalogo'}
        >
          <form className="form-grid" onSubmit={salvarLivro}>
            <input name="titulo" placeholder="Título" value={livroForm.titulo} onChange={handleLivroChange} required />
            <input name="autor" placeholder="Autor" value={livroForm.autor} onChange={handleLivroChange} required />
            <input name="isbn" placeholder="ISBN" value={livroForm.isbn} onChange={handleLivroChange} required />
            <button type="submit">{editingLivroId ? 'Atualizar livro' : 'Cadastrar livro'}</button>
          </form>

          <div className="list">
            {livros.map((livro) => (
              <article className="list-item" key={livro.id}>
                <div>
                  <strong>{livro.titulo}</strong>
                  <p>{livro.autor}</p>
                  <small>ISBN: {livro.isbn}</small>
                </div>
                <div className="actions">
                  <span className={`status status--${livro.status.toLowerCase()}`}>{livro.status}</span>
                  <button
                    type="button"
                    className="button-secondary"
                    onClick={() => {
                      setLivroForm({
                        titulo: livro.titulo,
                        autor: livro.autor,
                        isbn: livro.isbn
                      });
                      setEditingLivroId(livro.id);
                    }}
                  >
                    Editar
                  </button>
                  <button
                    type="button"
                    className="button-secondary"
                    onClick={() => abrirHistorico('livro', livro.id, `Histórico do livro: ${livro.titulo}`)}
                  >
                    Ver histórico
                  </button>
                  <button type="button" className="button-danger" onClick={() => excluirLivro(livro.id)}>
                    Excluir
                  </button>
                </div>
              </article>
            ))}
          </div>
        </SectionCard>

        <SectionCard
          title="Leitores"
          subtitle="Gerencie os usuários que podem retirar livros e consulte o histórico cadastral."
          hidden={activeSection !== 'leitores'}
        >
          <form className="form-grid" onSubmit={salvarLeitor}>
            <input name="nome" placeholder="Nome" value={leitorForm.nome} onChange={handleLeitorChange} required />
            <input name="email" placeholder="E-mail" value={leitorForm.email} onChange={handleLeitorChange} required />
            <button type="submit">{editingLeitorId ? 'Atualizar leitor' : 'Cadastrar leitor'}</button>
          </form>

          <div className="list">
            {leitores.map((leitor) => (
              <article className="list-item" key={leitor.id}>
                <div>
                  <strong>{leitor.nome}</strong>
                  <p>{leitor.email}</p>
                </div>
                <div className="actions">
                  <button
                    type="button"
                    className="button-secondary"
                    onClick={() => {
                      setLeitorForm({
                        nome: leitor.nome,
                        email: leitor.email
                      });
                      setEditingLeitorId(leitor.id);
                    }}
                  >
                    Editar
                  </button>
                  <button
                    type="button"
                    className="button-secondary"
                    onClick={() => abrirHistorico('leitor', leitor.id, `Histórico do leitor: ${leitor.nome}`)}
                  >
                    Ver histórico
                  </button>
                  <button type="button" className="button-secondary" onClick={() => abrirNotificacoes(leitor)}>
                    Ver notificações
                  </button>
                  <button type="button" className="button-danger" onClick={() => excluirLeitor(leitor.id)}>
                    Excluir
                  </button>
                </div>
              </article>
            ))}
          </div>
        </SectionCard>

        <SectionCard
          title="Circulação"
          subtitle="Registre empréstimos, finalize devoluções e acompanhe o ciclo de cada operação."
          hidden={activeSection !== 'circulacao'}
        >
          <form className="form-grid" onSubmit={salvarEmprestimo}>
            <select name="livroId" value={emprestimoForm.livroId} onChange={handleEmprestimoChange} required>
              <option value="">Selecione um livro</option>
              {livrosDisponiveis.map((livro) => (
                <option key={livro.id} value={livro.id}>
                  {livro.titulo}
                </option>
              ))}
            </select>

            <select name="leitorId" value={emprestimoForm.leitorId} onChange={handleEmprestimoChange} required>
              <option value="">Selecione um leitor</option>
              {leitores.map((leitor) => (
                <option key={leitor.id} value={leitor.id}>
                  {leitor.nome}
                </option>
              ))}
            </select>

            <input
              type="date"
              name="dataPrevistaDevolucao"
              value={emprestimoForm.dataPrevistaDevolucao}
              onChange={handleEmprestimoChange}
              required
            />

            <button type="submit">Registrar empréstimo</button>
          </form>

          <div className="list">
            {emprestimos.map((emprestimo) => (
              <article className="list-item" key={emprestimo.id}>
                <div>
                  <strong>{emprestimo.livroTitulo}</strong>
                  <p>Leitor: {emprestimo.leitorNome}</p>
                  <small>
                    Saída: {emprestimo.dataEmprestimo} | Prevista: {emprestimo.dataPrevistaDevolucao}
                  </small>
                </div>
                <div className="actions">
                  <span className={`status status--${emprestimo.ativo ? 'ativo' : 'encerrado'}`}>
                    {emprestimo.ativo ? 'ATIVO' : 'ENCERRADO'}
                  </span>
                  {emprestimo.ativo ? (
                    <>
                      <button
                        type="button"
                        className="button-secondary"
                        onClick={() => abrirHistorico('emprestimo', emprestimo.id, `Histórico do empréstimo #${emprestimo.id}`)}
                      >
                        Ver histórico
                      </button>
                      <button type="button" className="button-secondary" onClick={() => devolverEmprestimo(emprestimo.id)}>
                        Registrar devolução
                      </button>
                    </>
                  ) : (
                    <>
                      <button
                        type="button"
                        className="button-secondary"
                        onClick={() => abrirHistorico('emprestimo', emprestimo.id, `Histórico do empréstimo #${emprestimo.id}`)}
                      >
                        Ver histórico
                      </button>
                      <small>Devolvido em {emprestimo.dataDevolucao}</small>
                    </>
                  )}
                </div>
              </article>
            ))}
          </div>
        </SectionCard>

      </main>
    </div>
  );
}
