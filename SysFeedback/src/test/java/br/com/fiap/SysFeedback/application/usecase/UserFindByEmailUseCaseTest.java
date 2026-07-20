package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.domain.exception.UserNotFoundException;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserFindByEmailUseCaseTest {

    private final RepositoryUserPort repository = mock(RepositoryUserPort.class);
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);
    private final UserFindByEmailUseCase useCase = new UserFindByEmailUseCase(repository, mapper);

    @Test
    void deveBuscarUsuarioPorEmail() {
        when(repository.findByEmail(Fixture.USER_EMAIL)).thenReturn(Optional.of(Fixture.user()));

        UserResponseDTO response = useCase.execute(Fixture.USER_EMAIL);

        assertEquals(Fixture.USER_ID, response.id());
        assertEquals(Fixture.USER_EMAIL, response.email());
        verify(repository).findByEmail(Fixture.USER_EMAIL);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {
        when(repository.findByEmail(Fixture.USER_EMAIL)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.execute(Fixture.USER_EMAIL));
    }

    @Test
    void devePropagarExcecaoDoRepositorio() {
        when(repository.findByEmail(Fixture.USER_EMAIL))
                .thenThrow(new RuntimeException("Erro ao buscar usuario por email"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> useCase.execute(Fixture.USER_EMAIL));

        assertEquals("Erro ao buscar usuario por email", exception.getMessage());
        verify(repository).findByEmail(Fixture.USER_EMAIL);
    }
}
