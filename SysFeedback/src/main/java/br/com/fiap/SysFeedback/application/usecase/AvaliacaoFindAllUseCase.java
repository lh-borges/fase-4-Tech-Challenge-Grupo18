package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.AvaliacaoMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;

import java.util.List;

/**
 * Lista todas as avaliações. Consumido pelo dono (roles PROFESSOR/ADMIN).
 *
 * @author luisbraserv
 */
public class AvaliacaoFindAllUseCase {

    private final RepositoryAvaliacaoPort repositoryAvaliacaoPort;

    /**
     * Cria o caso de uso com sua dependência.
     *
     * @param  repositoryAvaliacaoPort  porta de persistência de avaliações
     *
     * @author luisbraserv
     */
    public AvaliacaoFindAllUseCase(RepositoryAvaliacaoPort repositoryAvaliacaoPort) {
        this.repositoryAvaliacaoPort = repositoryAvaliacaoPort;
    }

    /**
     * Retorna todas as avaliações registradas.
     *
     * @return lista de avaliações
     *
     * @author luisbraserv
     */
    public List<AvaliacaoResponseDTO> execute() {
        return repositoryAvaliacaoPort.findAll()
                .stream()
                .map(AvaliacaoMapper::toResponse)
                .toList();
    }
}
