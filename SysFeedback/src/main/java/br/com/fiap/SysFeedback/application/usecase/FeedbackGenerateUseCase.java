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
 *
 * @author luisbraserv
 */
public class FeedbackGenerateUseCase {

    private final RepositoryAvaliacaoPort repositoryAvaliacaoPort;
    private final RepositoryFeedbackPort repositoryFeedbackPort;
    private final FeedbackMapper feedbackMapper;

    /**
     * Cria o caso de uso com suas dependências.
     *
     * @param  repositoryAvaliacaoPort  porta de persistência de avaliações
     * @param  repositoryFeedbackPort  porta de persistência de feedbacks
     *
     * @author luisbraserv
     */
    public FeedbackGenerateUseCase(RepositoryAvaliacaoPort repositoryAvaliacaoPort,
                                   RepositoryFeedbackPort repositoryFeedbackPort,
                                   FeedbackMapper feedbackMapper) {
        this.repositoryAvaliacaoPort = repositoryAvaliacaoPort;
        this.repositoryFeedbackPort = repositoryFeedbackPort;
        this.feedbackMapper = feedbackMapper;
    }

    /**
     * Gera e persiste o feedback consolidado do período informado.
     *
     * @param  request  período [inicio, fim] a consolidar
     * @return feedback consolidado e persistido
     *
     * @author luisbraserv
     */
    public FeedbackResponseDTO execute(FeedbackRequestDTO request) {
        List<Avaliacao> avaliacoes =
                repositoryAvaliacaoPort.findByPeriodo(request.inicio(), request.fim());

        Feedback feedback = Feedback.gerar(request.inicio(), request.fim(), avaliacoes);
        Feedback salvo = repositoryFeedbackPort.save(feedback);

        return feedbackMapper.toResponse(salvo);
    }
}
