package br.com.fiap.SysFeedback.domain.exception;

import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainExceptionTest {

    @Test
    void deveCriarUserNotFoundExceptionPorEmail() {
        UserNotFoundException exception = new UserNotFoundException(Fixture.USER_EMAIL);

        assertEquals("User not found with email: " + Fixture.USER_EMAIL, exception.getMessage());
    }

    @Test
    void deveCriarUserNotFoundExceptionPorId() {
        UUID id = Fixture.USER_ID;

        UserNotFoundException exception = new UserNotFoundException(id);

        assertEquals("User not found with id: " + id, exception.getMessage());
    }

    @Test
    void deveCriarAvaliacaoInvalidaException() {
        AvaliacaoInvalidaException exception =
                new AvaliacaoInvalidaException("Descrição não pode ser vazia");

        assertEquals("Descrição não pode ser vazia", exception.getMessage());
    }

    @Test
    void deveCriarPeriodoInvalidoException() {
        PeriodoInvalidoException exception =
                new PeriodoInvalidoException("Data fim não pode ser anterior à data início");

        assertEquals("Data fim não pode ser anterior à data início", exception.getMessage());
    }

    @Test
    void deveCriarEmailAlreadyExistsException() {
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException(Fixture.USER_EMAIL);

        assertEquals("Email already exists: " + Fixture.USER_EMAIL, exception.getMessage());
    }

    @Test
    void deveCriarUnauthorizedOperationException() {
        UnauthorizedOperationException exception =
                new UnauthorizedOperationException("Operação não permitida");

        assertEquals("Operação não permitida", exception.getMessage());
    }
}
