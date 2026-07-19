package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.FeedbackResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.Feedback;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    // Feedback (dominio) -> FeedbackResponseDTO (cliente)
    FeedbackResponseDTO toResponse(Feedback feedback);
}
