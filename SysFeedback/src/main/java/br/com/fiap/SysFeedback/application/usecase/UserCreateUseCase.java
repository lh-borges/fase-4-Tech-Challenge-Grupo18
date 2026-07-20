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
    private final UserMapper userMapper;

    public UserCreateUseCase(
            RepositoryUserPort repositoryUserPort,
            PasswordEncoderPort passwordEncoderPort,
            UserMapper userMapper) {

        this.repositoryUserPort = repositoryUserPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.userMapper = userMapper;
    }

    public UserResponseDTO execute(UserRequestDTO user) {

        if (user != null && repositoryUserPort.findByEmail(user.email()).isPresent()) {
            throw new EmailAlreadyExistsException(user.email());
        }

        User userEntity = userMapper.toDomain(user);

        userEntity.setPassword(
                passwordEncoderPort.encode(userEntity.getPassword())
        );

        User savedUser = repositoryUserPort.save(userEntity);

        return userMapper.toResponse(savedUser);
    }
}
