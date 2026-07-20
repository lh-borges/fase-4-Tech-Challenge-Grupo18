package br.com.fiap.SysFeedback.domain.entity;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.domain.exception.PeriodoInvalidoException;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeedbackTest {

    @Test
    void deveGerarFeedbackComMediaTotalEContagens() {
        Feedback feedback = Feedback.gerar(
                Fixture.PERIODO_INICIO,
                Fixture.PERIODO_FIM,
                Fixture.avaliacoes()
        );

        assertEquals(Fixture.PERIODO_INICIO, feedback.getPeriodoInicio());
        assertEquals(Fixture.PERIODO_FIM, feedback.getPeriodoFim());
        assertEquals(2L, feedback.getTotalAvaliacoes());
        assertEquals(5.5, feedback.getMediaNotas());
        assertEquals(1L, feedback.getAvaliacoesPorDia().get(LocalDate.of(2026, 7, 18)));
        assertEquals(1L, feedback.getAvaliacoesPorDia().get(LocalDate.of(2026, 7, 17)));
        assertEquals(1L, feedback.getAvaliacoesPorUrgencia().get(Urgencia.BAIXA));
        assertEquals(1L, feedback.getAvaliacoesPorUrgencia().get(Urgencia.ALTA));
        assertNotNull(feedback.getGeradoEm());
    }

    @Test
    void deveGerarFeedbackVazioQuandoListaNaoTemAvaliacoes() {
        Feedback feedback = Feedback.gerar(Fixture.PERIODO_INICIO, Fixture.PERIODO_FIM, List.of());

        assertEquals(0L, feedback.getTotalAvaliacoes());
        assertEquals(0.0, feedback.getMediaNotas());
        assertEquals(0, feedback.getAvaliacoesPorDia().size());
        assertEquals(0, feedback.getAvaliacoesPorUrgencia().size());
    }

    @Test
    void deveReconstruirFeedbackComTodosOsCampos() {
        Feedback feedback = Fixture.feedback();

        assertEquals(Fixture.FEEDBACK_ID, feedback.getId());
        assertEquals(Fixture.PERIODO_INICIO, feedback.getPeriodoInicio());
        assertEquals(Fixture.PERIODO_FIM, feedback.getPeriodoFim());
        assertEquals(Fixture.MEDIA_NOTAS, feedback.getMediaNotas());
        assertEquals(Fixture.TOTAL_AVALIACOES, feedback.getTotalAvaliacoes());
        assertEquals(Fixture.FEEDBACK_GERADO_EM, feedback.getGeradoEm());
    }

    @Test
    void deveAlterarId() {
        Feedback feedback = Feedback.gerar(Fixture.PERIODO_INICIO, Fixture.PERIODO_FIM, List.of());

        feedback.setId(Fixture.FEEDBACK_ID);

        assertEquals(Fixture.FEEDBACK_ID, feedback.getId());
    }

    @Test
    void deveLancarExcecaoQuandoInicioOuFimForemNulos() {
        assertThrows(PeriodoInvalidoException.class,
                () -> Feedback.gerar(null, Fixture.PERIODO_FIM, List.of()));
        assertThrows(PeriodoInvalidoException.class,
                () -> Feedback.gerar(Fixture.PERIODO_INICIO, null, List.of()));
    }

    @Test
    void deveLancarExcecaoQuandoFimForAnteriorAoInicio() {
        assertThrows(PeriodoInvalidoException.class,
                () -> Feedback.gerar(Fixture.PERIODO_FIM, Fixture.PERIODO_INICIO, List.of()));
    }
}
