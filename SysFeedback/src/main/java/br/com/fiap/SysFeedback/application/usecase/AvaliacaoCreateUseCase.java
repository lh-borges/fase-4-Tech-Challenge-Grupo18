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

    public AvaliacaoCreateUseCase(RepositoryAvaliacaoPort repositoryAvaliacaoPort) {
        this.repositoryAvaliacaoPort = repositoryAvaliacaoPort;
    }

    public AvaliacaoResponseDTO execute(AvaliacaoRequestDTO request) {
        Avaliacao avaliacao = AvaliacaoMapper.toDomain(request);
        Avaliacao salva = repositoryAvaliacaoPort.save(avaliacao);
        return AvaliacaoMapper.toResponse(salva);
    }
}
