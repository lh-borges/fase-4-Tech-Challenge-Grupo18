package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserResponseDTOTest {

    @Test
    void deveCriarUserResponseDTOComTodosOsCampos() {
        UserResponseDTO dto = Fixture.userResponseDTO();

        assertEquals(Fixture.USER_ID, dto.id());
        assertEquals(Fixture.USER_NAME, dto.name());
        assertEquals(Fixture.USER_EMAIL, dto.email());
        assertEquals(Fixture.USER_ROLE, dto.role());
        assertEquals(Fixture.USER_CREATED_AT, dto.createdAt());
    }

    @Test
    void deveCompararRecordsComMesmosValoresComoIguais() {
        UserResponseDTO dto = Fixture.userResponseDTO();
        UserResponseDTO outroDto = Fixture.userResponseDTO();

        assertEquals(dto, outroDto);
        assertEquals(dto.hashCode(), outroDto.hashCode());
    }

    @Test
    void devePermitirCamposNulosPorNaoPossuirValidacaoNoRecord() {
        UserResponseDTO dto = Fixture.userResponseDTOComNulos();

        assertNull(dto.id());
        assertNull(dto.name());
        assertNull(dto.email());
        assertNull(dto.role());
        assertNull(dto.createdAt());
    }
}
