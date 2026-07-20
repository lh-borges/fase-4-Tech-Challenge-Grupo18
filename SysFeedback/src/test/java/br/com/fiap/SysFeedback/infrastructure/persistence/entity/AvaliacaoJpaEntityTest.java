package br.com.fiap.SysFeedback.infrastructure.persistence.entity;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AvaliacaoJpaEntityTest {

    @Test
    void deveCriarComConstrutorCompleto() {
        DisciplinaJpaEntity disciplina = new DisciplinaJpaEntity(Fixture.DISCIPLINA_NOME, "ARQ");
        disciplina.setId(Fixture.DISCIPLINA_ID);
        UserJpaEntity aluno = new UserJpaEntity(
                Fixture.USER_ID,
                Fixture.USER_NAME,
                Fixture.USER_EMAIL,
                Fixture.USER_PASSWORD,
                Fixture.USER_ROLE,
                Fixture.USER_CREATED_AT
        );
        AvaliacaoJpaEntity entity = new AvaliacaoJpaEntity(
                Fixture.AVALIACAO_ID,
                Fixture.DESCRICAO_AVALIACAO,
                Fixture.NOTA_AVALIACAO,
                Urgencia.BAIXA,
                Fixture.DATA_ENVIO,
                disciplina,
                aluno
        );

        assertEquals(Fixture.AVALIACAO_ID, entity.getId());
        assertEquals(Fixture.DESCRICAO_AVALIACAO, entity.getDescricao());
        assertEquals(Fixture.NOTA_AVALIACAO, entity.getNota());
        assertEquals(Urgencia.BAIXA, entity.getUrgencia());
        assertEquals(Fixture.DATA_ENVIO, entity.getDataEnvio());
        assertEquals(Fixture.DISCIPLINA_ID, entity.getDisciplina().getId());
        assertEquals(Fixture.USER_ID, entity.getAluno().getId());
    }

    @Test
    void devePermitirAlterarCamposComSetters() {
        UUID id = UUID.randomUUID();
        LocalDateTime dataEnvio = LocalDateTime.of(2026, 7, 19, 10, 0);
        AvaliacaoJpaEntity entity = new AvaliacaoJpaEntity();

        entity.setId(id);
        entity.setDescricao("Conteudo confuso");
        entity.setNota(4);
        entity.setUrgencia(Urgencia.ALTA);
        entity.setDataEnvio(dataEnvio);

        assertEquals(id, entity.getId());
        assertEquals("Conteudo confuso", entity.getDescricao());
        assertEquals(4, entity.getNota());
        assertEquals(Urgencia.ALTA, entity.getUrgencia());
        assertEquals(dataEnvio, entity.getDataEnvio());
    }

    @Test
    void construtorVazioDeveInicializarCamposComoNulosOuZero() {
        AvaliacaoJpaEntity entity = new AvaliacaoJpaEntity();

        assertNull(entity.getId());
        assertNull(entity.getDescricao());
        assertEquals(0, entity.getNota());
        assertNull(entity.getUrgencia());
        assertNull(entity.getDataEnvio());
    }
}
