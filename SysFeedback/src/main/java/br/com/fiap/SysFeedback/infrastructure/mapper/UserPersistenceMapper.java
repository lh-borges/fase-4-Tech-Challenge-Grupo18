package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.UserJpaEntity;

public class UserPersistenceMapper {


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

    public static User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        User user = new User(
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole()
        );
        user.setId(entity.getId());
        return user;
    }
}
