package br.com.fiap.SysFeedback.infrastructure.persistence.entity;

import br.com.fiap.SysFeedback.domain.enums.Role;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DisciplinaJpaEntityTest {

    @Test
    void construtorVazioDeveInicializarRelacoesVazias() {
        DisciplinaJpaEntity entity = new DisciplinaJpaEntity();

        assertNull(entity.getId());
        assertNull(entity.getNome());
        assertNull(entity.getCodigo());
        assertNotNull(entity.getProfessores());
        assertNotNull(entity.getAlunos());
        assertTrue(entity.getProfessores().isEmpty());
        assertTrue(entity.getAlunos().isEmpty());
    }

    @Test
    void deveCriarComNomeECodigo() {
        DisciplinaJpaEntity entity = new DisciplinaJpaEntity(Fixture.DISCIPLINA_NOME, "ARQ");

        assertNull(entity.getId());
        assertEquals(Fixture.DISCIPLINA_NOME, entity.getNome());
        assertEquals("ARQ", entity.getCodigo());
        assertTrue(entity.getProfessores().isEmpty());
        assertTrue(entity.getAlunos().isEmpty());
    }

    @Test
    void devePermitirAlterarCamposERelacoesComSetters() {
        UUID id = UUID.randomUUID();
        UserJpaEntity professor = user("professor@fiap.com", Role.PROFESSOR);
        UserJpaEntity aluno = user("aluno@fiap.com", Role.ALUNO);
        DisciplinaJpaEntity entity = new DisciplinaJpaEntity();

        entity.setId(id);
        entity.setNome("DevOps");
        entity.setCodigo("DEVOPS");
        entity.setProfessores(new HashSet<>(Set.of(professor)));
        entity.setAlunos(new HashSet<>(Set.of(aluno)));

        assertEquals(id, entity.getId());
        assertEquals("DevOps", entity.getNome());
        assertEquals("DEVOPS", entity.getCodigo());
        assertEquals(Set.of(professor), entity.getProfessores());
        assertEquals(Set.of(aluno), entity.getAlunos());
    }

    private UserJpaEntity user(String email, Role role) {
        return new UserJpaEntity(
                UUID.randomUUID(),
                "Usuario Teste",
                email,
                "123456",
                role,
                LocalDateTime.of(2026, 7, 20, 10, 0)
        );
    }
}
