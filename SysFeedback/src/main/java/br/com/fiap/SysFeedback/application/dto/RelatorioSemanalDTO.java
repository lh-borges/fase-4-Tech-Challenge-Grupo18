package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Relatório semanal consumido pela Cloud Function {@code relatorio-semanal} para
 * montar o e-mail. Reúne os "Dados para o relatório semanal" do enunciado:
 * descrição, urgência e data de envio de cada avaliação, além das contagens por
 * dia e por urgência — e a média das notas do período.
 *
 * @param  periodoInicio  início do período coberto pelo relatório
 * @param  periodoFim  fim do período coberto pelo relatório
 * @param  mediaNotas  média das notas das avaliações do período
 * @param  totalAvaliacoes  quantidade total de avaliações no período
 * @param  avaliacoesPorDia  contagem de avaliações agrupadas por dia
 * @param  avaliacoesPorUrgencia  contagem de avaliações agrupadas por urgência
 * @param  avaliacoes  lista das avaliações individuais do período
 *
 * @author Danilo Fernando
 */
public record RelatorioSemanalDTO(
        LocalDateTime periodoInicio,
        LocalDateTime periodoFim,
        double mediaNotas,
        long totalAvaliacoes,
        Map<LocalDate, Long> avaliacoesPorDia,
        Map<Urgencia, Long> avaliacoesPorUrgencia,
        List<ItemAvaliacao> avaliacoes
) {

    /**
     * Item individual do relatório (descrição, urgência, data de envio).
     *
     * @param  descricao  texto descritivo da avaliação
     * @param  urgencia  nível de urgência classificado da avaliação
     * @param  dataEnvio  data e hora em que a avaliação foi enviada
     *
     * @author Danilo Fernando
     */
    public record ItemAvaliacao(String descricao, Urgencia urgencia, LocalDateTime dataEnvio) {
    }
}
