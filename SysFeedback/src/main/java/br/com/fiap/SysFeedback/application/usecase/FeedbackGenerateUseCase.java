package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.FeedbackRequestDTO;
import br.com.fiap.SysFeedback.application.dto.FeedbackResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.FeedbackMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryFeedbackPort;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.domain.entity.Feedback;

import java.util.List;

/**
 * Gera um feedback consolidado para o período informado pelo dono
 * (roles PROFESSOR/ADMIN): busca as avaliações do intervalo, calcula a média
 * e as contagens do relatório e persiste o snapshot.
 */
public class FeedbackGenerateUseCase {

    private final RepositoryAvaliacaoPort repositoryAvaliacaoPort;
    private final RepositoryFeedbackPort repositoryFeedbackPort;
    private final FeedbackMapper feedbackMapper;

    public FeedbackGenerateUseCase(RepositoryAvaliacaoPort repositoryAvaliacaoPort,
                                   RepositoryFeedbackPort repositoryFeedbackPort,
                                   FeedbackMapper feedbackMapper) {
        this.repositoryAvaliacaoPort = repositoryAvaliacaoPort;
        this.repositoryFeedbackPort = repositoryFeedbackPort;
        this.feedbackMapper = feedbackMapper;
    }

    public FeedbackResponseDTO execute(FeedbackRequestDTO request) {
        List<Avaliacao> avaliacoes =
                repositoryAvaliacaoPort.findByPeriodo(request.inicio(), request.fim());

        Feedback feedback = Feedback.gerar(request.inicio(), request.fim(), avaliacoes);
        Feedback salvo = repositoryFeedbackPort.save(feedback);

        return feedbackMapper.toResponse(salvo);
    }
}
