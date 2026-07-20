package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.Feedback;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.FeedbackJpaEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeedbackPersistenceMapperTest {

    private final FeedbackPersistenceMapper mapper = Mappers.getMapper(FeedbackPersistenceMapper.class);

    @Test
    void deveConverterDomainParaJpa() {
        Feedback feedback = Fixture.feedback();

        FeedbackJpaEntity entity = mapper.toJpa(feedback);

        assertEquals(Fixture.FEEDBACK_ID, entity.getId());
        assertEquals(Fixture.PERIODO_INICIO, entity.getPeriodoInicio());
        assertEquals(Fixture.PERIODO_FIM, entity.getPeriodoFim());
        assertEquals(Fixture.MEDIA_NOTAS, entity.getMediaNotas());
        assertEquals(Fixture.TOTAL_AVALIACOES, entity.getTotalAvaliacoes());
        assertEquals(Fixture.FEEDBACK_GERADO_EM, entity.getGeradoEm());
        assertEquals(Fixture.avaliacoesPorDia(), entity.getAvaliacoesPorDia());
        assertEquals(Fixture.avaliacoesPorUrgencia(), entity.getAvaliacoesPorUrgencia());
    }

    @Test
    void deveConverterJpaParaDomain() {
        FeedbackJpaEntity entity = feedbackJpaEntity();

        Feedback feedback = mapper.toDomain(entity);

        assertEquals(Fixture.FEEDBACK_ID, feedback.getId());
        assertEquals(Fixture.PERIODO_INICIO, feedback.getPeriodoInicio());
        assertEquals(Fixture.PERIODO_FIM, feedback.getPeriodoFim());
        assertEquals(Fixture.MEDIA_NOTAS, feedback.getMediaNotas());
        assertEquals(Fixture.TOTAL_AVALIACOES, feedback.getTotalAvaliacoes());
        assertEquals(Fixture.FEEDBACK_GERADO_EM, feedback.getGeradoEm());
        assertEquals(Fixture.avaliacoesPorDia(), feedback.getAvaliacoesPorDia());
        assertEquals(Fixture.avaliacoesPorUrgencia(), feedback.getAvaliacoesPorUrgencia());
    }

    @Test
    void deveCopiarMapasAoConverterDomainParaJpa() {
        Feedback feedback = Fixture.feedback();

        FeedbackJpaEntity entity = mapper.toJpa(feedback);

        assertNotSame(feedback.getAvaliacoesPorDia(), entity.getAvaliacoesPorDia());
        assertNotSame(feedback.getAvaliacoesPorUrgencia(), entity.getAvaliacoesPorUrgencia());
    }

    @Test
    void deveCopiarMapasAoConverterJpaParaDomain() {
        FeedbackJpaEntity entity = feedbackJpaEntity();

        Feedback feedback = mapper.toDomain(entity);

        assertNotSame(entity.getAvaliacoesPorDia(), feedback.getAvaliacoesPorDia());
        assertNotSame(entity.getAvaliacoesPorUrgencia(), feedback.getAvaliacoesPorUrgencia());
    }

    @Test
    void deveRetornarNullQuandoDomainForNull() {
        assertNull(mapper.toJpa(null));
    }

    @Test
    void deveRetornarNullQuandoJpaForNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    void copyMapDeveRetornarMapaVazioQuandoOrigemForNull() {
        LinkedHashMap<String, Long> result = mapper.copyMap(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void copyMapDeveCopiarValoresPreservandoOrdem() {
        Map<LocalDate, Long> source = new LinkedHashMap<>();
        source.put(LocalDate.of(2026, 7, 17), 2L);
        source.put(LocalDate.of(2026, 7, 18), 3L);

        LinkedHashMap<LocalDate, Long> result = mapper.copyMap(source);

        assertEquals(source, result);
        assertNotSame(source, result);
        assertEquals(LocalDate.of(2026, 7, 17), result.keySet().iterator().next());
    }

    private FeedbackJpaEntity feedbackJpaEntity() {
        return new FeedbackJpaEntity(
                Fixture.FEEDBACK_ID,
                Fixture.PERIODO_INICIO,
                Fixture.PERIODO_FIM,
                Fixture.MEDIA_NOTAS,
                Fixture.TOTAL_AVALIACOES,
                Fixture.avaliacoesPorDia(),
                Fixture.avaliacoesPorUrgencia(),
                Fixture.FEEDBACK_GERADO_EM
        );
    }
}
