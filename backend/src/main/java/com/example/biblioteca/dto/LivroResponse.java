package com.example.biblioteca.dto;

import com.example.biblioteca.model.StatusLivro;

public record LivroResponse(
        Long id,
        String titulo,
        String autor,
        String isbn,
        StatusLivro status
) {
}
