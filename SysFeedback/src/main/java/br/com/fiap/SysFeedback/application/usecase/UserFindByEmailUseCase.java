package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.exception.UserNotFoundException;

/**
 * Caso de uso responsável por buscar um usuário pelo e-mail.
 *
 * @author Thiago de Jesus
 */
public class UserFindByEmailUseCase {

    private final RepositoryUserPort repositoryUserPort;

    /**
     * Cria o caso de uso com suas dependências.
     *
     * @param  repositoryUserPort  porta de persistência de usuários
     *
     * @author Thiago de Jesus
     */
    public UserFindByEmailUseCase(RepositoryUserPort repositoryUserPort) {
        this.repositoryUserPort = repositoryUserPort;
    }

    /**
     * Busca um usuário pelo e-mail e o retorna como DTO de resposta.
     *
     * @param  email  e-mail do usuário a ser buscado
     * @return usuário encontrado no formato de resposta
     *
     * @throws UserNotFoundException  quando não existe usuário com o e-mail informado
     *
     * @author Thiago de Jesus
     */
    public UserResponseDTO execute(String email) {
        User user = repositoryUserPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return UserMapper.toResponse(user);
    }

}
