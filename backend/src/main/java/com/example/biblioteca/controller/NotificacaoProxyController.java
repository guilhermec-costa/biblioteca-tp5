package com.example.biblioteca.controller;

import com.example.biblioteca.integration.NotificacaoClient;
import com.example.biblioteca.integration.NotificacaoResponse;
import com.example.biblioteca.service.LeitorService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Expõe ao frontend os dados cujo domínio e persistência pertencem ao microsserviço. */
@RestController
@RequestMapping("/api/leitores/{leitorId}/notificacoes")
public class NotificacaoProxyController {

    private final LeitorService leitorService;
    private final NotificacaoClient notificacaoClient;

    public NotificacaoProxyController(LeitorService leitorService, NotificacaoClient notificacaoClient) {
        this.leitorService = leitorService;
        this.notificacaoClient = notificacaoClient;
    }

    @GetMapping
    public List<NotificacaoResponse> listarPorLeitor(@PathVariable Long leitorId) {
        leitorService.buscarPorId(leitorId);
        return notificacaoClient.listarPorLeitor(leitorId);
    }
}
