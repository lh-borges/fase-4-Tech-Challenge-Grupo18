package br.com.fiap.SysFeedback.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa a proteção por API key do endpoint interno de relatório semanal.
 * Usa o seed (perfil de teste, H2) para ter dados no período.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RelatorioInternoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void semApiKeyRetorna401() throws Exception {
        mockMvc.perform(post("/internal/relatorio/semanal"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void comApiKeyInvalidaRetorna401() throws Exception {
        mockMvc.perform(post("/internal/relatorio/semanal")
                        .header("X-Internal-Api-Key", "errada"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void comApiKeyValidaRetornaRelatorio() throws Exception {
        mockMvc.perform(post("/internal/relatorio/semanal")
                        .header("X-Internal-Api-Key", "chave-de-teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mediaNotas").exists())
                .andExpect(jsonPath("$.avaliacoesPorUrgencia").exists())
                .andExpect(jsonPath("$.avaliacoes").exists());
    }
}
