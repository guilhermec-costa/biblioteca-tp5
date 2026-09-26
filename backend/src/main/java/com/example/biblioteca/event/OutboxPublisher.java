package com.example.biblioteca.event;

import com.example.biblioteca.config.RabbitMqConfig;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {
    private final OutboxEventRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public OutboxPublisher(OutboxEventRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelayString = "${biblioteca.outbox.fixed-delay:1000}")
    @Transactional
    public void publicarPendentes() {
        repository.findTop50ByStatusOrderByCriadoEmAsc(OutboxStatus.PENDENTE).forEach(this::publicar);
    }

    private void publicar(OutboxEvent event) {
        try {
            Message message = MessageBuilder.withBody(event.getPayload().getBytes(StandardCharsets.UTF_8))
                    .setContentType("application/json")
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setMessageId(event.getId().toString())
                    .setHeader("eventId", event.getId().toString())
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();
            CorrelationData correlation = new CorrelationData(event.getId().toString());
            rabbitTemplate.send(RabbitMqConfig.EXCHANGE, event.getRoutingKey(), message, correlation);
            CorrelationData.Confirm confirm = correlation.getFuture().get(5, TimeUnit.SECONDS);
            if (!confirm.isAck()) {
                throw new IllegalStateException("RabbitMQ recusou o evento: " + confirm.getReason());
            }
            event.marcarPublicado();
        } catch (Exception exception) {
            event.registrarFalha(exception.getMessage());
        }
    }
}
