package com.example.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;

public record LivroRequest(
        @NotBlank String titulo,
        @NotBlank String autor,
        @NotBlank String isbn
) {
}
