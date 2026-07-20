package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.AvaliacaoMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AvaliacaoFindAllUseCaseTest {

    private final RepositoryAvaliacaoPort repository = mock(RepositoryAvaliacaoPort.class);
    private final AvaliacaoMapper mapper = Mappers.getMapper(AvaliacaoMapper.class);
    private final AvaliacaoFindAllUseCase useCase = new AvaliacaoFindAllUseCase(repository, mapper);

    @Test
    void deveListarTodasAsAvaliacoes() {
        when(repository.findAll()).thenReturn(Fixture.avaliacoes());

        List<AvaliacaoResponseDTO> response = useCase.execute();

        assertEquals(2, response.size());
        assertEquals(Fixture.AVALIACAO_ID, response.getFirst().id());
        verify(repository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremAvaliacoes() {
        when(repository.findAll()).thenReturn(List.of());

        List<AvaliacaoResponseDTO> response = useCase.execute();

        assertEquals(0, response.size());
    }

    @Test
    void devePropagarExcecaoDoRepositorio() {
        RuntimeException erro = new RuntimeException("Erro ao buscar avaliacoes");
        when(repository.findAll()).thenThrow(erro);

        RuntimeException exception = assertThrows(RuntimeException.class, useCase::execute);

        assertEquals("Erro ao buscar avaliacoes", exception.getMessage());
        verify(repository).findAll();
    }
}
