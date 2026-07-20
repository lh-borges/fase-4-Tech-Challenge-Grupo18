package br.com.fiap.sysfeedback.functions.relatorio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Estrutura do relatório semanal retornado pelo endpoint interno do backend
 * (POST /internal/relatorio/semanal). Espelha o {@code RelatorioSemanalDTO} do
 * Spring Boot: média, contagens por dia e por urgência, e a lista de avaliações
 * (descrição, urgência, data de envio).
 *
 * @param  periodoInicio           início do período coberto, em ISO-8601
 * @param  periodoFim              fim do período coberto, em ISO-8601
 * @param  mediaNotas              média das notas das avaliações no período
 * @param  totalAvaliacoes         quantidade total de avaliações no período
 * @param  avaliacoesPorDia        contagem de avaliações agrupadas por dia
 * @param  avaliacoesPorUrgencia   contagem de avaliações agrupadas por urgência
 * @param  avaliacoes              lista detalhada das avaliações do período
 *
 * @author Danilo Fernando
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

    /**
     * Avaliação individual listada no detalhamento do relatório.
     *
     * @param  descricao  texto da avaliação registrada pelo usuário
     * @param  urgencia   nível de urgência da avaliação (ex.: {@code ALTA})
     * @param  dataEnvio  data e hora do envio, em formato ISO-8601
     *
     * @author Danilo Fernando
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(String descricao, String urgencia, String dataEnvio) {
    }
}
