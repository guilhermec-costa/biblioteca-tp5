package com.example.notificacoes.dto;

import com.example.notificacoes.model.TipoNotificacao;
import java.time.LocalDateTime;

public record NotificacaoResponse(
        Long id, Long leitorId, String leitorNome, TipoNotificacao tipo,
        String titulo, String mensagem, LocalDateTime criadaEm
) {
}
