package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.application.security.PasswordEncoderPort;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.exception.EmailAlreadyExistsException;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserCreateUseCaseTest {

    private final RepositoryUserPort repository = mock(RepositoryUserPort.class);
    private final PasswordEncoderPort passwordEncoder = mock(PasswordEncoderPort.class);
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);
    private final UserCreateUseCase useCase = new UserCreateUseCase(repository, passwordEncoder, mapper);

    @Test
    void deveCriarUsuarioComSenhaCriptografada() {
        when(repository.findByEmail(Fixture.USER_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(Fixture.USER_PASSWORD)).thenReturn(Fixture.USER_ENCODED_PASSWORD);
        when(repository.save(any(User.class))).thenReturn(Fixture.userComSenhaCriptografada());

        UserResponseDTO response = useCase.execute(Fixture.userRequestDTOValido());

        assertEquals(Fixture.USER_ID, response.id());
        assertEquals(Fixture.USER_EMAIL, response.email());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());
        assertEquals(Fixture.USER_ENCODED_PASSWORD, userCaptor.getValue().getPassword());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExistir() {
        when(repository.findByEmail(Fixture.USER_EMAIL)).thenReturn(Optional.of(Fixture.user()));

        assertThrows(EmailAlreadyExistsException.class,
                () -> useCase.execute(Fixture.userRequestDTOValido()));

        verify(repository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(Fixture.USER_PASSWORD);
    }

    @Test
    void deveLancarExcecaoQuandoRequestForNull() {
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void deveLancarExcecaoQuandoNomeForNulo() {
        when(repository.findByEmail(Fixture.USER_EMAIL)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(new br.com.fiap.SysFeedback.application.dto.UserRequestDTO(
                        null,
                        Fixture.USER_EMAIL,
                        Fixture.USER_PASSWORD,
                        Fixture.USER_ROLE
                )));

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void devePropagarExcecaoDoRepositorioAoSalvar() {
        when(repository.findByEmail(Fixture.USER_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(Fixture.USER_PASSWORD)).thenReturn(Fixture.USER_ENCODED_PASSWORD);
        when(repository.save(any(User.class))).thenThrow(new RuntimeException("Erro ao salvar usuario"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> useCase.execute(Fixture.userRequestDTOValido()));

        assertEquals("Erro ao salvar usuario", exception.getMessage());
    }
}
