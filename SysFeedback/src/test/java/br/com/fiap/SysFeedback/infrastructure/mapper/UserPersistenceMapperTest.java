package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.enums.Role;
import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserPersistenceMapperTest {

    private final UserPersistenceMapper mapper = Mappers.getMapper(UserPersistenceMapper.class);

    @Test
    void deveConverterDomainParaJpa() {
        User user = Fixture.user();

        UserJpaEntity entity = mapper.toJpa(user);

        assertEquals(Fixture.USER_ID, entity.getId());
        assertEquals(Fixture.USER_NAME, entity.getName());
        assertEquals(Fixture.USER_EMAIL, entity.getEmail());
        assertEquals(Fixture.USER_PASSWORD, entity.getPassword());
        assertEquals(Role.ALUNO, entity.getRole());
        assertEquals(Fixture.USER_CREATED_AT, entity.getCreatedAt());
    }

    @Test
    void deveConverterJpaParaDomain() {
        UserJpaEntity entity = new UserJpaEntity(
                Fixture.USER_ID,
                Fixture.USER_NAME,
                Fixture.USER_EMAIL,
                Fixture.USER_PASSWORD,
                Role.ALUNO,
                Fixture.USER_CREATED_AT
        );

        User user = mapper.toDomain(entity);

        assertEquals(Fixture.USER_ID, user.getId());
        assertEquals(Fixture.USER_NAME, user.getName());
        assertEquals(Fixture.USER_EMAIL, user.getEmail());
        assertEquals(Fixture.USER_PASSWORD, user.getPassword());
        assertEquals(Role.ALUNO, user.getRole());
        assertEquals(Fixture.USER_CREATED_AT, user.getCreatedAt());
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
