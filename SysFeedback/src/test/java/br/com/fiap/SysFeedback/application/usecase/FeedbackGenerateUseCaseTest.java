package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.FeedbackResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.FeedbackMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryFeedbackPort;
import br.com.fiap.SysFeedback.domain.entity.Feedback;
import br.com.fiap.SysFeedback.domain.exception.PeriodoInvalidoException;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FeedbackGenerateUseCaseTest {

    private final RepositoryAvaliacaoPort avaliacaoRepository = mock(RepositoryAvaliacaoPort.class);
    private final RepositoryFeedbackPort feedbackRepository = mock(RepositoryFeedbackPort.class);
    private final FeedbackMapper mapper = Mappers.getMapper(FeedbackMapper.class);
    private final FeedbackGenerateUseCase useCase =
            new FeedbackGenerateUseCase(avaliacaoRepository, feedbackRepository, mapper);

    @Test
    void deveGerarFeedbackDoPeriodo() {
        when(avaliacaoRepository.findByPeriodo(Fixture.PERIODO_INICIO, Fixture.PERIODO_FIM))
                .thenReturn(Fixture.avaliacoes());
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(Fixture.feedback());

        FeedbackResponseDTO response = useCase.execute(Fixture.feedbackRequestDTOValido());

        assertEquals(Fixture.FEEDBACK_ID, response.id());
        assertEquals(Fixture.MEDIA_NOTAS, response.mediaNotas());
        assertEquals(Fixture.TOTAL_AVALIACOES, response.totalAvaliacoes());
        verify(avaliacaoRepository).findByPeriodo(Fixture.PERIODO_INICIO, Fixture.PERIODO_FIM);
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void deveLancarExcecaoQuandoPeriodoForInvalido() {
        assertThrows(PeriodoInvalidoException.class,
                () -> useCase.execute(Fixture.feedbackRequestDTO(Fixture.PERIODO_FIM, Fixture.PERIODO_INICIO)));

        verify(feedbackRepository, never()).save(any(Feedback.class));
    }

    @Test
    void deveGerarFeedbackVazioQuandoNaoExistiremAvaliacoesNoPeriodo() {
        when(avaliacaoRepository.findByPeriodo(Fixture.PERIODO_INICIO, Fixture.PERIODO_FIM))
                .thenReturn(java.util.List.of());
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FeedbackResponseDTO response = useCase.execute(Fixture.feedbackRequestDTOValido());

        assertEquals(0L, response.totalAvaliacoes());
        assertEquals(0.0, response.mediaNotas());
    }

    @Test
    void deveLancarExcecaoQuandoInicioForNulo() {
        assertThrows(PeriodoInvalidoException.class,
                () -> useCase.execute(Fixture.feedbackRequestDTO(null, Fixture.PERIODO_FIM)));

        verify(feedbackRepository, never()).save(any(Feedback.class));
    }

    @Test
    void devePropagarExcecaoDoRepositorioDeAvaliacoes() {
        when(avaliacaoRepository.findByPeriodo(Fixture.PERIODO_INICIO, Fixture.PERIODO_FIM))
                .thenThrow(new RuntimeException("Erro ao buscar avaliacoes do periodo"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> useCase.execute(Fixture.feedbackRequestDTOValido()));

        assertEquals("Erro ao buscar avaliacoes do periodo", exception.getMessage());
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }
}
