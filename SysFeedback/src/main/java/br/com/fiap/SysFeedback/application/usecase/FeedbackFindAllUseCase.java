package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.FeedbackResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.FeedbackMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryFeedbackPort;

import java.util.List;

/**
 * Lista os feedbacks já gerados. Consumido pelo dono (roles PROFESSOR/ADMIN).
 *
 * @author luisbraserv
 */
public class FeedbackFindAllUseCase {

    private final RepositoryFeedbackPort repositoryFeedbackPort;

    /**
     * Cria o caso de uso com sua dependência.
     *
     * @param  repositoryFeedbackPort  porta de persistência de feedbacks
     *
     * @author luisbraserv
     */
    public FeedbackFindAllUseCase(RepositoryFeedbackPort repositoryFeedbackPort) {
        this.repositoryFeedbackPort = repositoryFeedbackPort;
    }

    /**
     * Retorna todos os feedbacks já gerados.
     *
     * @return lista de feedbacks
     *
     * @author luisbraserv
     */
    public List<FeedbackResponseDTO> execute() {
        return repositoryFeedbackPort.findAll()
                .stream()
                .map(FeedbackMapper::toResponse)
                .toList();
    }
}
