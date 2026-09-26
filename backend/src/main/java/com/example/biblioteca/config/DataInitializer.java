package com.example.biblioteca.config;

import com.example.biblioteca.dto.LeitorRequest;
import com.example.biblioteca.dto.LivroRequest;
import com.example.biblioteca.service.LeitorService;
import com.example.biblioteca.service.LivroService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    CommandLineRunner loadData(LivroService livroService, LeitorService leitorService) {
        return args -> {
            if (livroService.listarTodos().isEmpty()) {
                livroService.criar(new LivroRequest("Clean Code", "Robert C. Martin", "9780132350884"));
                livroService.criar(new LivroRequest("Domain-Driven Design", "Eric Evans", "9780321125217"));
                livroService.criar(new LivroRequest("Refactoring", "Martin Fowler", "9780201485677"));
            }

            if (leitorService.listarTodos().isEmpty()) {
                leitorService.criar(new LeitorRequest("Ana Costa", "ana@biblioteca.local"));
                leitorService.criar(new LeitorRequest("Bruno Lima", "bruno@biblioteca.local"));
            }
        };
    }
}
