package br.com.fiap.SysFeedback.infrastructure.web.controller;

import br.com.fiap.SysFeedback.application.usecase.AvaliacaoCreateUseCase;
import br.com.fiap.SysFeedback.application.usecase.AvaliacaoFindAllUseCase;
import br.com.fiap.SysFeedback.domain.exception.AvaliacaoInvalidaException;
import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.web.handler.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AvaliacaoControllerTest {

    private final AvaliacaoCreateUseCase avaliacaoCreateUseCase = mock(AvaliacaoCreateUseCase.class);
    private final AvaliacaoFindAllUseCase avaliacaoFindAllUseCase = mock(AvaliacaoFindAllUseCase.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AvaliacaoController controller =
                new AvaliacaoController(avaliacaoCreateUseCase, avaliacaoFindAllUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveCriarAvaliacao() throws Exception {
        when(avaliacaoCreateUseCase.execute(any())).thenReturn(Fixture.avaliacaoResponseDTO());

        mockMvc.perform(post("/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Fixture.avaliacaoRequestDTOValida())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(Fixture.AVALIACAO_ID.toString()))
                .andExpect(jsonPath("$.descricao").value(Fixture.DESCRICAO_AVALIACAO))
                .andExpect(jsonPath("$.nota").value(Fixture.NOTA_AVALIACAO))
                .andExpect(jsonPath("$.urgencia").value("BAIXA"));

        verify(avaliacaoCreateUseCase).execute(any());
    }

    @Test
    void deveListarAvaliacoes() throws Exception {
        when(avaliacaoFindAllUseCase.execute()).thenReturn(List.of(Fixture.avaliacaoResponseDTO()));

        mockMvc.perform(get("/avaliacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(Fixture.AVALIACAO_ID.toString()))
                .andExpect(jsonPath("$[0].nota").value(Fixture.NOTA_AVALIACAO));

        verify(avaliacaoFindAllUseCase).execute();
    }

    @Test
    void deveRetornarBadRequestQuandoPayloadForInvalido() throws Exception {
        mockMvc.perform(post("/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Fixture.avaliacaoRequestDTO(" ", 11))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro de validação nos campos enviados"))
                .andExpect(jsonPath("$.fields.descricao").value("Descrição é obrigatória"))
                .andExpect(jsonPath("$.fields.nota").value("Nota máxima é 10"));
    }

    @Test
    void deveRetornarBadRequestQuandoUseCaseLancarExcecaoDeDominio() throws Exception {
        when(avaliacaoCreateUseCase.execute(any()))
                .thenThrow(new AvaliacaoInvalidaException("Nota deve estar entre 0 e 10"));

        mockMvc.perform(post("/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Fixture.avaliacaoRequestDTOValida())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Nota deve estar entre 0 e 10"))
                .andExpect(jsonPath("$.path").value("/avaliacoes"));
    }

    @Test
    void deveRetornarBadRequestQuandoBodyEstiverAusente() throws Exception {
        mockMvc.perform(post("/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Corpo da requisição ausente ou mal formatado"))
                .andExpect(jsonPath("$.path").value("/avaliacoes"));
    }

    @Test
    void deveRetornarBadRequestQuandoJsonForMalformado() throws Exception {
        mockMvc.perform(post("/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Aula boa\","))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Corpo da requisição ausente ou mal formatado"));
    }

    @Test
    void deveRetornarInternalServerErrorQuandoListagemFalhar() throws Exception {
        when(avaliacaoFindAllUseCase.execute()).thenThrow(new RuntimeException("Falha ao listar avaliacoes"));

        mockMvc.perform(get("/avaliacoes"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno inesperado"))
                .andExpect(jsonPath("$.path").value("/avaliacoes"));
    }
}
