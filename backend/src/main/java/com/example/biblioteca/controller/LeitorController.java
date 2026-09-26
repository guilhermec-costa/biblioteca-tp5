package com.example.biblioteca.controller;

import com.example.biblioteca.dto.HistoricoResponse;
import com.example.biblioteca.dto.LeitorRequest;
import com.example.biblioteca.dto.LeitorResponse;
import com.example.biblioteca.model.Leitor;
import com.example.biblioteca.service.HistoricoService;
import com.example.biblioteca.service.LeitorService;
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
@RequestMapping("/api/leitores")
public class LeitorController {

    private final LeitorService leitorService;
    private final HistoricoService historicoService;

    public LeitorController(LeitorService leitorService, HistoricoService historicoService) {
        this.leitorService = leitorService;
        this.historicoService = historicoService;
    }

    @GetMapping
    public List<LeitorResponse> listarTodos() {
        return leitorService.listarTodos().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}/historico")
    public List<HistoricoResponse<LeitorResponse>> listarHistorico(@PathVariable Long id) {
        leitorService.buscarPorId(id);
        return historicoService.listarHistoricoLeitores(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeitorResponse criar(@Valid @RequestBody LeitorRequest request) {
        return toResponse(leitorService.criar(request));
    }

    @PutMapping("/{id}")
    public LeitorResponse atualizar(@PathVariable Long id, @Valid @RequestBody LeitorRequest request) {
        return toResponse(leitorService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        leitorService.excluir(id);
    }

    private LeitorResponse toResponse(Leitor leitor) {
        return new LeitorResponse(leitor.getId(), leitor.getNome(), leitor.getEmail());
    }
}
