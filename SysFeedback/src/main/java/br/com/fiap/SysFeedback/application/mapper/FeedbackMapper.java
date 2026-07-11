package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.FeedbackResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.Feedback;

public class FeedbackMapper {

    // Feedback (domínio) → FeedbackResponseDTO (cliente)
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
