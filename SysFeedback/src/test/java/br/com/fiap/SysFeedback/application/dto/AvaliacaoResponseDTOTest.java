package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvaliacaoResponseDTOTest {

    @Test
    void deveCriarAvaliacaoResponseDTOComTodosOsCampos() {
        AvaliacaoResponseDTO dto = Fixture.avaliacaoResponseDTO();

        assertEquals(Fixture.AVALIACAO_ID, dto.id());
        assertEquals(Fixture.DESCRICAO_AVALIACAO, dto.descricao());
        assertEquals(Fixture.NOTA_AVALIACAO, dto.nota());
        assertEquals(Urgencia.BAIXA, dto.urgencia());
        assertEquals(Fixture.DATA_ENVIO, dto.dataEnvio());
    }

    @Test
    void deveCompararRecordsComMesmosValoresComoIguais() {
        AvaliacaoResponseDTO dto = Fixture.avaliacaoResponseDTO();
        AvaliacaoResponseDTO outroDto = Fixture.avaliacaoResponseDTO();

        assertEquals(dto, outroDto);
        assertEquals(dto.hashCode(), outroDto.hashCode());
    }

    @Test
    void deveIncluirCamposNoToStringDoRecord() {
        String texto = Fixture.avaliacaoResponseDTO().toString();

        assertTrue(texto.contains(Fixture.AVALIACAO_ID.toString()));
        assertTrue(texto.contains(Fixture.DESCRICAO_AVALIACAO));
        assertTrue(texto.contains("nota=" + Fixture.NOTA_AVALIACAO));
    }

    @Test
    void devePermitirCamposNulosPorNaoPossuirValidacaoNoRecord() {
        AvaliacaoResponseDTO dto = Fixture.avaliacaoResponseDTOComNulos();

        assertNull(dto.id());
        assertNull(dto.descricao());
        assertEquals(Fixture.NOTA_AVALIACAO, dto.nota());
        assertNull(dto.urgencia());
        assertNull(dto.dataEnvio());
    }

    @Test
    void devePermitirNotaNegativaPorNaoPossuirValidacaoNoResponseDTO() {
        AvaliacaoResponseDTO dto = new AvaliacaoResponseDTO(
                Fixture.AVALIACAO_ID,
                Fixture.DESCRICAO_AVALIACAO,
                -1,
                Urgencia.ALTA,
                Fixture.DATA_ENVIO
        );

        assertEquals(-1, dto.nota());
    }
}
