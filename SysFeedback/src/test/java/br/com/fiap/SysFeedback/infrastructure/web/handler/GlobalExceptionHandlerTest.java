package br.com.fiap.SysFeedback.infrastructure.web.handler;

import br.com.fiap.SysFeedback.domain.exception.UnauthorizedOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestErrorController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveRetornarForbiddenParaOperacaoNaoAutorizada() throws Exception {
        mockMvc.perform(get("/test-error/forbidden").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Operação não autorizada"))
                .andExpect(jsonPath("$.path").value("/test-error/forbidden"));
    }

    @Test
    void deveRetornarInternalServerErrorParaErroNaoTratado() throws Exception {
        mockMvc.perform(get("/test-error/generic").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Erro interno inesperado"))
                .andExpect(jsonPath("$.path").value("/test-error/generic"));
    }

    @RestController
    static class TestErrorController {

        @GetMapping("/test-error/forbidden")
        void forbidden() {
            throw new UnauthorizedOperationException("Operação não autorizada");
        }

        @GetMapping("/test-error/generic")
        void generic() {
            throw new RuntimeException("Falha inesperada");
        }
    }
}
