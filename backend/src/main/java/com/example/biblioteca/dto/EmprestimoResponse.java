package com.example.biblioteca.dto;

import java.time.LocalDate;

public record EmprestimoResponse(
        Long id,
        Long livroId,
        String livroTitulo,
        Long leitorId,
        String leitorNome,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucao,
        boolean ativo
) {
}
