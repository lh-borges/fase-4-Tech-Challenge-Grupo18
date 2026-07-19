package br.com.fiap.SysFeedback.infrastructure.persistence.adapter;

import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.infrastructure.mapper.UserPersistenceMapper;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.UserJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de persistência que implementa a porta {@link RepositoryUserPort}
 * delegando as operações ao {@link UserJpaRepository} e mapeando entre domínio e JPA.
 *
 * @author Thiago de Jesus
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements RepositoryUserPort {

    private final UserJpaRepository userJpaRepository;

    /**
     * Persiste o usuário informado e retorna a entidade de domínio salva.
     *
     * @param  user  usuário de domínio a ser salvo
     * @return usuário persistido convertido para o domínio
     *
     * @author Thiago de Jesus
     */
    @Override
    public User save(User user) {
        UserJpaEntity entity = UserPersistenceMapper.toJpa(user);
        UserJpaEntity saved = userJpaRepository.save(entity);
        return UserPersistenceMapper.toDomain(saved);
    }

    /**
     * Busca um usuário pelo e-mail.
     *
     * @param  email  e-mail utilizado na busca
     * @return {@link Optional} com o usuário encontrado, ou vazio se não existir
     *
     * @author Thiago de Jesus
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(UserPersistenceMapper::toDomain);
    }

    /**
     * Busca um usuário pelo identificador único.
     *
     * @param  id  identificador do usuário
     * @return {@link Optional} com o usuário encontrado, ou vazio se não existir
     *
     * @author Thiago de Jesus
     */
    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id)
                .map(UserPersistenceMapper::toDomain);
    }

    /**
     * Remove o usuário informado da base de dados.
     *
     * @param  user  usuário de domínio a ser removido
     *
     * @author Thiago de Jesus
     */
    @Override
    public void delete(User user) {
        UserJpaEntity entity = UserPersistenceMapper.toJpa(user);
        userJpaRepository.delete(entity);
    }

    /**
     * Retorna todos os usuários cadastrados.
     *
     * @return lista de usuários de domínio
     *
     * @author Thiago de Jesus
     */
    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll()
                .stream()
                .map(UserPersistenceMapper::toDomain)
                .toList();
    }
}