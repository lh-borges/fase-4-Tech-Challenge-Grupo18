package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.AvaliacaoMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;

/**
 * Registra uma nova avaliação enviada por um usuário (role ALUNO).
 */
public class AvaliacaoCreateUseCase {

    private final RepositoryAvaliacaoPort repositoryAvaliacaoPort;
    private final AvaliacaoMapper avaliacaoMapper;

    public AvaliacaoCreateUseCase(RepositoryAvaliacaoPort repositoryAvaliacaoPort,
                                  AvaliacaoMapper avaliacaoMapper) {
        this.repositoryAvaliacaoPort = repositoryAvaliacaoPort;
        this.avaliacaoMapper = avaliacaoMapper;
    }

    public AvaliacaoResponseDTO execute(AvaliacaoRequestDTO request) {
        Avaliacao avaliacao = avaliacaoMapper.toDomain(request);
        Avaliacao salva = repositoryAvaliacaoPort.save(avaliacao);
        return avaliacaoMapper.toResponse(salva);
    }
}
