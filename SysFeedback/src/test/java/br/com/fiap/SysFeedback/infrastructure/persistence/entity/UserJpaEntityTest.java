package br.com.fiap.SysFeedback.infrastructure.persistence.entity;

import br.com.fiap.SysFeedback.domain.enums.Role;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserJpaEntityTest {

    @Test
    void deveCriarComConstrutorCompleto() {
        UserJpaEntity entity = new UserJpaEntity(
                Fixture.USER_ID,
                Fixture.USER_NAME,
                Fixture.USER_EMAIL,
                Fixture.USER_PASSWORD,
                Role.ALUNO,
                Fixture.USER_CREATED_AT
        );

        assertEquals(Fixture.USER_ID, entity.getId());
        assertEquals(Fixture.USER_NAME, entity.getName());
        assertEquals(Fixture.USER_EMAIL, entity.getEmail());
        assertEquals(Fixture.USER_PASSWORD, entity.getPassword());
        assertEquals(Role.ALUNO, entity.getRole());
        assertEquals(Fixture.USER_CREATED_AT, entity.getCreatedAt());
    }

    @Test
    void devePermitirAlterarCamposComSetters() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 19, 9, 0);
        UserJpaEntity entity = new UserJpaEntity();

        entity.setId(id);
        entity.setName("Professor Teste");
        entity.setEmail("professor@fiap.com");
        entity.setPassword("senha");
        entity.setRole(Role.PROFESSOR);
        entity.setCreatedAt(createdAt);

        assertEquals(id, entity.getId());
        assertEquals("Professor Teste", entity.getName());
        assertEquals("professor@fiap.com", entity.getEmail());
        assertEquals("senha", entity.getPassword());
        assertEquals(Role.PROFESSOR, entity.getRole());
        assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    void construtorVazioDeveInicializarCamposComoNulos() {
        UserJpaEntity entity = new UserJpaEntity();

        assertNull(entity.getId());
        assertNull(entity.getName());
        assertNull(entity.getEmail());
        assertNull(entity.getPassword());
        assertNull(entity.getRole());
        assertNull(entity.getCreatedAt());
    }
}
