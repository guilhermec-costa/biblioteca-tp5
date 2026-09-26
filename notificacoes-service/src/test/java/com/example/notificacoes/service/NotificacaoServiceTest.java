package com.example.notificacoes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.notificacoes.event.EmprestimoEvent;
import com.example.notificacoes.model.Notificacao;
import com.example.notificacoes.repository.NotificacaoRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {
    @Mock private NotificacaoRepository notificacaoRepository;
    @InjectMocks private NotificacaoService notificacaoService;

    @Test
    void devePersistirNotificacaoRecebidaPorEvento() {
        UUID eventId = UUID.randomUUID();
        when(notificacaoRepository.existsByEventId(eventId)).thenReturn(false);
        EmprestimoEvent event = evento(eventId);

        notificacaoService.processar(event);

        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(notificacaoRepository).save(captor.capture());
        assertThat(captor.getValue().getEventId()).isEqualTo(eventId);
        assertThat(captor.getValue().getCriadaEm()).isNotNull();
        assertThat(captor.getValue().getMensagem()).contains("Clean Code", "2026-08-30");
    }

    @Test
    void deveIgnorarEventoDuplicado() {
        UUID eventId = UUID.randomUUID();
        when(notificacaoRepository.existsByEventId(eventId)).thenReturn(true);

        notificacaoService.processar(evento(eventId));

        verify(notificacaoRepository, never()).save(any());
    }

    private EmprestimoEvent evento(UUID eventId) {
        return new EmprestimoEvent(eventId, "EMPRESTIMO_REGISTRADO", 1, OffsetDateTime.now(),
                30L, 20L, "Ana Costa", 10L, "Clean Code", LocalDate.of(2026, 8, 30), null);
    }
}
