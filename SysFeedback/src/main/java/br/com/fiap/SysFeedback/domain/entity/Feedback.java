package br.com.fiap.SysFeedback.domain.entity;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.domain.exception.PeriodoInvalidoException;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Feedback consolidado gerado pelo dono (roles PROFESSOR/ADMIN) para um período.
 *
 * <p>Agrega as avaliações recebidas entre {@code periodoInicio} e {@code periodoFim},
 * produzindo a média das notas e as contagens necessárias para o relatório semanal
 * (avaliações por dia e por urgência). É persistido como um snapshot do período.</p>
 */
@Getter
public class Feedback {

    private UUID id;
    private final LocalDateTime periodoInicio;
    private final LocalDateTime periodoFim;
    private final double mediaNotas;
    private final long totalAvaliacoes;
    private final Map<LocalDate, Long> avaliacoesPorDia;
    private final Map<Urgencia, Long> avaliacoesPorUrgencia;
    private final LocalDateTime geradoEm;

    /** Reconstrução a partir da persistência. */
    public Feedback(UUID id,
                    LocalDateTime periodoInicio,
                    LocalDateTime periodoFim,
                    double mediaNotas,
                    long totalAvaliacoes,
                    Map<LocalDate, Long> avaliacoesPorDia,
                    Map<Urgencia, Long> avaliacoesPorUrgencia,
                    LocalDateTime geradoEm) {
        this.id = id;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
        this.mediaNotas = mediaNotas;
        this.totalAvaliacoes = totalAvaliacoes;
        this.avaliacoesPorDia = avaliacoesPorDia;
        this.avaliacoesPorUrgencia = avaliacoesPorUrgencia;
        this.geradoEm = geradoEm;
    }

    private Feedback(LocalDateTime periodoInicio,
                     LocalDateTime periodoFim,
                     List<Avaliacao> avaliacoes) {
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
        this.totalAvaliacoes = avaliacoes.size();
        this.mediaNotas = avaliacoes.stream()
                .mapToInt(Avaliacao::getNota)
                .average()
                .orElse(0.0);
        this.avaliacoesPorDia = avaliacoes.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDataEnvio().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.counting()));
        this.avaliacoesPorUrgencia = avaliacoes.stream()
                .collect(Collectors.groupingBy(
                        Avaliacao::getUrgencia,
                        LinkedHashMap::new,
                        Collectors.counting()));
        this.geradoEm = LocalDateTime.now();
    }

    /**
     * Gera um feedback consolidado a partir das avaliações de um período.
     *
     * @param periodoInicio início do período (inclusivo)
     * @param periodoFim    fim do período (inclusivo)
     * @param avaliacoes    avaliações compreendidas no período
     * @return feedback consolidado (ainda sem id, atribuído na persistência)
     */
    public static Feedback gerar(LocalDateTime periodoInicio,
                                 LocalDateTime periodoFim,
                                 List<Avaliacao> avaliacoes) {
        if (periodoInicio == null || periodoFim == null) {
            throw new PeriodoInvalidoException("Período de início e fim são obrigatórios");
        }
        if (periodoFim.isBefore(periodoInicio)) {
            throw new PeriodoInvalidoException("Data fim não pode ser anterior à data início");
        }
        return new Feedback(periodoInicio, periodoFim, avaliacoes);
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
