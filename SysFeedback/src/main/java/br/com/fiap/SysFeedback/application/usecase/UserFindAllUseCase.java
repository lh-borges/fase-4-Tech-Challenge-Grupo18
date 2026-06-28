package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.domain.entity.User;

import java.util.List;

public class UserFindAllUseCase {

    private final RepositoryUserPort repositoryUserPort;

    public UserFindAllUseCase(RepositoryUserPort repositoryUserPort) {
        this.repositoryUserPort = repositoryUserPort;
    }

    public List<UserResponseDTO> execute() {
        return repositoryUserPort.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }
}
