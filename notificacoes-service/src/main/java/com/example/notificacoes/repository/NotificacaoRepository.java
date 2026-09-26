package com.example.notificacoes.repository;

import com.example.notificacoes.model.Notificacao;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    boolean existsByEventId(UUID eventId);
    List<Notificacao> findByLeitorIdOrderByCriadaEmDesc(Long leitorId);
}
