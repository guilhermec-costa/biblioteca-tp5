package com.example.notificacoes.event;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record EmprestimoEvent(
        UUID eventId, String eventType, int eventVersion, OffsetDateTime occurredAt,
        Long emprestimoId, Long leitorId, String leitorNome, Long livroId, String livroTitulo,
        LocalDate dataPrevistaDevolucao, LocalDate dataDevolucao
) {}
