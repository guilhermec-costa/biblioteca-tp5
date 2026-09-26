# Serviço de Notificações

Microsserviço Spring Boot que consome eventos de empréstimo e devolução publicados no RabbitMQ. Em produção usa um banco PostgreSQL próprio.

A fila durável `notificacoes.emprestimos.v1` recebe as routing keys `biblioteca.emprestimo.*.v1`. O listener tenta cada mensagem até três vezes e encaminha falhas persistentes para `notificacoes.emprestimos.dlq.v1`. O `eventId` único evita notificações duplicadas durante reentregas.

O endpoint `GET /api/notificacoes/leitor/{leitorId}` lista os avisos do leitor. O Actuator fornece health checks e métricas Prometheus, enquanto os traces são enviados ao Tempo por OTLP.

## Testes

```bash
mvn verify
```

Para executar o conjunto completo, use o Docker Compose na raiz do projeto.
