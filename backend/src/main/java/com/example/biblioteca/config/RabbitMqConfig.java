package com.example.biblioteca.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String EXCHANGE = "biblioteca.eventos.v1";
    @Bean
    TopicExchange bibliotecaEventosExchange() { return new TopicExchange(EXCHANGE, true, false); }
}
