package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Feedback consolidado retornado ao cliente, com os dados do relatório:
 * média das notas, total, avaliações por dia e avaliações por urgência.
 */
public record FeedbackResponseDTO(
        UUID id,
        LocalDateTime periodoInicio,
        LocalDateTime periodoFim,
        double mediaNotas,
        long totalAvaliacoes,
        Map<LocalDate, Long> avaliacoesPorDia,
        Map<Urgencia, Long> avaliacoesPorUrgencia,
        LocalDateTime geradoEm
) {
}
