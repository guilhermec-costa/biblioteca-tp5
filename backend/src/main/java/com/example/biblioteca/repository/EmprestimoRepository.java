package com.example.biblioteca.repository;

import com.example.biblioteca.model.Emprestimo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.history.RevisionRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long>, RevisionRepository<Emprestimo, Long, Integer> {
    @Override
    @EntityGraph(attributePaths = {"livro", "leitor"})
    List<Emprestimo> findAll();

    @Override
    @EntityGraph(attributePaths = {"livro", "leitor"})
    Optional<Emprestimo> findById(Long id);

    @EntityGraph(attributePaths = {"livro", "leitor"})
    List<Emprestimo> findByAtivoTrueOrderByDataPrevistaDevolucaoAsc();

    boolean existsByLeitorIdAndAtivoTrue(Long leitorId);

    boolean existsByLivroIdAndAtivoTrue(Long livroId);
}
