package com.example.biblioteca.event;

import com.example.biblioteca.model.Emprestimo;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public final class EmprestimoEventFactory {
    private EmprestimoEventFactory() {}

    public static EmprestimoEvent emprestimoRegistrado(Emprestimo emprestimo) {
        return criar("EMPRESTIMO_REGISTRADO", emprestimo);
    }

    public static EmprestimoEvent devolucaoRegistrada(Emprestimo emprestimo) {
        return criar("DEVOLUCAO_REGISTRADA", emprestimo);
    }

    private static EmprestimoEvent criar(String tipo, Emprestimo emprestimo) {
        return new EmprestimoEvent(
                UUID.randomUUID(), tipo, 1, OffsetDateTime.now(ZoneOffset.UTC),
                emprestimo.getId(), emprestimo.getLeitor().getId(), emprestimo.getLeitor().getNome(),
                emprestimo.getLivro().getId(), emprestimo.getLivro().getTitulo(),
                emprestimo.getDataPrevistaDevolucao(), emprestimo.getDataDevolucao()
        );
    }
}
