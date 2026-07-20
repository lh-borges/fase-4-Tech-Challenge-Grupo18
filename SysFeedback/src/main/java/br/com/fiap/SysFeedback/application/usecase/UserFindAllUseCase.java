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
    private final UserMapper userMapper;

    public UserFindAllUseCase(RepositoryUserPort repositoryUserPort,
                              UserMapper userMapper) {
        this.repositoryUserPort = repositoryUserPort;
        this.userMapper = userMapper;
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
                .map(userMapper::toResponse)
                .toList();
    }
}
