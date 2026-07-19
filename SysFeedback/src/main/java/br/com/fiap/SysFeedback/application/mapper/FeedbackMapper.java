package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.FeedbackResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.Feedback;

/**
 * Converte a entidade de feedback no DTO retornado ao cliente.
 *
 * @author luisbraserv
 */
public class FeedbackMapper {

    /**
     * Converte a entidade de domínio no DTO de resposta.
     *
     * @param  feedback  entidade de domínio
     * @return DTO de resposta, ou {@code null} se o feedback for nulo
     *
     * @author luisbraserv
     */
    public static FeedbackResponseDTO toResponse(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        return new FeedbackResponseDTO(
                feedback.getId(),
                feedback.getPeriodoInicio(),
                feedback.getPeriodoFim(),
                feedback.getMediaNotas(),
                feedback.getTotalAvaliacoes(),
                feedback.getAvaliacoesPorDia(),
                feedback.getAvaliacoesPorUrgencia(),
                feedback.getGeradoEm()
        );
    }
}
