package br.com.fiap.SysFeedback.infrastructure.persistence.repository;

import br.com.fiap.SysFeedback.infrastructure.persistence.entity.DisciplinaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório Spring Data JPA para {@link DisciplinaJpaEntity}, incluindo consultas
 * pelas relações de ensino e matrícula.
 *
 * @author Danilo Fernando
 */
@Repository
public interface DisciplinaJpaRepository extends JpaRepository<DisciplinaJpaEntity, UUID> {

    /**
     * Lista as disciplinas lecionadas por um professor.
     *
     * @param  professorId  identificador do professor
     * @return disciplinas em que o professor consta como docente
     *
     * @author Danilo Fernando
     */
    List<DisciplinaJpaEntity> findByProfessores_Id(UUID professorId);

    /**
     * Lista as disciplinas em que um aluno está matriculado.
     *
     * @param  alunoId  identificador do aluno
     * @return disciplinas em que o aluno consta como matriculado
     *
     * @author Danilo Fernando
     */
    List<DisciplinaJpaEntity> findByAlunos_Id(UUID alunoId);

    /**
     * Verifica se um aluno está matriculado em uma disciplina específica.
     *
     * @param  id  identificador da disciplina
     * @param  alunoId  identificador do aluno
     * @return {@code true} se o aluno estiver matriculado na disciplina
     *
     * @author Danilo Fernando
     */
    boolean existsByIdAndAlunos_Id(UUID id, UUID alunoId);
}
