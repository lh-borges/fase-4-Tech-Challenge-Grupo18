package br.com.fiap.SysFeedback.application.repository;

import br.com.fiap.SysFeedback.domain.entity.Disciplina;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saída para consulta de disciplinas e das relações de ensino e matrícula.
 *
 * @author Danilo Fernando
 */
public interface RepositoryDisciplinaPort {

    /**
     * Lista todas as disciplinas cadastradas.
     *
     * @return lista de todas as disciplinas
     *
     * @author Danilo Fernando
     */
    List<Disciplina> findAll();

    /**
     * Lista as disciplinas lecionadas por um professor.
     *
     * @param  professorId  identificador do professor
     * @return disciplinas que o professor leciona
     *
     * @author Danilo Fernando
     */
    List<Disciplina> findByProfessor(UUID professorId);

    /**
     * Lista as disciplinas em que um aluno está matriculado.
     *
     * @param  alunoId  identificador do aluno
     * @return disciplinas em que o aluno está matriculado
     *
     * @author Danilo Fernando
     */
    List<Disciplina> findByAluno(UUID alunoId);

    /**
     * Busca uma disciplina pelo identificador.
     *
     * @param  id  identificador da disciplina
     * @return disciplina encontrada, ou vazio se não existir
     *
     * @author Danilo Fernando
     */
    Optional<Disciplina> findById(UUID id);

    /**
     * Verifica se um aluno está matriculado em uma disciplina.
     *
     * @param  alunoId  identificador do aluno
     * @param  disciplinaId  identificador da disciplina
     * @return {@code true} se houver matrícula, caso contrário {@code false}
     *
     * @author Danilo Fernando
     */
    boolean alunoMatriculado(UUID alunoId, UUID disciplinaId);
}
