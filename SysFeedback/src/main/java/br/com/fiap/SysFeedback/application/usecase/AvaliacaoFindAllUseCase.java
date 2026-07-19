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
    private final AvaliacaoMapper avaliacaoMapper;

    public AvaliacaoFindAllUseCase(RepositoryAvaliacaoPort repositoryAvaliacaoPort,
                                   AvaliacaoMapper avaliacaoMapper) {
        this.repositoryAvaliacaoPort = repositoryAvaliacaoPort;
        this.avaliacaoMapper = avaliacaoMapper;
    }

    public List<AvaliacaoResponseDTO> execute() {
        return repositoryAvaliacaoPort.findAll()
                .stream()
                .map(avaliacaoMapper::toResponse)
                .toList();
    }
}
