package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.exception.UserNotFoundException;
import java.util.UUID;

public class UserDeleteUseCase {

    private final RepositoryUserPort repositoryUserPort;

    public UserDeleteUseCase(RepositoryUserPort repositoryUserPort) {
        this.repositoryUserPort = repositoryUserPort;
    }

    public void execute(UUID id) {
        User user = repositoryUserPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        repositoryUserPort.delete(user);
    }
}