package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.UserRequestDTO;
import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.application.security.PasswordEncoderPort;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.exception.EmailAlreadyExistsException;

public class UserCreateUseCase {

    private final RepositoryUserPort repositoryUserPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public UserCreateUseCase(
            RepositoryUserPort repositoryUserPort,
            PasswordEncoderPort passwordEncoderPort) {

        this.repositoryUserPort = repositoryUserPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    public UserResponseDTO execute(UserRequestDTO user) {

        if (user != null && repositoryUserPort.findByEmail(user.email()).isPresent()) {
            throw new EmailAlreadyExistsException(user.email());
        }

        User userEntity = UserMapper.toDomain(user);

        userEntity.setPassword(
                passwordEncoderPort.encode(userEntity.getPassword())
        );

        User savedUser = repositoryUserPort.save(userEntity);

        return UserMapper.toResponse(savedUser);
    }
}
