package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Feedback consolidado retornado ao cliente, com os dados do relatório:
 * média das notas, total, avaliações por dia e avaliações por urgência.
 *
 * @param  id  identificador do feedback
 * @param  periodoInicio  início do período consolidado
 * @param  periodoFim  fim do período consolidado
 * @param  mediaNotas  média das notas das avaliações do período
 * @param  totalAvaliacoes  quantidade total de avaliações consideradas
 * @param  avaliacoesPorDia  contagem de avaliações agrupadas por dia
 * @param  avaliacoesPorUrgencia  contagem de avaliações agrupadas por urgência
 * @param  geradoEm  data e hora em que o feedback foi gerado
 *
 * @author luisbraserv
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
