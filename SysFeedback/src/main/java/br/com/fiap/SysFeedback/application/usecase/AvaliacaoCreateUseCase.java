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
 * Registra uma avaliação de uma disciplina enviada por um aluno (role ALUNO).
 *
 * <p>O aluno só pode avaliar disciplina em que está matriculado. Quando a avaliação
 * é crítica (urgência ALTA), dispara uma notificação assíncrona via
 * {@link NotificadorUrgentePort} (Pub/Sub → Cloud Function de e-mail). A regra de
 * "o que é crítico" vive aqui/no domínio; a infraestrutura só transporta o evento.</p>
 *
 * @author luisbraserv
 */
public class AvaliacaoCreateUseCase {

    private final RepositoryAvaliacaoPort repositoryAvaliacaoPort;
    private final RepositoryDisciplinaPort repositoryDisciplinaPort;
    private final NotificadorUrgentePort notificadorUrgentePort;

    /**
     * Cria o caso de uso com suas dependências.
     *
     * @param  repositoryAvaliacaoPort  porta de persistência de avaliações
     * @param  repositoryDisciplinaPort  porta de consulta de disciplinas e matrículas
     * @param  notificadorUrgentePort  porta de notificação de itens críticos
     *
     * @author Danilo Fernando
     */
    public AvaliacaoCreateUseCase(RepositoryAvaliacaoPort repositoryAvaliacaoPort,
                                  RepositoryDisciplinaPort repositoryDisciplinaPort,
                                  NotificadorUrgentePort notificadorUrgentePort) {
        this.repositoryAvaliacaoPort = repositoryAvaliacaoPort;
        this.repositoryDisciplinaPort = repositoryDisciplinaPort;
        this.notificadorUrgentePort = notificadorUrgentePort;
    }

    /**
     * Registra a avaliação do aluno para uma disciplina em que ele está matriculado
     * e notifica quando a urgência for ALTA.
     *
     * @param  request  payload com descrição, nota e disciplina
     * @param  alunoId  identificador do aluno autenticado (autor)
     * @return avaliação registrada, com os dados da disciplina
     *
     * @throws UnauthorizedOperationException  quando a disciplina não existe ou o aluno não está matriculado
     *
     * @author Danilo Fernando
     */
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
