package br.com.fiap.sysfeedback.functions.relatorio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Estrutura do relatório semanal retornado pelo endpoint interno do backend
 * (POST /internal/relatorio/semanal). Espelha o {@code RelatorioSemanalDTO} do
 * Spring Boot: média, contagens por dia e por urgência, e a lista de avaliações
 * (descrição, urgência, data de envio).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RelatorioSemanal(
        String periodoInicio,
        String periodoFim,
        double mediaNotas,
        long totalAvaliacoes,
        Map<String, Long> avaliacoesPorDia,
        Map<String, Long> avaliacoesPorUrgencia,
        List<Item> avaliacoes) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(String descricao, String urgencia, String dataEnvio) {
    }
}
