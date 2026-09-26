package com.example.biblioteca.service;

import com.example.biblioteca.dto.EmprestimoResponse;
import com.example.biblioteca.dto.HistoricoResponse;
import com.example.biblioteca.dto.LeitorResponse;
import com.example.biblioteca.dto.LivroResponse;
import com.example.biblioteca.model.Emprestimo;
import com.example.biblioteca.model.Leitor;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.repository.EmprestimoRepository;
import com.example.biblioteca.repository.LeitorRepository;
import com.example.biblioteca.repository.LivroRepository;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistoricoService {

    private final LivroRepository livroRepository;
    private final LeitorRepository leitorRepository;
    private final EmprestimoRepository emprestimoRepository;

    public HistoricoService(
            LivroRepository livroRepository,
            LeitorRepository leitorRepository,
            EmprestimoRepository emprestimoRepository
    ) {
        this.livroRepository = livroRepository;
        this.leitorRepository = leitorRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @Transactional(readOnly = true)
    public List<HistoricoResponse<LivroResponse>> listarHistoricoLivros(Long id) {
        return livroRepository.findRevisions(id)
                .getContent()
                .stream()
                .map(revision -> toHistorico(revision, this::toLivroResponse))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HistoricoResponse<LeitorResponse>> listarHistoricoLeitores(Long id) {
        return leitorRepository.findRevisions(id)
                .getContent()
                .stream()
                .map(revision -> toHistorico(revision, this::toLeitorResponse))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HistoricoResponse<EmprestimoResponse>> listarHistoricoEmprestimos(Long id) {
        return emprestimoRepository.findRevisions(id)
                .getContent()
                .stream()
                .map(revision -> toHistorico(revision, this::toEmprestimoResponse))
                .toList();
    }

    private <T, R> HistoricoResponse<R> toHistorico(Revision<Integer, T> revision, Function<T, R> mapper) {
        String tipoOperacao = revision.getMetadata().getRevisionType().name();

        return new HistoricoResponse<>(
                revision.getRequiredRevisionNumber(),
                tipoOperacao,
                revision.getMetadata().getRevisionInstant().orElse(null),
                mapper.apply(revision.getEntity())
        );
    }

    private LivroResponse toLivroResponse(Livro livro) {
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getStatus()
        );
    }

    private LeitorResponse toLeitorResponse(Leitor leitor) {
        return new LeitorResponse(leitor.getId(), leitor.getNome(), leitor.getEmail());
    }

    private EmprestimoResponse toEmprestimoResponse(Emprestimo emprestimo) {
        return new EmprestimoResponse(
                emprestimo.getId(),
                emprestimo.getLivro().getId(),
                emprestimo.getLivro().getTitulo(),
                emprestimo.getLeitor().getId(),
                emprestimo.getLeitor().getNome(),
                emprestimo.getDataEmprestimo(),
                emprestimo.getDataPrevistaDevolucao(),
                emprestimo.getDataDevolucao(),
                emprestimo.isAtivo()
        );
    }
}
