package com.example.biblioteca.dto;

import java.time.Instant;

public record HistoricoResponse<T>(
        Integer revisao,
        String tipoOperacao,
        Instant dataHora,
        T dados
) {
}
