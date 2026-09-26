package com.example.biblioteca.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EmprestimoRequest(
        @NotNull Long livroId,
        @NotNull Long leitorId,
        @NotNull @Future LocalDate dataPrevistaDevolucao
) {
}
