package com.example.notificacoes.service;

import com.example.notificacoes.event.EmprestimoEvent;
import com.example.notificacoes.model.Notificacao;
import com.example.notificacoes.model.TipoNotificacao;
import com.example.notificacoes.repository.NotificacaoRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificacaoService {
    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public void processar(EmprestimoEvent event) {
        if (notificacaoRepository.existsByEventId(event.eventId())) {
            return;
        }

        TipoNotificacao tipo = TipoNotificacao.valueOf(event.eventType());
        Notificacao notificacao = new Notificacao();
        notificacao.setEventId(event.eventId());
        notificacao.setLeitorId(event.leitorId());
        notificacao.setLeitorNome(event.leitorNome());
        notificacao.setTipo(tipo);
        notificacao.setTitulo(tipo == TipoNotificacao.EMPRESTIMO_REGISTRADO
                ? "Empréstimo registrado" : "Devolução registrada");
        notificacao.setMensagem(criarMensagem(tipo, event));
        notificacao.setCriadaEm(LocalDateTime.now());
        notificacaoRepository.save(notificacao);
    }

    private String criarMensagem(TipoNotificacao tipo, EmprestimoEvent event) {
        if (tipo == TipoNotificacao.EMPRESTIMO_REGISTRADO) {
            return "O livro '" + event.livroTitulo() + "' deve ser devolvido até "
                    + event.dataPrevistaDevolucao() + ".";
        }
        return "A devolução do livro '" + event.livroTitulo() + "' foi registrada com sucesso.";
    }

    @Transactional(readOnly = true)
    public List<Notificacao> listarPorLeitor(Long leitorId) {
        return notificacaoRepository.findByLeitorIdOrderByCriadaEmDesc(leitorId);
    }
}
