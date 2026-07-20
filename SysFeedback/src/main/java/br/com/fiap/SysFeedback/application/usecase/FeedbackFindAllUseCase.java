package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.FeedbackResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.FeedbackMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryFeedbackPort;

import java.util.List;

/**
 * Lista os feedbacks já gerados. Consumido pelo dono (roles PROFESSOR/ADMIN).
 */
public class FeedbackFindAllUseCase {

    private final RepositoryFeedbackPort repositoryFeedbackPort;
    private final FeedbackMapper feedbackMapper;

    public FeedbackFindAllUseCase(RepositoryFeedbackPort repositoryFeedbackPort,
                                  FeedbackMapper feedbackMapper) {
        this.repositoryFeedbackPort = repositoryFeedbackPort;
        this.feedbackMapper = feedbackMapper;
    }

    public List<FeedbackResponseDTO> execute() {
        return repositoryFeedbackPort.findAll()
                .stream()
                .map(feedbackMapper::toResponse)
                .toList();
    }
}
