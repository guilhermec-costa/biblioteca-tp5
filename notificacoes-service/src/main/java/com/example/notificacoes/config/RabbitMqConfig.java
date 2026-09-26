package com.example.notificacoes.config;

import java.util.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String EXCHANGE = "biblioteca.eventos.v1";
    public static final String QUEUE = "notificacoes.emprestimos.v1";
    public static final String DLX = "biblioteca.eventos.dlx";
    public static final String DLQ = "notificacoes.emprestimos.dlq.v1";

    @Bean TopicExchange bibliotecaEventosExchange() { return new TopicExchange(EXCHANGE, true, false); }
    @Bean DirectExchange deadLetterExchange() { return new DirectExchange(DLX, true, false); }

    @Bean
    Queue notificacoesQueue() {
        return new Queue(QUEUE, true, false, false, Map.of(
                "x-dead-letter-exchange", DLX, "x-dead-letter-routing-key", DLQ));
    }

    @Bean Queue notificacoesDeadLetterQueue() { return new Queue(DLQ, true); }

    @Bean
    Binding eventosEmprestimoBinding(Queue notificacoesQueue, TopicExchange bibliotecaEventosExchange) {
        return BindingBuilder.bind(notificacoesQueue).to(bibliotecaEventosExchange)
                .with("biblioteca.emprestimo.*.v1");
    }

    @Bean
    Binding deadLetterBinding(Queue notificacoesDeadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(notificacoesDeadLetterQueue).to(deadLetterExchange).with(DLQ);
    }
}
