package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.exception.UserNotFoundException;

public class UserFindByEmailUseCase {

    private final RepositoryUserPort repositoryUserPort;

    public UserFindByEmailUseCase(RepositoryUserPort repositoryUserPort) {
        this.repositoryUserPort = repositoryUserPort;
    }

    public UserResponseDTO execute(String email) {
        User user = repositoryUserPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return UserMapper.toResponse(user);
    }

    

}
