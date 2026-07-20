package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.exception.UserNotFoundException;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDeleteUseCaseTest {

    private final RepositoryUserPort repository = mock(RepositoryUserPort.class);
    private final UserDeleteUseCase useCase = new UserDeleteUseCase(repository);

    @Test
    void deveExcluirUsuarioExistente() {
        User user = Fixture.user();
        when(repository.findById(Fixture.USER_ID)).thenReturn(Optional.of(user));

        useCase.execute(Fixture.USER_ID);

        verify(repository).delete(user);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        when(repository.findById(Fixture.USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.execute(Fixture.USER_ID));

        verify(repository, never()).delete(any(User.class));
    }

    @Test
    void devePropagarExcecaoQuandoRepositorioFalharAoExcluir() {
        User user = Fixture.user();
        when(repository.findById(Fixture.USER_ID)).thenReturn(Optional.of(user));
        org.mockito.Mockito.doThrow(new RuntimeException("Erro ao excluir usuario"))
                .when(repository).delete(user);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> useCase.execute(Fixture.USER_ID));

        org.junit.jupiter.api.Assertions.assertEquals("Erro ao excluir usuario", exception.getMessage());
        verify(repository).delete(user);
    }
}
