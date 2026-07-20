package br.com.fiap.SysFeedback.infrastructure.web.controller;

import br.com.fiap.SysFeedback.application.usecase.FeedbackFindAllUseCase;
import br.com.fiap.SysFeedback.application.usecase.FeedbackGenerateUseCase;
import br.com.fiap.SysFeedback.domain.exception.PeriodoInvalidoException;
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

class FeedbackControllerTest {

    private final FeedbackGenerateUseCase feedbackGenerateUseCase = mock(FeedbackGenerateUseCase.class);
    private final FeedbackFindAllUseCase feedbackFindAllUseCase = mock(FeedbackFindAllUseCase.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        FeedbackController controller =
                new FeedbackController(feedbackGenerateUseCase, feedbackFindAllUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveGerarFeedback() throws Exception {
        when(feedbackGenerateUseCase.execute(any())).thenReturn(Fixture.feedbackResponseDTO());

        mockMvc.perform(post("/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Fixture.feedbackRequestDTOValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(Fixture.FEEDBACK_ID.toString()))
                .andExpect(jsonPath("$.mediaNotas").value(Fixture.MEDIA_NOTAS))
                .andExpect(jsonPath("$.totalAvaliacoes").value(Fixture.TOTAL_AVALIACOES));

        verify(feedbackGenerateUseCase).execute(any());
    }

    @Test
    void deveListarFeedbacks() throws Exception {
        when(feedbackFindAllUseCase.execute()).thenReturn(List.of(Fixture.feedbackResponseDTO()));

        mockMvc.perform(get("/feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(Fixture.FEEDBACK_ID.toString()))
                .andExpect(jsonPath("$[0].totalAvaliacoes").value(Fixture.TOTAL_AVALIACOES));

        verify(feedbackFindAllUseCase).execute();
    }

    @Test
    void deveRetornarBadRequestQuandoPeriodoForNulo() throws Exception {
        mockMvc.perform(post("/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Fixture.feedbackRequestDTO(null, null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro de validação nos campos enviados"))
                .andExpect(jsonPath("$.fields.inicio").value("Data de início é obrigatória"))
                .andExpect(jsonPath("$.fields.fim").value("Data de fim é obrigatória"));
    }

    @Test
    void deveRetornarBadRequestQuandoUseCaseLancarPeriodoInvalido() throws Exception {
        when(feedbackGenerateUseCase.execute(any()))
                .thenThrow(new PeriodoInvalidoException("Data fim não pode ser anterior à data início"));

        mockMvc.perform(post("/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Fixture.feedbackRequestDTOValido())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Data fim não pode ser anterior à data início"));
    }

    @Test
    void deveRetornarBadRequestQuandoBodyEstiverAusente() throws Exception {
        mockMvc.perform(post("/feedback")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Corpo da requisição ausente ou mal formatado"))
                .andExpect(jsonPath("$.path").value("/feedback"));
    }

    @Test
    void deveRetornarBadRequestQuandoJsonForMalformado() throws Exception {
        mockMvc.perform(post("/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inicio\":\"2026-07-01T00:00:00\","))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Corpo da requisição ausente ou mal formatado"));
    }

    @Test
    void deveRetornarInternalServerErrorQuandoListagemFalhar() throws Exception {
        when(feedbackFindAllUseCase.execute()).thenThrow(new RuntimeException("Falha ao listar feedbacks"));

        mockMvc.perform(get("/feedback"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno inesperado"))
                .andExpect(jsonPath("$.path").value("/feedback"));
    }
}
