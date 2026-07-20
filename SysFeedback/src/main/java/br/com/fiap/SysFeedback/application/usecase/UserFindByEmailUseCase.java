package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.exception.UserNotFoundException;

public class UserFindByEmailUseCase {

    private final RepositoryUserPort repositoryUserPort;
    private final UserMapper userMapper;

    public UserFindByEmailUseCase(RepositoryUserPort repositoryUserPort,
                                  UserMapper userMapper) {
        this.repositoryUserPort = repositoryUserPort;
        this.userMapper = userMapper;
    }

    public UserResponseDTO execute(String email) {
        User user = repositoryUserPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return userMapper.toResponse(user);
    }
}
