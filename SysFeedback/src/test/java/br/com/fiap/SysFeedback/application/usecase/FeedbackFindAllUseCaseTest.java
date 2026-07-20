package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.FeedbackResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.FeedbackMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryFeedbackPort;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FeedbackFindAllUseCaseTest {

    private final RepositoryFeedbackPort repository = mock(RepositoryFeedbackPort.class);
    private final FeedbackMapper mapper = Mappers.getMapper(FeedbackMapper.class);
    private final FeedbackFindAllUseCase useCase = new FeedbackFindAllUseCase(repository, mapper);

    @Test
    void deveListarTodosOsFeedbacks() {
        when(repository.findAll()).thenReturn(Fixture.feedbacks());

        List<FeedbackResponseDTO> response = useCase.execute();

        assertEquals(1, response.size());
        assertEquals(Fixture.FEEDBACK_ID, response.getFirst().id());
        verify(repository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremFeedbacks() {
        when(repository.findAll()).thenReturn(List.of());

        List<FeedbackResponseDTO> response = useCase.execute();

        assertEquals(0, response.size());
    }

    @Test
    void devePropagarExcecaoDoRepositorio() {
        RuntimeException erro = new RuntimeException("Erro ao buscar feedbacks");
        when(repository.findAll()).thenThrow(erro);

        RuntimeException exception = assertThrows(RuntimeException.class, useCase::execute);

        assertEquals("Erro ao buscar feedbacks", exception.getMessage());
        verify(repository).findAll();
    }
}
