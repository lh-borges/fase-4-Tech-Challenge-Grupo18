package br.com.fiap.SysFeedback.infrastructure.persistence.adapter;

import br.com.fiap.SysFeedback.domain.entity.Feedback;
import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.mapper.FeedbackPersistenceMapper;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.FeedbackJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.FeedbackJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FeedbackRepositoryAdapterTest {

    private final FeedbackJpaRepository feedbackJpaRepository = mock(FeedbackJpaRepository.class);
    private final FeedbackPersistenceMapper feedbackPersistenceMapper = mock(FeedbackPersistenceMapper.class);

    private FeedbackRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FeedbackRepositoryAdapter(feedbackJpaRepository, feedbackPersistenceMapper);
    }

    @Test
    void deveSalvarFeedbackMapeandoDomainParaJpaEDepoisParaDomain() {
        Feedback feedback = Fixture.feedback();
        FeedbackJpaEntity entity = feedbackJpaEntity();

        when(feedbackPersistenceMapper.toJpa(feedback)).thenReturn(entity);
        when(feedbackJpaRepository.save(entity)).thenReturn(entity);
        when(feedbackPersistenceMapper.toDomain(entity)).thenReturn(feedback);

        Feedback result = adapter.save(feedback);

        assertEquals(feedback, result);
        verify(feedbackPersistenceMapper).toJpa(feedback);
        verify(feedbackJpaRepository).save(entity);
        verify(feedbackPersistenceMapper).toDomain(entity);
    }

    @Test
    void deveListarTodosFeedbacks() {
        Feedback feedback = Fixture.feedback();
        FeedbackJpaEntity entity = feedbackJpaEntity();
        when(feedbackJpaRepository.findAll()).thenReturn(List.of(entity));
        when(feedbackPersistenceMapper.toDomain(entity)).thenReturn(feedback);

        List<Feedback> result = adapter.findAll();

        assertEquals(List.of(feedback), result);
        verify(feedbackJpaRepository).findAll();
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
