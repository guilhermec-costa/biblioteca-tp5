package com.example.biblioteca.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.biblioteca.dto.EmprestimoRequest;
import com.example.biblioteca.event.EmprestimoEvent;
import com.example.biblioteca.event.OutboxService;
import com.example.biblioteca.model.Emprestimo;
import com.example.biblioteca.model.Leitor;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.model.StatusLivro;
import com.example.biblioteca.repository.EmprestimoRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {
    @Mock private EmprestimoRepository emprestimoRepository;
    @Mock private LivroService livroService;
    @Mock private LeitorService leitorService;
    @Mock private OutboxService outboxService;
    @InjectMocks private EmprestimoService emprestimoService;

    @Test
    void deveRegistrarEventoNaOutboxQuandoEmprestimoForRegistrado() {
        Livro livro = new Livro();
        livro.setId(10L);
        livro.setTitulo("Clean Code");
        livro.setStatus(StatusLivro.DISPONIVEL);
        Leitor leitor = new Leitor();
        leitor.setId(20L);
        leitor.setNome("Ana Costa");
        when(livroService.buscarPorId(10L)).thenReturn(livro);
        when(leitorService.buscarPorId(20L)).thenReturn(leitor);
        when(emprestimoRepository.existsByLivroIdAndAtivoTrue(10L)).thenReturn(false);
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(invocation -> {
            Emprestimo salvo = invocation.getArgument(0);
            salvo.setId(30L);
            return salvo;
        });

        Emprestimo emprestimo = emprestimoService.registrarEmprestimo(
                new EmprestimoRequest(10L, 20L, LocalDate.of(2026, 8, 30)));

        ArgumentCaptor<EmprestimoEvent> captor = ArgumentCaptor.forClass(EmprestimoEvent.class);
        verify(outboxService).registrar(eq("biblioteca.emprestimo.registrado.v1"), captor.capture());
        assertThat(emprestimo.isAtivo()).isTrue();
        assertThat(livro.getStatus()).isEqualTo(StatusLivro.EMPRESTADO);
        assertThat(captor.getValue().eventType()).isEqualTo("EMPRESTIMO_REGISTRADO");
        assertThat(captor.getValue().leitorId()).isEqualTo(20L);
        assertThat(captor.getValue().livroTitulo()).isEqualTo("Clean Code");
    }
}
