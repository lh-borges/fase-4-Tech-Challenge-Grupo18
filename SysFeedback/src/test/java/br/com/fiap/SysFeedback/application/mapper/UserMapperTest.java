package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.UserRequestDTO;
import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void deveConverterRequestDTOParaDomain() {
        UserRequestDTO request = Fixture.userRequestDTOValido();

        User user = mapper.toDomain(request);

        assertNull(user.getId());
        assertEquals(Fixture.USER_NAME, user.getName());
        assertEquals(Fixture.USER_EMAIL, user.getEmail());
        assertEquals(Fixture.USER_PASSWORD, user.getPassword());
        assertEquals(Fixture.USER_ROLE, user.getRole());
    }

    @Test
    void deveConverterDomainParaResponseDTO() {
        User user = Fixture.user();

        UserResponseDTO response = mapper.toResponse(user);

        assertEquals(Fixture.USER_ID, response.id());
        assertEquals(Fixture.USER_NAME, response.name());
        assertEquals(Fixture.USER_EMAIL, response.email());
        assertEquals(Fixture.USER_ROLE, response.role());
        assertEquals(Fixture.USER_CREATED_AT, response.createdAt());
    }

    @Test
    void deveRetornarNullAoConverterRequestNullParaDomain() {
        User user = mapper.toDomain(null);

        assertNull(user);
    }

    @Test
    void deveRetornarNullAoConverterDomainNullParaResponseDTO() {
        UserResponseDTO response = mapper.toResponse(null);

        assertNull(response);
    }
}
