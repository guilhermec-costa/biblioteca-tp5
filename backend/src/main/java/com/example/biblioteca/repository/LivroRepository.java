package com.example.biblioteca.repository;

import com.example.biblioteca.model.Livro;
import com.example.biblioteca.model.StatusLivro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface LivroRepository extends JpaRepository<Livro, Long>, RevisionRepository<Livro, Long, Integer> {
    List<Livro> findByStatusOrderByTituloAsc(StatusLivro status);

    List<Livro> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrderByTituloAsc(String titulo, String autor);

    boolean existsByIsbnIgnoreCase(String isbn);

    boolean existsByIsbnIgnoreCaseAndIdNot(String isbn, Long id);
}
