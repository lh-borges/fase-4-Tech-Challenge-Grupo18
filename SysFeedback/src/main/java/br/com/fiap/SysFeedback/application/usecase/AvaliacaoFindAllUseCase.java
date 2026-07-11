package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.AvaliacaoMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;

import java.util.List;

/**
 * Lista todas as avaliações. Consumido pelo dono (roles PROFESSOR/ADMIN).
 */
public class AvaliacaoFindAllUseCase {

    private final RepositoryAvaliacaoPort repositoryAvaliacaoPort;

    public AvaliacaoFindAllUseCase(RepositoryAvaliacaoPort repositoryAvaliacaoPort) {
        this.repositoryAvaliacaoPort = repositoryAvaliacaoPort;
    }

    public List<AvaliacaoResponseDTO> execute() {
        return repositoryAvaliacaoPort.findAll()
                .stream()
                .map(AvaliacaoMapper::toResponse)
                .toList();
    }
}
