package com.example.biblioteca.service;

import com.example.biblioteca.dto.LeitorRequest;
import com.example.biblioteca.model.Leitor;
import com.example.biblioteca.repository.EmprestimoRepository;
import com.example.biblioteca.repository.LeitorRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class LeitorService {

    private final LeitorRepository leitorRepository;
    private final EmprestimoRepository emprestimoRepository;

    public LeitorService(LeitorRepository leitorRepository, EmprestimoRepository emprestimoRepository) {
        this.leitorRepository = leitorRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @Transactional(readOnly = true)
    public List<Leitor> listarTodos() {
        return leitorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Leitor buscarPorId(Long id) {
        return leitorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Leitor não encontrado."));
    }

    public Leitor criar(LeitorRequest request) {
        String email = normalizarEmail(request.email());
        validarEmailDuplicado(email, null);

        Leitor leitor = new Leitor();
        leitor.setNome(request.nome());
        leitor.setEmail(email);
        return leitorRepository.save(leitor);
    }

    public Leitor atualizar(Long id, LeitorRequest request) {
        Leitor leitor = buscarPorId(id);
        String email = normalizarEmail(request.email());
        validarEmailDuplicado(email, id);
        leitor.setNome(request.nome());
        leitor.setEmail(email);
        return leitorRepository.save(leitor);
    }

    public void excluir(Long id) {
        if (emprestimoRepository.existsByLeitorIdAndAtivoTrue(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível excluir leitor com empréstimo ativo.");
        }
        leitorRepository.delete(buscarPorId(id));
    }

    private void validarEmailDuplicado(String email, Long idAtual) {
        boolean emailDuplicado = idAtual == null
                ? leitorRepository.existsByEmailIgnoreCase(email)
                : leitorRepository.existsByEmailIgnoreCaseAndIdNot(email, idAtual);

        if (emailDuplicado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um leitor cadastrado com este email.");
        }
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
