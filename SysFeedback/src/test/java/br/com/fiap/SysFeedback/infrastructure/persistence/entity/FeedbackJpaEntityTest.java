package br.com.fiap.SysFeedback.infrastructure.persistence.entity;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class FeedbackJpaEntityTest {

    @Test
    void deveCriarComConstrutorCompleto() {
        Map<LocalDate, Long> porDia = Fixture.avaliacoesPorDia();
        Map<Urgencia, Long> porUrgencia = Fixture.avaliacoesPorUrgencia();

        FeedbackJpaEntity entity = new FeedbackJpaEntity(
                Fixture.FEEDBACK_ID,
                Fixture.PERIODO_INICIO,
                Fixture.PERIODO_FIM,
                Fixture.MEDIA_NOTAS,
                Fixture.TOTAL_AVALIACOES,
                porDia,
                porUrgencia,
                Fixture.FEEDBACK_GERADO_EM
        );

        assertEquals(Fixture.FEEDBACK_ID, entity.getId());
        assertEquals(Fixture.PERIODO_INICIO, entity.getPeriodoInicio());
        assertEquals(Fixture.PERIODO_FIM, entity.getPeriodoFim());
        assertEquals(Fixture.MEDIA_NOTAS, entity.getMediaNotas());
        assertEquals(Fixture.TOTAL_AVALIACOES, entity.getTotalAvaliacoes());
        assertEquals(porDia, entity.getAvaliacoesPorDia());
        assertEquals(porUrgencia, entity.getAvaliacoesPorUrgencia());
        assertEquals(Fixture.FEEDBACK_GERADO_EM, entity.getGeradoEm());
    }

    @Test
    void construtorVazioDeveInicializarMapasVazios() {
        FeedbackJpaEntity entity = new FeedbackJpaEntity();

        assertNull(entity.getId());
        assertNull(entity.getPeriodoInicio());
        assertNull(entity.getPeriodoFim());
        assertEquals(0.0, entity.getMediaNotas());
        assertEquals(0L, entity.getTotalAvaliacoes());
        assertNotNull(entity.getAvaliacoesPorDia());
        assertNotNull(entity.getAvaliacoesPorUrgencia());
        assertEquals(Map.of(), entity.getAvaliacoesPorDia());
        assertEquals(Map.of(), entity.getAvaliacoesPorUrgencia());
        assertNull(entity.getGeradoEm());
    }

    @Test
    void devePermitirAlterarMapasComSetters() {
        FeedbackJpaEntity entity = new FeedbackJpaEntity();
        Map<LocalDate, Long> porDia = new LinkedHashMap<>();
        porDia.put(LocalDate.of(2026, 7, 19), 3L);
        Map<Urgencia, Long> porUrgencia = new LinkedHashMap<>();
        porUrgencia.put(Urgencia.MEDIA, 3L);

        entity.setAvaliacoesPorDia(porDia);
        entity.setAvaliacoesPorUrgencia(porUrgencia);

        assertEquals(porDia, entity.getAvaliacoesPorDia());
        assertEquals(porUrgencia, entity.getAvaliacoesPorUrgencia());
    }
}
