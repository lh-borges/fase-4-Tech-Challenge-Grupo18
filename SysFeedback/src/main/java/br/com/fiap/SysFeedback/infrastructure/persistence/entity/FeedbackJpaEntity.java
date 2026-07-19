package br.com.fiap.SysFeedback.infrastructure.persistence.entity;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Entidade JPA que mapeia a tabela {@code feedbacks}, consolidando o período,
 * a média das notas, o total de avaliações e as contagens por dia e por
 * urgência (mapeadas em tabelas de coleção associadas).
 *
 * @author luisbraserv
 */
@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDateTime periodoInicio;

    @Column(name = "periodo_fim", nullable = false)
    private LocalDateTime periodoFim;

    @Column(name = "media_notas", nullable = false)
    private double mediaNotas;

    @Column(name = "total_avaliacoes", nullable = false)
    private long totalAvaliacoes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "feedback_avaliacoes_por_dia",
            joinColumns = @JoinColumn(name = "feedback_id"))
    @MapKeyColumn(name = "dia")
    @Column(name = "quantidade")
    private Map<LocalDate, Long> avaliacoesPorDia = new LinkedHashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "feedback_avaliacoes_por_urgencia",
            joinColumns = @JoinColumn(name = "feedback_id"))
    @MapKeyColumn(name = "urgencia")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "quantidade")
    private Map<Urgencia, Long> avaliacoesPorUrgencia = new LinkedHashMap<>();

    @Column(name = "gerado_em", nullable = false)
    private LocalDateTime geradoEm;
}
