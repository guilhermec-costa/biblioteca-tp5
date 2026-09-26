package com.example.biblioteca.integration;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "notificacoes-service", url = "${notificacoes.service.url}")
public interface NotificacaoClient {

    @GetMapping("/api/notificacoes/leitor/{leitorId}")
    List<NotificacaoResponse> listarPorLeitor(@PathVariable Long leitorId);
}
