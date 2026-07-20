package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.domain.entity.User;

import java.util.List;

public class UserFindAllUseCase {

    private final RepositoryUserPort repositoryUserPort;
    private final UserMapper userMapper;

    public UserFindAllUseCase(RepositoryUserPort repositoryUserPort,
                              UserMapper userMapper) {
        this.repositoryUserPort = repositoryUserPort;
        this.userMapper = userMapper;
    }

    public List<UserResponseDTO> execute() {
        return repositoryUserPort.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
