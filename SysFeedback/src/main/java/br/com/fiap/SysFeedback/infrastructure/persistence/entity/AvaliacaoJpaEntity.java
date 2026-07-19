package br.com.fiap.SysFeedback.infrastructure.persistence.entity;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA que mapeia a tabela {@code avaliacoes}, armazenando descrição,
 * nota, urgência e data de envio de cada avaliação.
 *
 * @author luisbraserv
 */
@Entity
@Table(name = "avaliacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(nullable = false)
    private int nota;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Urgencia urgencia;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio;

    /** Disciplina avaliada (a aula que recebeu o feedback). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private DisciplinaJpaEntity disciplina;

    /** Aluno autor da avaliação. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    private UserJpaEntity aluno;
}
