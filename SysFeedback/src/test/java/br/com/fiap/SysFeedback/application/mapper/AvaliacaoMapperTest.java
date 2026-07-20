package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AvaliacaoMapperTest {

    private final AvaliacaoMapper mapper = Mappers.getMapper(AvaliacaoMapper.class);

    @Test
    void deveConverterRequestDTOParaDomain() {
        AvaliacaoRequestDTO request = Fixture.avaliacaoRequestDTOValida();

        Avaliacao avaliacao = mapper.toDomain(request);

        assertNull(avaliacao.getId());
        assertEquals(Fixture.DESCRICAO_AVALIACAO, avaliacao.getDescricao());
        assertEquals(Fixture.NOTA_AVALIACAO, avaliacao.getNota());
        assertEquals(Urgencia.BAIXA, avaliacao.getUrgencia());
    }

    @Test
    void deveConverterRequestDTOComNotaBaixaParaDomainComUrgenciaAlta() {
        AvaliacaoRequestDTO request = Fixture.avaliacaoRequestDTO("Aula confusa", 2);

        Avaliacao avaliacao = mapper.toDomain(request);

        assertEquals(2, avaliacao.getNota());
        assertEquals(Urgencia.ALTA, avaliacao.getUrgencia());
    }

    @Test
    void deveConverterDomainParaResponseDTO() {
        Avaliacao avaliacao = Fixture.avaliacao();

        AvaliacaoResponseDTO response = mapper.toResponse(avaliacao);

        assertEquals(Fixture.AVALIACAO_ID, response.id());
        assertEquals(Fixture.DESCRICAO_AVALIACAO, response.descricao());
        assertEquals(Fixture.NOTA_AVALIACAO, response.nota());
        assertEquals(Urgencia.BAIXA, response.urgencia());
        assertEquals(Fixture.DATA_ENVIO, response.dataEnvio());
    }

    @Test
    void deveRetornarNullAoConverterRequestNullParaDomain() {
        Avaliacao avaliacao = mapper.toDomain(null);

        assertNull(avaliacao);
    }

    @Test
    void deveRetornarNullAoConverterDomainNullParaResponseDTO() {
        AvaliacaoResponseDTO response = mapper.toResponse(null);

        assertNull(response);
    }
}
