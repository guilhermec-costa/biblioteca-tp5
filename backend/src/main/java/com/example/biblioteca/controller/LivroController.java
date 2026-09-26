package com.example.biblioteca.controller;

import com.example.biblioteca.dto.HistoricoResponse;
import com.example.biblioteca.dto.LivroRequest;
import com.example.biblioteca.dto.LivroResponse;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.service.HistoricoService;
import com.example.biblioteca.service.LivroService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/livros")
public class LivroController {

    private final LivroService livroService;
    private final HistoricoService historicoService;

    public LivroController(LivroService livroService, HistoricoService historicoService) {
        this.livroService = livroService;
        this.historicoService = historicoService;
    }

    @GetMapping
    public List<LivroResponse> listarTodos() {
        return livroService.listarTodos().stream().map(this::toResponse).toList();
    }

    @GetMapping("/disponiveis")
    public List<LivroResponse> listarDisponiveis() {
        return livroService.listarDisponiveis().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}/historico")
    public List<HistoricoResponse<LivroResponse>> listarHistorico(@PathVariable Long id) {
        livroService.buscarPorId(id);
        return historicoService.listarHistoricoLivros(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LivroResponse criar(@Valid @RequestBody LivroRequest request) {
        return toResponse(livroService.criar(request));
    }

    @PutMapping("/{id}")
    public LivroResponse atualizar(@PathVariable Long id, @Valid @RequestBody LivroRequest request) {
        return toResponse(livroService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        livroService.excluir(id);
    }

    private LivroResponse toResponse(Livro livro) {
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getStatus()
        );
    }
}
