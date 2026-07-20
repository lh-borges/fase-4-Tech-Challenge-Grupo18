package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserFindAllUseCaseTest {

    private final RepositoryUserPort repository = mock(RepositoryUserPort.class);
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);
    private final UserFindAllUseCase useCase = new UserFindAllUseCase(repository, mapper);

    @Test
    void deveListarTodosOsUsuarios() {
        when(repository.findAll()).thenReturn(Fixture.users());

        List<UserResponseDTO> response = useCase.execute();

        assertEquals(2, response.size());
        assertEquals(Fixture.USER_ID, response.getFirst().id());
        verify(repository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremUsuarios() {
        when(repository.findAll()).thenReturn(List.of());

        List<UserResponseDTO> response = useCase.execute();

        assertEquals(0, response.size());
    }

    @Test
    void devePropagarExcecaoDoRepositorio() {
        RuntimeException erro = new RuntimeException("Erro ao buscar usuarios");
        when(repository.findAll()).thenThrow(erro);

        RuntimeException exception = assertThrows(RuntimeException.class, useCase::execute);

        assertEquals("Erro ao buscar usuarios", exception.getMessage());
        verify(repository).findAll();
    }
}
