package br.com.fiap.SysFeedback.infrastructure.persistence.repository;

import br.com.fiap.SysFeedback.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório Spring Data JPA para a entidade {@link UserJpaEntity}.
 *
 * @author Thiago de Jesus
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    /**
     * Busca uma entidade de usuário pelo e-mail.
     *
     * @param  email  e-mail utilizado na busca
     * @return {@link Optional} com a entidade encontrada, ou vazio se não existir
     *
     * @author Thiago de Jesus
     */
    Optional<UserJpaEntity> findByEmail(String email);

    /**
     * Verifica se já existe um usuário cadastrado com o e-mail informado.
     *
     * @param  email  e-mail a ser verificado
     * @return {@code true} se existir um usuário com o e-mail, caso contrário {@code false}
     *
     * @author Thiago de Jesus
     */
    boolean existsByEmail(String email);
}
