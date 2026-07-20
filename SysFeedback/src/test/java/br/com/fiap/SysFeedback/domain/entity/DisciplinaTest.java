package br.com.fiap.SysFeedback.domain.entity;

import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DisciplinaTest {

    @Test
    void deveCriarDisciplinaSemId() {
        Disciplina disciplina = new Disciplina(Fixture.DISCIPLINA_NOME, "ARQ");

        assertNull(disciplina.getId());
        assertEquals(Fixture.DISCIPLINA_NOME, disciplina.getNome());
        assertEquals("ARQ", disciplina.getCodigo());
    }

    @Test
    void deveReconstruirDisciplinaComId() {
        Disciplina disciplina = new Disciplina(Fixture.DISCIPLINA_ID, Fixture.DISCIPLINA_NOME, "ARQ");

        assertEquals(Fixture.DISCIPLINA_ID, disciplina.getId());
        assertEquals(Fixture.DISCIPLINA_NOME, disciplina.getNome());
        assertEquals("ARQ", disciplina.getCodigo());
    }

    @Test
    void deveAlterarId() {
        UUID id = UUID.randomUUID();
        Disciplina disciplina = new Disciplina(Fixture.DISCIPLINA_NOME, "ARQ");

        disciplina.setId(id);

        assertEquals(id, disciplina.getId());
    }

    @Test
    void devePermitirCamposNulosPorNaoPossuirValidacaoNoDominio() {
        Disciplina disciplina = new Disciplina(null, null, null);

        assertNull(disciplina.getId());
        assertNull(disciplina.getNome());
        assertNull(disciplina.getCodigo());
    }
}
