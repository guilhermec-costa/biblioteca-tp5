package com.example.biblioteca.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_outbox")
public class OutboxEvent {
    @Id private UUID id;
    @Column(nullable = false) private String routingKey;
    @Column(nullable = false, length = 4000) private String payload;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OutboxStatus status;
    @Column(nullable = false) private int tentativas;
    @Column(nullable = false, updatable = false) private LocalDateTime criadoEm;
    private LocalDateTime publicadoEm;
    @Column(length = 1000) private String ultimoErro;

    protected OutboxEvent() {}

    public OutboxEvent(UUID id, String routingKey, String payload) {
        this.id = id;
        this.routingKey = routingKey;
        this.payload = payload;
        this.status = OutboxStatus.PENDENTE;
        this.criadoEm = LocalDateTime.now();
    }

    public void marcarPublicado() {
        status = OutboxStatus.PUBLICADO;
        publicadoEm = LocalDateTime.now();
        ultimoErro = null;
    }

    public void registrarFalha(String erro) {
        tentativas++;
        ultimoErro = erro == null ? "Falha sem detalhe" : erro.substring(0, Math.min(erro.length(), 1000));
    }

    public UUID getId() { return id; }
    public String getRoutingKey() { return routingKey; }
    public String getPayload() { return payload; }
    public OutboxStatus getStatus() { return status; }
    public int getTentativas() { return tentativas; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getPublicadoEm() { return publicadoEm; }
    public String getUltimoErro() { return ultimoErro; }
}
