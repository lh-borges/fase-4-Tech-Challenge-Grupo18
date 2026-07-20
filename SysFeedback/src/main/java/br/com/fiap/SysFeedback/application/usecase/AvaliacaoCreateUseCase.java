package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.AvaliacaoMapper;
import br.com.fiap.SysFeedback.application.messaging.NotificadorUrgentePort;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryDisciplinaPort;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.domain.entity.Disciplina;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.domain.exception.UnauthorizedOperationException;

import java.util.UUID;

/**
 * Registra uma avaliação enviada por um aluno para uma disciplina.
 */
public class AvaliacaoCreateUseCase {

    private final RepositoryAvaliacaoPort repositoryAvaliacaoPort;
    private final RepositoryDisciplinaPort repositoryDisciplinaPort;
    private final NotificadorUrgentePort notificadorUrgentePort;

    public AvaliacaoCreateUseCase(RepositoryAvaliacaoPort repositoryAvaliacaoPort,
                                  RepositoryDisciplinaPort repositoryDisciplinaPort,
                                  NotificadorUrgentePort notificadorUrgentePort) {
        this.repositoryAvaliacaoPort = repositoryAvaliacaoPort;
        this.repositoryDisciplinaPort = repositoryDisciplinaPort;
        this.notificadorUrgentePort = notificadorUrgentePort;
    }

    public AvaliacaoResponseDTO execute(AvaliacaoRequestDTO request, UUID alunoId) {
        Disciplina disciplina = repositoryDisciplinaPort.findById(request.disciplinaId())
                .orElseThrow(() -> new UnauthorizedOperationException("Disciplina não encontrada"));

        if (!repositoryDisciplinaPort.alunoMatriculado(alunoId, disciplina.getId())) {
            throw new UnauthorizedOperationException("Aluno não está matriculado nesta disciplina");
        }

        Avaliacao avaliacao = new Avaliacao(request.descricao(), request.nota(), disciplina.getId(), alunoId);
        Avaliacao salva = repositoryAvaliacaoPort.save(avaliacao);

        if (salva.getUrgencia() == Urgencia.ALTA) {
            notificadorUrgentePort.notificarUrgente(salva);
        }

        return AvaliacaoMapper.toResponse(salva, disciplina.getNome());
    }
}
