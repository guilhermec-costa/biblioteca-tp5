# Backend da Biblioteca

API Spring Boot responsável por catálogo, leitores e circulação. Em produção usa PostgreSQL; no desenvolvimento e nos testes isolados pode usar H2.

Empréstimos e devoluções gravam eventos na tabela `event_outbox` na mesma transação dos dados de negócio. O `OutboxPublisher` envia as pendências para o TopicExchange `biblioteca.eventos.v1` com confirmação do RabbitMQ. A consulta HTTP ao serviço de notificações permanece somente para leitura.

O serviço expõe as probes do Actuator e métricas Prometheus. Traces são enviados por OTLP e os logs carregam `traceId` e `spanId`.

## Testes

```bash
mvn verify
```

Para executar o conjunto completo, use o Docker Compose na raiz do projeto.
