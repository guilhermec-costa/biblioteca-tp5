package com.example.notificacoes.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.notificacoes.model.Notificacao;
import com.example.notificacoes.model.TipoNotificacao;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class NotificacaoRepositoryTest {
    @Autowired private NotificacaoRepository notificacaoRepository;

    @Test
    void deveRetornarApenasNotificacoesDoLeitorEmOrdemDecrescente() {
        notificacaoRepository.save(novaNotificacao(1L, LocalDateTime.of(2026, 8, 10, 9, 0)));
        notificacaoRepository.save(novaNotificacao(1L, LocalDateTime.of(2026, 8, 11, 9, 0)));
        notificacaoRepository.save(novaNotificacao(2L, LocalDateTime.of(2026, 8, 12, 9, 0)));
        var resultado = notificacaoRepository.findByLeitorIdOrderByCriadaEmDesc(1L);
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Notificacao::getCriadaEm)
                .containsExactly(LocalDateTime.of(2026, 8, 11, 9, 0), LocalDateTime.of(2026, 8, 10, 9, 0));
    }

    private Notificacao novaNotificacao(Long leitorId, LocalDateTime criadaEm) {
        Notificacao notificacao = new Notificacao();
        notificacao.setLeitorId(leitorId);
        notificacao.setLeitorNome("Ana Costa");
        notificacao.setTipo(TipoNotificacao.EMPRESTIMO_REGISTRADO);
        notificacao.setTitulo("Empréstimo registrado");
        notificacao.setMensagem("Mensagem de teste");
        notificacao.setCriadaEm(criadaEm);
        return notificacao;
    }
}
