package br.com.fiap.SysFeedback.domain.entity;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.domain.exception.AvaliacaoInvalidaException;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AvaliacaoTest {

    @Test
    void deveCriarAvaliacaoNovaComUrgenciaEDataAutomaticas() {
        Avaliacao avaliacao = new Avaliacao(
                Fixture.DESCRICAO_AVALIACAO,
                Fixture.NOTA_AVALIACAO,
                Fixture.DISCIPLINA_ID,
                Fixture.USER_ID
        );

        assertEquals(Fixture.DESCRICAO_AVALIACAO, avaliacao.getDescricao());
        assertEquals(Fixture.NOTA_AVALIACAO, avaliacao.getNota());
        assertEquals(Urgencia.BAIXA, avaliacao.getUrgencia());
        assertNotNull(avaliacao.getDataEnvio());
        assertEquals(Fixture.DISCIPLINA_ID, avaliacao.getDisciplinaId());
        assertEquals(Fixture.USER_ID, avaliacao.getAlunoId());
    }

    @Test
    void deveReconstruirAvaliacaoComTodosOsCampos() {
        Avaliacao avaliacao = Fixture.avaliacao();

        assertEquals(Fixture.AVALIACAO_ID, avaliacao.getId());
        assertEquals(Fixture.DESCRICAO_AVALIACAO, avaliacao.getDescricao());
        assertEquals(Fixture.NOTA_AVALIACAO, avaliacao.getNota());
        assertEquals(Urgencia.BAIXA, avaliacao.getUrgencia());
        assertEquals(Fixture.DATA_ENVIO, avaliacao.getDataEnvio());
    }

    @Test
    void deveAlterarId() {
        Avaliacao avaliacao = new Avaliacao(
                Fixture.DESCRICAO_AVALIACAO,
                Fixture.NOTA_AVALIACAO,
                Fixture.DISCIPLINA_ID,
                Fixture.USER_ID
        );

        avaliacao.setId(Fixture.AVALIACAO_ID);

        assertEquals(Fixture.AVALIACAO_ID, avaliacao.getId());
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoForNulaOuVazia() {
        assertThrows(AvaliacaoInvalidaException.class,
                () -> new Avaliacao(null, 8, Fixture.DISCIPLINA_ID, Fixture.USER_ID));
        assertThrows(AvaliacaoInvalidaException.class,
                () -> new Avaliacao(" ", 8, Fixture.DISCIPLINA_ID, Fixture.USER_ID));
    }

    @Test
    void deveLancarExcecaoQuandoNotaForMenorQueZeroOuMaiorQueDez() {
        assertThrows(AvaliacaoInvalidaException.class,
                () -> new Avaliacao(Fixture.DESCRICAO_AVALIACAO, -1, Fixture.DISCIPLINA_ID, Fixture.USER_ID));
        assertThrows(AvaliacaoInvalidaException.class,
                () -> new Avaliacao(Fixture.DESCRICAO_AVALIACAO, 11, Fixture.DISCIPLINA_ID, Fixture.USER_ID));
    }

    @Test
    void deveLancarExcecaoQuandoDisciplinaForNula() {
        assertThrows(AvaliacaoInvalidaException.class,
                () -> new Avaliacao(Fixture.DESCRICAO_AVALIACAO, 8, null, Fixture.USER_ID));
    }
}
