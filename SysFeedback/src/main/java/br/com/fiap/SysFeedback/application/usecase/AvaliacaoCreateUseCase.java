package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.AvaliacaoMapper;
import br.com.fiap.SysFeedback.application.messaging.NotificadorUrgentePort;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;

/**
 * Registra uma nova avaliação enviada por um usuário (role ALUNO).
 *
 * <p>Quando a avaliação registrada é crítica (urgência ALTA), dispara uma
 * notificação assíncrona via {@link NotificadorUrgentePort} (Pub/Sub → Cloud
 * Function de e-mail). A regra de "o que é crítico" vive aqui/no domínio; a
 * infraestrutura só transporta o evento.</p>
 */
public class AvaliacaoCreateUseCase {

    private final RepositoryAvaliacaoPort repositoryAvaliacaoPort;
    private final NotificadorUrgentePort notificadorUrgentePort;

    public AvaliacaoCreateUseCase(RepositoryAvaliacaoPort repositoryAvaliacaoPort,
                                  NotificadorUrgentePort notificadorUrgentePort) {
        this.repositoryAvaliacaoPort = repositoryAvaliacaoPort;
        this.notificadorUrgentePort = notificadorUrgentePort;
    }

    public AvaliacaoResponseDTO execute(AvaliacaoRequestDTO request) {
        Avaliacao avaliacao = AvaliacaoMapper.toDomain(request);
        Avaliacao salva = repositoryAvaliacaoPort.save(avaliacao);

        if (salva.getUrgencia() == Urgencia.ALTA) {
            notificadorUrgentePort.notificarUrgente(salva);
        }

        return AvaliacaoMapper.toResponse(salva);
    }
}
