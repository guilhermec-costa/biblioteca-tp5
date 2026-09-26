package com.example.biblioteca.service;

import com.example.biblioteca.dto.LivroRequest;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.model.StatusLivro;
import com.example.biblioteca.repository.LivroRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class LivroService {

    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Transactional(readOnly = true)
    public List<Livro> listarTodos() {
        return livroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Livro> listarDisponiveis() {
        return livroRepository.findByStatusOrderByTituloAsc(StatusLivro.DISPONIVEL);
    }

    @Transactional(readOnly = true)
    public Livro buscarPorId(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado."));
    }

    public Livro criar(LivroRequest request) {
        String isbn = normalizarIsbn(request.isbn());
        validarIsbnDuplicado(isbn, null);

        Livro livro = new Livro();
        livro.setTitulo(request.titulo());
        livro.setAutor(request.autor());
        livro.setIsbn(isbn);
        livro.setStatus(StatusLivro.DISPONIVEL);
        return livroRepository.save(livro);
    }

    public Livro atualizar(Long id, LivroRequest request) {
        Livro livro = buscarPorId(id);
        String isbn = normalizarIsbn(request.isbn());
        validarIsbnDuplicado(isbn, id);
        livro.setTitulo(request.titulo());
        livro.setAutor(request.autor());
        livro.setIsbn(isbn);
        return livroRepository.save(livro);
    }

    public void excluir(Long id) {
        Livro livro = buscarPorId(id);
        if (livro.getStatus() == StatusLivro.EMPRESTADO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível excluir um livro emprestado.");
        }
        livroRepository.delete(livro);
    }

    public Livro salvar(Livro livro) {
        return livroRepository.save(livro);
    }

    private void validarIsbnDuplicado(String isbn, Long idAtual) {
        boolean isbnDuplicado = idAtual == null
                ? livroRepository.existsByIsbnIgnoreCase(isbn)
                : livroRepository.existsByIsbnIgnoreCaseAndIdNot(isbn, idAtual);

        if (isbnDuplicado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um livro cadastrado com este ISBN.");
        }
    }

    private String normalizarIsbn(String isbn) {
        return isbn == null ? null : isbn.trim();
    }
}
