package com.example.notificacoes.controller;

import com.example.notificacoes.dto.NotificacaoResponse;
import com.example.notificacoes.model.Notificacao;
import com.example.notificacoes.service.NotificacaoService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {
    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping("/leitor/{leitorId}")
    public List<NotificacaoResponse> listarPorLeitor(@PathVariable Long leitorId) {
        return notificacaoService.listarPorLeitor(leitorId).stream().map(this::toResponse).toList();
    }

    private NotificacaoResponse toResponse(Notificacao notificacao) {
        return new NotificacaoResponse(
                notificacao.getId(), notificacao.getLeitorId(), notificacao.getLeitorNome(),
                notificacao.getTipo(), notificacao.getTitulo(), notificacao.getMensagem(), notificacao.getCriadaEm()
        );
    }
}
