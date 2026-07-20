package br.com.fiap.SysFeedback.infrastructure.persistence.repository;

import br.com.fiap.SysFeedback.SysFeedbackApplication;
import br.com.fiap.SysFeedback.domain.enums.Role;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = SysFeedbackApplication.class)
@EntityScan(basePackages = "br.com.fiap.SysFeedback.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "br.com.fiap.SysFeedback.infrastructure.persistence.repository")
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository repository;

    @Test
    void deveSalvarEListarUsuario() {
        UserJpaEntity saved = repository.save(user("Aluno Teste", "aluno@fiap.com", Role.ALUNO));

        assertNotNull(saved.getId());
        assertEquals(1, repository.findAll().size());
        assertEquals("Aluno Teste", repository.findAll().get(0).getName());
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        repository.save(user("Aluno Teste", "aluno@fiap.com", Role.ALUNO));

        Optional<UserJpaEntity> result = repository.findByEmail("aluno@fiap.com");

        assertTrue(result.isPresent());
        assertEquals("Aluno Teste", result.get().getName());
        assertEquals(Role.ALUNO, result.get().getRole());
    }

    @Test
    void deveRetornarOptionalVazioQuandoEmailNaoExistir() {
        Optional<UserJpaEntity> result = repository.findByEmail("naoexiste@fiap.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void deveVerificarSeEmailExiste() {
        repository.save(user("Professor Teste", "professor@fiap.com", Role.PROFESSOR));

        assertTrue(repository.existsByEmail("professor@fiap.com"));
        assertFalse(repository.existsByEmail("outro@fiap.com"));
    }

    @Test
    void deveFalharAoSalvarEmailDuplicado() {
        repository.saveAndFlush(user("Aluno Teste", "duplicado@fiap.com", Role.ALUNO));

        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(user("Outro Aluno", "duplicado@fiap.com", Role.ALUNO)));
    }

    @Test
    void deveFalharAoSalvarUsuarioSemEmail() {
        UserJpaEntity entity = user("Aluno Teste", null, Role.ALUNO);

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(entity));
    }

    @Test
    void deveFalharAoSalvarUsuarioSemRole() {
        UserJpaEntity entity = user("Aluno Teste", "semrole@fiap.com", null);

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(entity));
    }

    private UserJpaEntity user(String name, String email, Role role) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setName(name);
        entity.setEmail(email);
        entity.setPassword("senha");
        entity.setRole(role);
        entity.setCreatedAt(LocalDateTime.of(2026, 7, 19, 10, 0));
        return entity;
    }
}
