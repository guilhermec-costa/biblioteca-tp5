package com.example.biblioteca.integration;

import java.time.LocalDateTime;

public record NotificacaoResponse(
        Long id,
        Long leitorId,
        String leitorNome,
        String tipo,
        String titulo,
        String mensagem,
        LocalDateTime criadaEm
) {
}
