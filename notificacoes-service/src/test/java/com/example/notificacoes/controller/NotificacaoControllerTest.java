package com.example.notificacoes.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.notificacoes.model.Notificacao;
import com.example.notificacoes.model.TipoNotificacao;
import com.example.notificacoes.service.NotificacaoService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class NotificacaoControllerTest {

    @Test
    void deveListarNotificacoesDoLeitorPelaApi() throws Exception {
        NotificacaoService service = Mockito.mock(NotificacaoService.class);
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new NotificacaoController(service))
                .build();
        Notificacao notificacao = new Notificacao();
        notificacao.setLeitorId(20L);
        notificacao.setLeitorNome("Ana Costa");
        notificacao.setTipo(TipoNotificacao.EMPRESTIMO_REGISTRADO);
        notificacao.setTitulo("Empréstimo registrado");
        notificacao.setMensagem("Mensagem");
        notificacao.setCriadaEm(LocalDateTime.of(2027, 1, 10, 12, 0));
        when(service.listarPorLeitor(20L)).thenReturn(List.of(notificacao));

        mockMvc.perform(get("/api/notificacoes/leitor/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].leitorId").value(20))
                .andExpect(jsonPath("$[0].tipo").value("EMPRESTIMO_REGISTRADO"));
    }
}
