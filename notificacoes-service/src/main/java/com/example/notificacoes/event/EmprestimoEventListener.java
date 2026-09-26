package com.example.notificacoes.event;

import com.example.notificacoes.config.RabbitMqConfig;
import com.example.notificacoes.service.NotificacaoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmprestimoEventListener {
    private final ObjectMapper objectMapper;
    private final NotificacaoService notificacaoService;

    public EmprestimoEventListener(ObjectMapper objectMapper, NotificacaoService notificacaoService) {
        this.objectMapper = objectMapper;
        this.notificacaoService = notificacaoService;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE)
    public void consumir(String payload) throws JsonProcessingException {
        notificacaoService.processar(objectMapper.readValue(payload, EmprestimoEvent.class));
    }
}
