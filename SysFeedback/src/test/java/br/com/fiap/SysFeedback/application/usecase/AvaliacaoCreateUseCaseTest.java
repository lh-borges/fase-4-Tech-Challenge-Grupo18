package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.AvaliacaoMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.domain.exception.AvaliacaoInvalidaException;
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

class AvaliacaoCreateUseCaseTest {

    private final RepositoryAvaliacaoPort repository = mock(RepositoryAvaliacaoPort.class);
    private final AvaliacaoMapper mapper = Mappers.getMapper(AvaliacaoMapper.class);
    private final AvaliacaoCreateUseCase useCase = new AvaliacaoCreateUseCase(repository, mapper);

    @Test
    void deveCriarAvaliacao() {
        when(repository.save(any(Avaliacao.class))).thenReturn(Fixture.avaliacao());

        AvaliacaoResponseDTO response = useCase.execute(Fixture.avaliacaoRequestDTOValida());

        assertEquals(Fixture.AVALIACAO_ID, response.id());
        assertEquals(Fixture.DESCRICAO_AVALIACAO, response.descricao());
        assertEquals(Fixture.NOTA_AVALIACAO, response.nota());
        assertEquals(Urgencia.BAIXA, response.urgencia());
        verify(repository).save(any(Avaliacao.class));
    }

    @Test
    void deveLancarExcecaoQuandoNotaForInvalida() {
        assertThrows(AvaliacaoInvalidaException.class,
                () -> useCase.execute(Fixture.avaliacaoRequestDTO("Descricao valida", 11)));

        verify(repository, never()).save(any(Avaliacao.class));
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoForVazia() {
        assertThrows(AvaliacaoInvalidaException.class,
                () -> useCase.execute(Fixture.avaliacaoRequestDTO(" ", 8)));

        verify(repository, never()).save(any(Avaliacao.class));
    }

    @Test
    void deveRetornarNullQuandoRequestForNullERepositorioRetornarNull() {
        AvaliacaoResponseDTO response = useCase.execute(null);

        org.junit.jupiter.api.Assertions.assertNull(response);
        verify(repository).save(null);
    }

    @Test
    void devePropagarExcecaoDoRepositorioAoSalvar() {
        when(repository.save(any(Avaliacao.class))).thenThrow(new RuntimeException("Erro ao salvar avaliacao"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> useCase.execute(Fixture.avaliacaoRequestDTOValida()));

        assertEquals("Erro ao salvar avaliacao", exception.getMessage());
    }
}
