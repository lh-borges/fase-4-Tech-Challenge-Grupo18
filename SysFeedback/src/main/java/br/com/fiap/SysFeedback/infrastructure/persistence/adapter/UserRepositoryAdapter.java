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

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements RepositoryUserPort {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserPersistenceMapper.toJpa(user);
        UserJpaEntity saved = userJpaRepository.save(entity);
        return UserPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id)
                .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public void delete(User user) {
        UserJpaEntity entity = UserPersistenceMapper.toJpa(user);
        userJpaRepository.delete(entity);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll()
                .stream()
                .map(UserPersistenceMapper::toDomain)
                .toList();
    }
}