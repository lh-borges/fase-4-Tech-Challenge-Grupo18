package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.FeedbackResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.Feedback;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FeedbackMapperTest {

    private final FeedbackMapper mapper = Mappers.getMapper(FeedbackMapper.class);

    @Test
    void deveConverterFeedbackParaResponseDTO() {
        Feedback feedback = Fixture.feedback();

        FeedbackResponseDTO response = mapper.toResponse(feedback);

        assertEquals(Fixture.FEEDBACK_ID, response.id());
        assertEquals(Fixture.PERIODO_INICIO, response.periodoInicio());
        assertEquals(Fixture.PERIODO_FIM, response.periodoFim());
        assertEquals(Fixture.MEDIA_NOTAS, response.mediaNotas());
        assertEquals(Fixture.TOTAL_AVALIACOES, response.totalAvaliacoes());
        assertEquals(Fixture.FEEDBACK_GERADO_EM, response.geradoEm());
    }

    @Test
    void deveConverterMapasDeAvaliacoesPorDiaEUrgencia() {
        Feedback feedback = Fixture.feedback();

        FeedbackResponseDTO response = mapper.toResponse(feedback);

        assertEquals(2L, response.avaliacoesPorDia().get(LocalDate.of(2026, 7, 17)));
        assertEquals(2L, response.avaliacoesPorDia().get(LocalDate.of(2026, 7, 18)));
        assertEquals(1L, response.avaliacoesPorUrgencia().get(Urgencia.ALTA));
        assertEquals(1L, response.avaliacoesPorUrgencia().get(Urgencia.MEDIA));
        assertEquals(2L, response.avaliacoesPorUrgencia().get(Urgencia.BAIXA));
    }

    @Test
    void deveRetornarNullQuandoFeedbackForNull() {
        FeedbackResponseDTO response = mapper.toResponse(null);

        assertNull(response);
    }
}
