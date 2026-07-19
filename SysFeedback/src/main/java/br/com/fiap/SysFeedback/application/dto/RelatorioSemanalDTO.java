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

    /** Item individual do relatório (descrição, urgência, data de envio). */
    public record ItemAvaliacao(String descricao, Urgencia urgencia, LocalDateTime dataEnvio) {
    }
}
