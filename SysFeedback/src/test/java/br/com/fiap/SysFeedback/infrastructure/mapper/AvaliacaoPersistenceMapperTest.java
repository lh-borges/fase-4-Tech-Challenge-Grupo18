package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AvaliacaoPersistenceMapperTest {

    private final AvaliacaoPersistenceMapper mapper = Mappers.getMapper(AvaliacaoPersistenceMapper.class);

    @Test
    void deveConverterDomainParaJpa() {
        Avaliacao avaliacao = Fixture.avaliacao();

        AvaliacaoJpaEntity entity = mapper.toJpa(avaliacao);

        assertEquals(Fixture.AVALIACAO_ID, entity.getId());
        assertEquals(Fixture.DESCRICAO_AVALIACAO, entity.getDescricao());
        assertEquals(Fixture.NOTA_AVALIACAO, entity.getNota());
        assertEquals(Urgencia.BAIXA, entity.getUrgencia());
        assertEquals(Fixture.DATA_ENVIO, entity.getDataEnvio());
    }

    @Test
    void deveConverterJpaParaDomain() {
        AvaliacaoJpaEntity entity = new AvaliacaoJpaEntity(
                Fixture.AVALIACAO_ID,
                Fixture.DESCRICAO_AVALIACAO,
                Fixture.NOTA_AVALIACAO,
                Urgencia.BAIXA,
                Fixture.DATA_ENVIO
        );

        Avaliacao avaliacao = mapper.toDomain(entity);

        assertEquals(Fixture.AVALIACAO_ID, avaliacao.getId());
        assertEquals(Fixture.DESCRICAO_AVALIACAO, avaliacao.getDescricao());
        assertEquals(Fixture.NOTA_AVALIACAO, avaliacao.getNota());
        assertEquals(Urgencia.BAIXA, avaliacao.getUrgencia());
        assertEquals(Fixture.DATA_ENVIO, avaliacao.getDataEnvio());
    }

    @Test
    void deveRetornarNullQuandoDomainForNull() {
        assertNull(mapper.toJpa(null));
    }

    @Test
    void deveRetornarNullQuandoJpaForNull() {
        assertNull(mapper.toDomain(null));
    }
}
