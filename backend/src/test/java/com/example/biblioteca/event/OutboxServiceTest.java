package com.example.biblioteca.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class OutboxServiceTest {

    @Test
    void deveSerializarEPersistirEventoPendente() throws Exception {
        OutboxEventRepository repository = Mockito.mock(OutboxEventRepository.class);
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        OutboxService service = new OutboxService(repository, objectMapper);
        UUID eventId = UUID.randomUUID();
        EmprestimoEvent event = new EmprestimoEvent(
                eventId, "EMPRESTIMO_REGISTRADO", 1, OffsetDateTime.now(),
                30L, 20L, "Ana Costa", 10L, "Clean Code",
                LocalDate.of(2027, 12, 31), null);

        service.registrar("biblioteca.emprestimo.registrado.v1", event);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(eventId);
        assertThat(captor.getValue().getStatus()).isEqualTo(OutboxStatus.PENDENTE);
        assertThat(captor.getValue().getPayload()).contains("EMPRESTIMO_REGISTRADO", "Ana Costa");
    }
}
