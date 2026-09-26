package com.example.biblioteca.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.biblioteca.model.Emprestimo;
import com.example.biblioteca.model.Leitor;
import com.example.biblioteca.model.Livro;
import com.example.biblioteca.service.EmprestimoService;
import com.example.biblioteca.service.HistoricoService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class EmprestimoControllerTest {

    @Test
    void deveRegistrarEmprestimoPelaApi() throws Exception {
        EmprestimoService emprestimoService = Mockito.mock(EmprestimoService.class);
        HistoricoService historicoService = Mockito.mock(HistoricoService.class);
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new EmprestimoController(emprestimoService, historicoService))
                .build();

        Livro livro = new Livro();
        livro.setId(10L);
        livro.setTitulo("Clean Code");
        Leitor leitor = new Leitor();
        leitor.setId(20L);
        leitor.setNome("Ana Costa");
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(30L);
        emprestimo.setLivro(livro);
        emprestimo.setLeitor(leitor);
        emprestimo.setDataEmprestimo(LocalDate.of(2027, 1, 10));
        emprestimo.setDataPrevistaDevolucao(LocalDate.of(2027, 1, 20));
        emprestimo.setAtivo(true);
        when(emprestimoService.registrarEmprestimo(any())).thenReturn(emprestimo);

        mockMvc.perform(post("/api/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "livroId": 10,
                                  "leitorId": 20,
                                  "dataPrevistaDevolucao": "2027-01-20"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(30))
                .andExpect(jsonPath("$.livroTitulo").value("Clean Code"))
                .andExpect(jsonPath("$.leitorNome").value("Ana Costa"))
                .andExpect(jsonPath("$.ativo").value(true));
    }
}
