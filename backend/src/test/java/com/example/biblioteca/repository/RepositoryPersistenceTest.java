package com.example.biblioteca.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.biblioteca.config.PersistenceConfig;
import com.example.biblioteca.model.Emprestimo;
import com.example.biblioteca.model.Leitor;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.model.StatusLivro;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(PersistenceConfig.class)
@ActiveProfiles("test")
class RepositoryPersistenceTest {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private LeitorRepository leitorRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @BeforeEach
    void limparBase() {
        emprestimoRepository.deleteAllInBatch();
        leitorRepository.deleteAllInBatch();
        livroRepository.deleteAllInBatch();
    }

    @Test
    void devePersistirLivroComAuditoriaERecuperarDisponiveisOrdenados() {
        Livro cleanCode = novoLivro("Clean Code", "Robert C. Martin", "9780132350884", StatusLivro.DISPONIVEL);
        Livro ddd = novoLivro("Domain-Driven Design", "Eric Evans", "9780321125217", StatusLivro.DISPONIVEL);
        Livro emprestado = novoLivro("Refactoring", "Martin Fowler", "9780201485677", StatusLivro.EMPRESTADO);

        livroRepository.save(cleanCode);
        livroRepository.save(ddd);
        livroRepository.save(emprestado);
        livroRepository.flush();

        var disponiveis = livroRepository.findByStatusOrderByTituloAsc(StatusLivro.DISPONIVEL);

        assertThat(disponiveis).extracting(Livro::getTitulo)
                .containsExactly("Clean Code", "Domain-Driven Design");
        assertThat(cleanCode.getCreatedAt()).isNotNull();
        assertThat(cleanCode.getUpdatedAt()).isNotNull();
        assertThat(cleanCode.getVersion()).isZero();
    }

    @Test
    void deveAplicarConsultasDeIntegridadeParaLivroELeitor() {
        livroRepository.saveAndFlush(novoLivro("Clean Architecture", "Robert C. Martin", "9780134494166", StatusLivro.DISPONIVEL));

        Leitor leitor = new Leitor();
        leitor.setNome("Ana Costa");
        leitor.setEmail("ana@biblioteca.local");
        leitorRepository.saveAndFlush(leitor);

        assertThat(livroRepository.existsByIsbnIgnoreCase("9780134494166")).isTrue();
        assertThat(livroRepository.existsByIsbnIgnoreCaseAndIdNot("9780134494166", -1L)).isTrue();
        assertThat(leitorRepository.existsByEmailIgnoreCase("ANA@BIBLIOTECA.LOCAL")).isTrue();
        assertThat(leitorRepository.existsByEmailIgnoreCaseAndIdNot("ana@biblioteca.local", -1L)).isTrue();
    }

    @Test
    void deveListarEmprestimosAtivosComRelacionamentosCarregadosEOrdenados() {
        Livro cleanCode = livroRepository.saveAndFlush(novoLivro("Clean Code", "Robert C. Martin", "9780132350884", StatusLivro.EMPRESTADO));
        Livro ddd = livroRepository.saveAndFlush(novoLivro("Domain-Driven Design", "Eric Evans", "9780321125217", StatusLivro.EMPRESTADO));

        Leitor ana = new Leitor();
        ana.setNome("Ana Costa");
        ana.setEmail("ana@biblioteca.local");
        ana = leitorRepository.saveAndFlush(ana);

        Leitor bruno = new Leitor();
        bruno.setNome("Bruno Lima");
        bruno.setEmail("bruno@biblioteca.local");
        bruno = leitorRepository.saveAndFlush(bruno);

        emprestimoRepository.save(novoEmprestimo(cleanCode, ana, true, LocalDate.now().plusDays(5), null));
        emprestimoRepository.save(novoEmprestimo(ddd, bruno, true, LocalDate.now().plusDays(2), null));
        emprestimoRepository.flush();

        var emprestimosAtivos = emprestimoRepository.findByAtivoTrueOrderByDataPrevistaDevolucaoAsc();

        assertThat(emprestimosAtivos).hasSize(2);
        assertThat(emprestimosAtivos).extracting(e -> e.getLivro().getTitulo())
                .containsExactly("Domain-Driven Design", "Clean Code");
        assertThat(emprestimoRepository.existsByLivroIdAndAtivoTrue(cleanCode.getId())).isTrue();
        assertThat(emprestimoRepository.existsByLeitorIdAndAtivoTrue(ana.getId())).isTrue();
    }

    private Livro novoLivro(String titulo, String autor, String isbn, StatusLivro status) {
        Livro livro = new Livro();
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setIsbn(isbn);
        livro.setStatus(status);
        return livro;
    }

    private Emprestimo novoEmprestimo(
            Livro livro,
            Leitor leitor,
            boolean ativo,
            LocalDate dataPrevistaDevolucao,
            LocalDate dataDevolucao
    ) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLivro(livro);
        emprestimo.setLeitor(leitor);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);
        emprestimo.setDataDevolucao(dataDevolucao);
        emprestimo.setAtivo(ativo);
        return emprestimo;
    }
}
