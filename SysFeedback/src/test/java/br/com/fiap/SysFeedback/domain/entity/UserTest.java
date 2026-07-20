package br.com.fiap.SysFeedback.domain.entity;

import br.com.fiap.SysFeedback.domain.enums.Role;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    void deveCriarUsuarioNovoComCreatedAtAutomatico() {
        User user = new User(
                Fixture.USER_NAME,
                Fixture.USER_EMAIL,
                Fixture.USER_PASSWORD,
                Fixture.USER_ROLE
        );

        assertEquals(Fixture.USER_NAME, user.getName());
        assertEquals(Fixture.USER_EMAIL, user.getEmail());
        assertEquals(Fixture.USER_PASSWORD, user.getPassword());
        assertEquals(Fixture.USER_ROLE, user.getRole());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void deveReconstruirUsuarioComTodosOsCampos() {
        User user = Fixture.user();

        assertEquals(Fixture.USER_ID, user.getId());
        assertEquals(Fixture.USER_NAME, user.getName());
        assertEquals(Fixture.USER_EMAIL, user.getEmail());
        assertEquals(Fixture.USER_PASSWORD, user.getPassword());
        assertEquals(Fixture.USER_ROLE, user.getRole());
        assertEquals(Fixture.USER_CREATED_AT, user.getCreatedAt());
    }

    @Test
    void deveAlterarIdESenha() {
        User user = new User(Fixture.USER_NAME, Fixture.USER_EMAIL, Fixture.USER_PASSWORD, Fixture.USER_ROLE);

        user.setId(Fixture.USER_ID);
        user.setPassword(Fixture.USER_ENCODED_PASSWORD);

        assertEquals(Fixture.USER_ID, user.getId());
        assertEquals(Fixture.USER_ENCODED_PASSWORD, user.getPassword());
    }

    @Test
    void deveLancarExcecaoQuandoNomeForNuloOuVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new User(null, Fixture.USER_EMAIL, Fixture.USER_PASSWORD, Fixture.USER_ROLE));
        assertThrows(IllegalArgumentException.class,
                () -> new User(" ", Fixture.USER_EMAIL, Fixture.USER_PASSWORD, Fixture.USER_ROLE));
    }

    @Test
    void deveLancarExcecaoQuandoEmailForNuloOuVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new User(Fixture.USER_NAME, null, Fixture.USER_PASSWORD, Fixture.USER_ROLE));
        assertThrows(IllegalArgumentException.class,
                () -> new User(Fixture.USER_NAME, " ", Fixture.USER_PASSWORD, Fixture.USER_ROLE));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaForNulaOuVazia() {
        assertThrows(IllegalArgumentException.class,
                () -> new User(Fixture.USER_NAME, Fixture.USER_EMAIL, null, Fixture.USER_ROLE));
        assertThrows(IllegalArgumentException.class,
                () -> new User(Fixture.USER_NAME, Fixture.USER_EMAIL, " ", Fixture.USER_ROLE));
    }

    @Test
    void deveLancarExcecaoQuandoRoleForNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new User(Fixture.USER_NAME, Fixture.USER_EMAIL, Fixture.USER_PASSWORD, null));
    }

    @Test
    void deveReconstruirUsuarioProfessor() {
        User user = new User(
                Fixture.USER_ID,
                Fixture.USER_NAME,
                Fixture.USER_EMAIL,
                Fixture.USER_PASSWORD,
                Role.PROFESSOR,
                Fixture.USER_CREATED_AT
        );

        assertEquals(Role.PROFESSOR, user.getRole());
    }
}
