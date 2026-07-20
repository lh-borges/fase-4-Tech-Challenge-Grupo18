package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.application.security.PasswordEncoderPort;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.enums.Role;
import br.com.fiap.SysFeedback.domain.exception.UnauthorizedOperationException;
import br.com.fiap.SysFeedback.domain.exception.UserNotFoundException;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserUpdateUseCaseTest {

    private final RepositoryUserPort repository = mock(RepositoryUserPort.class);
    private final PasswordEncoderPort passwordEncoder = mock(PasswordEncoderPort.class);
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);
    private final UserUpdateUseCase useCase = new UserUpdateUseCase(repository, passwordEncoder, mapper);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveAtualizarProprioUsuarioMantendoRoleAtual() {
        autenticar(Fixture.USER_EMAIL, Role.ALUNO);
        when(repository.findById(Fixture.USER_ID)).thenReturn(Optional.of(Fixture.user()));
        when(passwordEncoder.encode(Fixture.USER_PASSWORD)).thenReturn(Fixture.USER_ENCODED_PASSWORD);
        when(repository.save(any(User.class))).thenReturn(Fixture.userComSenhaCriptografada());

        UserResponseDTO response = useCase.execute(
                Fixture.USER_ID,
                Fixture.userUpdateDTO("Aluno Atualizado", Fixture.USER_EMAIL, Fixture.USER_PASSWORD, Role.ADMIN)
        );

        assertEquals(Fixture.USER_ID, response.id());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());
        assertEquals(Role.ALUNO, userCaptor.getValue().getRole());
    }

    @Test
    void devePermitirAdminAlterarRole() {
        autenticar("admin@fiap.com", Role.ADMIN);
        when(repository.findById(Fixture.USER_ID)).thenReturn(Optional.of(Fixture.user()));
        when(passwordEncoder.encode(Fixture.USER_PASSWORD)).thenReturn(Fixture.USER_ENCODED_PASSWORD);
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(
                Fixture.USER_ID,
                Fixture.userUpdateDTO(Fixture.USER_NAME, Fixture.USER_EMAIL, Fixture.USER_PASSWORD, Role.PROFESSOR)
        );

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());
        assertEquals(Role.PROFESSOR, userCaptor.getValue().getRole());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        autenticar(Fixture.USER_EMAIL, Role.ALUNO);
        when(repository.findById(Fixture.USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> useCase.execute(Fixture.USER_ID, Fixture.userUpdateDTOValido()));

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioComumTentarAlterarOutroUsuario() {
        autenticar("outro@fiap.com", Role.ALUNO);
        when(repository.findById(Fixture.USER_ID)).thenReturn(Optional.of(Fixture.user()));

        assertThrows(UnauthorizedOperationException.class,
                () -> useCase.execute(Fixture.USER_ID, Fixture.userUpdateDTOValido()));

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void deveLancarExcecaoQuandoNaoHouverUsuarioAutenticado() {
        when(repository.findById(Fixture.USER_ID)).thenReturn(Optional.of(Fixture.user()));

        assertThrows(NullPointerException.class,
                () -> useCase.execute(Fixture.USER_ID, Fixture.userUpdateDTOValido()));

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void deveLancarExcecaoQuandoDTOForNull() {
        autenticar(Fixture.USER_EMAIL, Role.ALUNO);
        when(repository.findById(Fixture.USER_ID)).thenReturn(Optional.of(Fixture.user()));

        assertThrows(NullPointerException.class,
                () -> useCase.execute(Fixture.USER_ID, null));

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void devePropagarExcecaoDoRepositorioAoSalvar() {
        autenticar(Fixture.USER_EMAIL, Role.ALUNO);
        when(repository.findById(Fixture.USER_ID)).thenReturn(Optional.of(Fixture.user()));
        when(passwordEncoder.encode(Fixture.USER_PASSWORD)).thenReturn(Fixture.USER_ENCODED_PASSWORD);
        when(repository.save(any(User.class))).thenThrow(new RuntimeException("Erro ao atualizar usuario"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> useCase.execute(Fixture.USER_ID, Fixture.userUpdateDTOValido()));

        assertEquals("Erro ao atualizar usuario", exception.getMessage());
    }

    private void autenticar(String email, Role role) {
        var authentication = new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
