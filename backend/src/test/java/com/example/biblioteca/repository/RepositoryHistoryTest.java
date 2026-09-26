package com.example.biblioteca.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.biblioteca.config.PersistenceConfig;
import com.example.biblioteca.model.Emprestimo;
import com.example.biblioteca.model.Leitor;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.model.StatusLivro;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata.RevisionType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(PersistenceConfig.class)
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class RepositoryHistoryTest {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private LeitorRepository leitorRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Test
    void deveRegistrarHistoricoDeAlteracoesDoLivro() {
        Livro livro = new Livro();
        livro.setTitulo("Clean Code");
        livro.setAutor("Robert C. Martin");
        livro.setIsbn("9780132350884");
        livro.setStatus(StatusLivro.DISPONIVEL);
        livro = livroRepository.saveAndFlush(livro);

        livro.setStatus(StatusLivro.EMPRESTADO);
        livroRepository.saveAndFlush(livro);

        List<Revision<Integer, Livro>> revisoes = livroRepository.findRevisions(livro.getId()).getContent();

        assertThat(revisoes).hasSize(2);
        assertThat(revisoes).extracting(revisao -> revisao.getMetadata().getRevisionType())
                .containsExactly(RevisionType.INSERT, RevisionType.UPDATE);
        assertThat(revisoes.get(0).getEntity().getStatus()).isEqualTo(StatusLivro.DISPONIVEL);
        assertThat(revisoes.get(1).getEntity().getStatus()).isEqualTo(StatusLivro.EMPRESTADO);
    }

    @Test
    void deveRegistrarHistoricoDeAberturaEFechamentoDoEmprestimo() {
        Livro livro = new Livro();
        livro.setTitulo("Refactoring");
        livro.setAutor("Martin Fowler");
        livro.setIsbn("9780201485677");
        livro.setStatus(StatusLivro.EMPRESTADO);
        livro = livroRepository.saveAndFlush(livro);

        Leitor leitor = new Leitor();
        leitor.setNome("Ana Costa");
        leitor.setEmail("ana@biblioteca.local");
        leitor = leitorRepository.saveAndFlush(leitor);

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLivro(livro);
        emprestimo.setLeitor(leitor);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(7));
        emprestimo.setAtivo(true);
        emprestimo = emprestimoRepository.saveAndFlush(emprestimo);

        emprestimo.setAtivo(false);
        emprestimo.setDataDevolucao(LocalDate.now().plusDays(3));
        emprestimoRepository.saveAndFlush(emprestimo);

        List<Revision<Integer, Emprestimo>> revisoes = emprestimoRepository.findRevisions(emprestimo.getId()).getContent();

        assertThat(revisoes).hasSize(2);
        assertThat(revisoes.get(0).getEntity().isAtivo()).isTrue();
        assertThat(revisoes.get(1).getEntity().isAtivo()).isFalse();
        assertThat(revisoes.get(1).getEntity().getDataDevolucao()).isNotNull();
    }
}
