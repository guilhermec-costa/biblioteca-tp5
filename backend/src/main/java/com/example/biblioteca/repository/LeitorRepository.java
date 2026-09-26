package com.example.biblioteca.repository;

import com.example.biblioteca.model.Leitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface LeitorRepository extends JpaRepository<Leitor, Long>, RevisionRepository<Leitor, Long, Integer> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
