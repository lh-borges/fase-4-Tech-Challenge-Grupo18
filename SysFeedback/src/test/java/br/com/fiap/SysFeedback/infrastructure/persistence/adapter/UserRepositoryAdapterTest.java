package br.com.fiap.SysFeedback.infrastructure.persistence.adapter;

import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.enums.Role;
import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.mapper.UserPersistenceMapper;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.UserJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRepositoryAdapterTest {

    private final UserJpaRepository userJpaRepository = mock(UserJpaRepository.class);
    private final UserPersistenceMapper userPersistenceMapper = mock(UserPersistenceMapper.class);

    private UserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserRepositoryAdapter(userJpaRepository, userPersistenceMapper);
    }

    @Test
    void deveSalvarUsuarioMapeandoDomainParaJpaEDepoisParaDomain() {
        User user = Fixture.user();
        UserJpaEntity entity = userJpaEntity();

        when(userPersistenceMapper.toJpa(user)).thenReturn(entity);
        when(userJpaRepository.save(entity)).thenReturn(entity);
        when(userPersistenceMapper.toDomain(entity)).thenReturn(user);

        User result = adapter.save(user);

        assertEquals(user, result);
        verify(userPersistenceMapper).toJpa(user);
        verify(userJpaRepository).save(entity);
        verify(userPersistenceMapper).toDomain(entity);
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        User user = Fixture.user();
        UserJpaEntity entity = userJpaEntity();
        when(userJpaRepository.findByEmail(Fixture.USER_EMAIL)).thenReturn(Optional.of(entity));
        when(userPersistenceMapper.toDomain(entity)).thenReturn(user);

        Optional<User> result = adapter.findByEmail(Fixture.USER_EMAIL);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userJpaRepository).findByEmail(Fixture.USER_EMAIL);
    }

    @Test
    void deveRetornarOptionalVazioQuandoEmailNaoExistir() {
        when(userJpaRepository.findByEmail("naoexiste@fiap.com")).thenReturn(Optional.empty());

        Optional<User> result = adapter.findByEmail("naoexiste@fiap.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void deveBuscarUsuarioPorId() {
        User user = Fixture.user();
        UserJpaEntity entity = userJpaEntity();
        when(userJpaRepository.findById(Fixture.USER_ID)).thenReturn(Optional.of(entity));
        when(userPersistenceMapper.toDomain(entity)).thenReturn(user);

        Optional<User> result = adapter.findById(Fixture.USER_ID);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userJpaRepository).findById(Fixture.USER_ID);
    }

    @Test
    void deveExcluirUsuario() {
        User user = Fixture.user();
        UserJpaEntity entity = userJpaEntity();
        when(userPersistenceMapper.toJpa(user)).thenReturn(entity);

        adapter.delete(user);

        verify(userPersistenceMapper).toJpa(user);
        verify(userJpaRepository).delete(entity);
    }

    @Test
    void deveListarTodosUsuarios() {
        User user = Fixture.user();
        UserJpaEntity entity = userJpaEntity();
        when(userJpaRepository.findAll()).thenReturn(List.of(entity));
        when(userPersistenceMapper.toDomain(entity)).thenReturn(user);

        List<User> result = adapter.findAll();

        assertEquals(List.of(user), result);
        verify(userJpaRepository).findAll();
    }

    private UserJpaEntity userJpaEntity() {
        return new UserJpaEntity(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                Fixture.USER_NAME,
                Fixture.USER_EMAIL,
                Fixture.USER_PASSWORD,
                Role.ALUNO,
                Fixture.USER_CREATED_AT
        );
    }
}
