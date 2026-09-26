package com.example.notificacoes.event;

import static org.mockito.Mockito.verify;

import com.example.notificacoes.service.NotificacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class EmprestimoEventListenerTest {

    @Test
    void deveConverterJsonEEncaminharEventoAoServico() throws Exception {
        NotificacaoService service = Mockito.mock(NotificacaoService.class);
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        EmprestimoEventListener listener = new EmprestimoEventListener(objectMapper, service);
        UUID eventId = UUID.randomUUID();
        EmprestimoEvent event = new EmprestimoEvent(
                eventId, "EMPRESTIMO_REGISTRADO", 1, OffsetDateTime.now(),
                30L, 20L, "Ana Costa", 10L, "Clean Code",
                LocalDate.of(2027, 1, 20), null);

        listener.consumir(objectMapper.writeValueAsString(event));

        ArgumentCaptor<EmprestimoEvent> captor = ArgumentCaptor.forClass(EmprestimoEvent.class);
        verify(service).processar(captor.capture());
        org.assertj.core.api.Assertions.assertThat(captor.getValue().eventId()).isEqualTo(eventId);
    }
}
