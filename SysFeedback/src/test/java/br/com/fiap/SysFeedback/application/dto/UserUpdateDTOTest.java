package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserUpdateDTOTest {

    @Test
    void deveCriarUserUpdateDTOComTodosOsCampos() {
        UserUpdateDTO dto = Fixture.userUpdateDTOValido();

        assertEquals(Fixture.USER_NAME, dto.name());
        assertEquals(Fixture.USER_EMAIL, dto.email());
        assertEquals(Fixture.USER_PASSWORD, dto.password());
        assertEquals(Fixture.USER_ROLE, dto.role());
    }

    @Test
    void deveCompararRecordsComMesmosValoresComoIguais() {
        UserUpdateDTO dto = Fixture.userUpdateDTOValido();
        UserUpdateDTO outroDto = Fixture.userUpdateDTOValido();

        assertEquals(dto, outroDto);
        assertEquals(dto.hashCode(), outroDto.hashCode());
    }

    @Test
    void devePermitirCamposNulosPorNaoPossuirValidacaoNoDTO() {
        UserUpdateDTO dto = Fixture.userUpdateDTOComNulos();

        assertNull(dto.name());
        assertNull(dto.email());
        assertNull(dto.password());
        assertNull(dto.role());
    }
}
