package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.Disciplina;
import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.DisciplinaJpaEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DisciplinaPersistenceMapperTest {

    private final DisciplinaPersistenceMapper mapper =
            Mappers.getMapper(DisciplinaPersistenceMapper.class);

    @Test
    void deveConverterJpaParaDomain() {
        DisciplinaJpaEntity entity = disciplinaJpaEntity();

        Disciplina disciplina = mapper.toDomain(entity);

        assertEquals(Fixture.DISCIPLINA_ID, disciplina.getId());
        assertEquals(Fixture.DISCIPLINA_NOME, disciplina.getNome());
        assertEquals("ARQ", disciplina.getCodigo());
    }

    @Test
    void deveConverterDomainParaJpaIgnorandoRelacionamentos() {
        Disciplina disciplina = new Disciplina(Fixture.DISCIPLINA_ID, Fixture.DISCIPLINA_NOME, "ARQ");

        DisciplinaJpaEntity entity = mapper.toJpa(disciplina);

        assertEquals(Fixture.DISCIPLINA_ID, entity.getId());
        assertEquals(Fixture.DISCIPLINA_NOME, entity.getNome());
        assertEquals("ARQ", entity.getCodigo());
        assertNotNull(entity.getProfessores());
        assertNotNull(entity.getAlunos());
        assertTrue(entity.getProfessores().isEmpty());
        assertTrue(entity.getAlunos().isEmpty());
    }

    @Test
    void deveRetornarNullQuandoJpaForNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    void deveRetornarNullQuandoDomainForNull() {
        assertNull(mapper.toJpa(null));
    }

    private DisciplinaJpaEntity disciplinaJpaEntity() {
        DisciplinaJpaEntity entity = new DisciplinaJpaEntity(Fixture.DISCIPLINA_NOME, "ARQ");
        entity.setId(Fixture.DISCIPLINA_ID);
        return entity;
    }
}
