package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;

import java.util.List;

/**
 * Caso de uso responsável por listar todos os usuários.
 *
 * @author Thiago de Jesus
 */
public class UserFindAllUseCase {

    private final RepositoryUserPort repositoryUserPort;

    /**
     * Cria o caso de uso com suas dependências.
     *
     * @param  repositoryUserPort  porta de persistência de usuários
     *
     * @author Thiago de Jesus
     */
    public UserFindAllUseCase(RepositoryUserPort repositoryUserPort) {
        this.repositoryUserPort = repositoryUserPort;
    }

    /**
     * Retorna todos os usuários cadastrados como DTOs de resposta.
     *
     * @return lista de usuários no formato de resposta
     *
     * @author Thiago de Jesus
     */
    public List<UserResponseDTO> execute() {
        return repositoryUserPort.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }
}
