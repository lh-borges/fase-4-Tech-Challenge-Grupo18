package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.UserJpaEntity;

/**
 * Conversor entre a entidade de domínio {@link User} e a entidade de
 * persistência {@link UserJpaEntity}.
 *
 * @author Thiago de Jesus
 */
public class UserPersistenceMapper {


    /**
     * Converte a entidade de domínio para a entidade JPA de persistência.
     *
     * @param  user  usuário de domínio a ser convertido
     * @return entidade JPA correspondente, ou {@code null} se a entrada for nula
     *
     * @author Thiago de Jesus
     */
    public static UserJpaEntity toJpa(User user) {
        if (user == null) {
            return null;
        }
        return new UserJpaEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    /**
     * Converte a entidade JPA de persistência para a entidade de domínio.
     *
     * @param  entity  entidade JPA a ser convertida
     * @return usuário de domínio correspondente, ou {@code null} se a entrada for nula
     *
     * @author Thiago de Jesus
     */
    public static User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.getCreatedAt()
        );
    }
}
