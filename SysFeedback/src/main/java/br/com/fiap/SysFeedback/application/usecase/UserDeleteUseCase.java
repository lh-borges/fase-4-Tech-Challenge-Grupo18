package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.exception.UserNotFoundException;
import java.util.UUID;

/**
 * Caso de uso responsável pela remoção de um usuário.
 *
 * @author Thiago de Jesus
 */
public class UserDeleteUseCase {

    private final RepositoryUserPort repositoryUserPort;

    /**
     * Cria o caso de uso com suas dependências.
     *
     * @param  repositoryUserPort  porta de persistência de usuários
     *
     * @author Thiago de Jesus
     */
    public UserDeleteUseCase(RepositoryUserPort repositoryUserPort) {
        this.repositoryUserPort = repositoryUserPort;
    }

    /**
     * Remove o usuário identificado pelo id informado.
     *
     * @param  id  identificador do usuário a ser removido
     *
     * @throws UserNotFoundException  quando não existe usuário com o id informado
     *
     * @author Thiago de Jesus
     */
    public void execute(UUID id) {
        User user = repositoryUserPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        repositoryUserPort.delete(user);
    }
}