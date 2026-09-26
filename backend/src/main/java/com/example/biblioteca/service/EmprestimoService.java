package com.example.biblioteca.service;

import com.example.biblioteca.dto.EmprestimoRequest;
import com.example.biblioteca.event.EmprestimoEventFactory;
import com.example.biblioteca.event.OutboxService;
import com.example.biblioteca.model.Emprestimo;
import com.example.biblioteca.model.Leitor;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.model.StatusLivro;
import com.example.biblioteca.repository.EmprestimoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroService livroService;
    private final LeitorService leitorService;
    private final OutboxService outboxService;

    public EmprestimoService(
            EmprestimoRepository emprestimoRepository,
            LivroService livroService,
            LeitorService leitorService,
            OutboxService outboxService
    ) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroService = livroService;
        this.leitorService = leitorService;
        this.outboxService = outboxService;
    }

    @Transactional(readOnly = true)
    public List<Emprestimo> listarTodos() {
        return emprestimoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Emprestimo> listarAtivos() {
        return emprestimoRepository.findByAtivoTrueOrderByDataPrevistaDevolucaoAsc();
    }

    @Transactional(readOnly = true)
    public Emprestimo buscarPorId(Long id) {
        return emprestimoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empréstimo não encontrado."));
    }

    public Emprestimo registrarEmprestimo(EmprestimoRequest request) {
        Livro livro = livroService.buscarPorId(request.livroId());
        Leitor leitor = leitorService.buscarPorId(request.leitorId());

        if (livro.getStatus() == StatusLivro.EMPRESTADO || emprestimoRepository.existsByLivroIdAndAtivoTrue(livro.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Livro indisponível para empréstimo.");
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLivro(livro);
        emprestimo.setLeitor(leitor);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataPrevistaDevolucao(request.dataPrevistaDevolucao());
        emprestimo.setAtivo(true);

        livro.setStatus(StatusLivro.EMPRESTADO);
        livroService.salvar(livro);

        Emprestimo salvo = emprestimoRepository.save(emprestimo);
        outboxService.registrar(
                "biblioteca.emprestimo.registrado.v1",
                EmprestimoEventFactory.emprestimoRegistrado(salvo)
        );
        return salvo;
    }

    public Emprestimo registrarDevolucao(Long id) {
        Emprestimo emprestimo = buscarPorId(id);
        if (!emprestimo.isAtivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este empréstimo já foi finalizado.");
        }

        emprestimo.setAtivo(false);
        emprestimo.setDataDevolucao(LocalDate.now());

        Livro livro = emprestimo.getLivro();
        livro.setStatus(StatusLivro.DISPONIVEL);
        livroService.salvar(livro);

        Emprestimo salvo = emprestimoRepository.save(emprestimo);
        outboxService.registrar(
                "biblioteca.emprestimo.devolvido.v1",
                EmprestimoEventFactory.devolucaoRegistrada(salvo)
        );
        return salvo;
    }
}
