package br.com.fiap.SysFeedback.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidade JPA da tabela {@code disciplinas} e das relações de ensino e matrícula.
 *
 * <p>É o lado dono de duas relações N:N com {@link UserJpaEntity}:
 * {@code professor_disciplina} (professores que lecionam) e {@code aluno_disciplina}
 * (alunos matriculados).</p>
 *
 * @author Danilo Fernando
 */
@Entity
@Table(name = "disciplinas")
@Getter
@Setter
@NoArgsConstructor
public class DisciplinaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String codigo;

    @ManyToMany
    @JoinTable(
            name = "professor_disciplina",
            joinColumns = @JoinColumn(name = "disciplina_id"),
            inverseJoinColumns = @JoinColumn(name = "professor_id")
    )
    private Set<UserJpaEntity> professores = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "aluno_disciplina",
            joinColumns = @JoinColumn(name = "disciplina_id"),
            inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )
    private Set<UserJpaEntity> alunos = new HashSet<>();

    /**
     * Cria uma disciplina com nome e código (sem relações).
     *
     * @param  nome  nome da disciplina
     * @param  codigo  código curto identificador
     *
     * @author Danilo Fernando
     */
    public DisciplinaJpaEntity(String nome, String codigo) {
        this.nome = nome;
        this.codigo = codigo;
    }
}
