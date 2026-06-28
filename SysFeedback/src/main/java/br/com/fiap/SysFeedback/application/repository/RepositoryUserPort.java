package br.com.fiap.SysFeedback.application.repository;

import br.com.fiap.SysFeedback.domain.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepositoryUserPort {

    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    void delete(User user);
    List<User> findAll();

}
