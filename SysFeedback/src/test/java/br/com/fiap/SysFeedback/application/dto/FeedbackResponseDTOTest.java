package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FeedbackResponseDTOTest {

    @Test
    void deveCriarFeedbackResponseDTOComTodosOsCampos() {
        FeedbackResponseDTO dto = Fixture.feedbackResponseDTO();

        assertEquals(Fixture.FEEDBACK_ID, dto.id());
        assertEquals(Fixture.PERIODO_INICIO, dto.periodoInicio());
        assertEquals(Fixture.PERIODO_FIM, dto.periodoFim());
        assertEquals(Fixture.MEDIA_NOTAS, dto.mediaNotas());
        assertEquals(Fixture.TOTAL_AVALIACOES, dto.totalAvaliacoes());
        assertEquals(2L, dto.avaliacoesPorDia().get(LocalDate.of(2026, 7, 17)));
        assertEquals(2L, dto.avaliacoesPorDia().get(LocalDate.of(2026, 7, 18)));
        assertEquals(1L, dto.avaliacoesPorUrgencia().get(Urgencia.ALTA));
        assertEquals(1L, dto.avaliacoesPorUrgencia().get(Urgencia.MEDIA));
        assertEquals(2L, dto.avaliacoesPorUrgencia().get(Urgencia.BAIXA));
        assertEquals(Fixture.FEEDBACK_GERADO_EM, dto.geradoEm());
    }

    @Test
    void deveCompararRecordsComMesmosValoresComoIguais() {
        FeedbackResponseDTO dto = Fixture.feedbackResponseDTO();
        FeedbackResponseDTO outroDto = Fixture.feedbackResponseDTO();

        assertEquals(dto, outroDto);
        assertEquals(dto.hashCode(), outroDto.hashCode());
    }

    @Test
    void devePermitirCamposNulosPorNaoPossuirValidacaoNoRecord() {
        FeedbackResponseDTO dto = Fixture.feedbackResponseDTOComNulos();

        assertNull(dto.id());
        assertNull(dto.periodoInicio());
        assertNull(dto.periodoFim());
        assertEquals(0.0, dto.mediaNotas());
        assertEquals(0L, dto.totalAvaliacoes());
        assertNull(dto.avaliacoesPorDia());
        assertNull(dto.avaliacoesPorUrgencia());
        assertNull(dto.geradoEm());
    }

    @Test
    void devePermitirValoresNumericosNegativosPorNaoPossuirValidacaoNoResponseDTO() {
        FeedbackResponseDTO dto = new FeedbackResponseDTO(
                Fixture.FEEDBACK_ID,
                Fixture.PERIODO_INICIO,
                Fixture.PERIODO_FIM,
                -1.0,
                -5L,
                Fixture.avaliacoesPorDia(),
                Fixture.avaliacoesPorUrgencia(),
                Fixture.FEEDBACK_GERADO_EM
        );

        assertEquals(-1.0, dto.mediaNotas());
        assertEquals(-5L, dto.totalAvaliacoes());
    }
}
