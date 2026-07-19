package br.com.fiap.SysFeedback.infrastructure.persistence.adapter;

import br.com.fiap.SysFeedback.application.repository.RepositoryDisciplinaPort;
import br.com.fiap.SysFeedback.domain.entity.Disciplina;
import br.com.fiap.SysFeedback.infrastructure.mapper.DisciplinaPersistenceMapper;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.DisciplinaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de persistência que implementa {@link RepositoryDisciplinaPort}
 * delegando ao repositório JPA e traduzindo para o domínio.
 *
 * @author Danilo Fernando
 */
@Repository
@RequiredArgsConstructor
public class DisciplinaRepositoryAdapter implements RepositoryDisciplinaPort {

    private final DisciplinaJpaRepository disciplinaJpaRepository;

    /**
     * {@inheritDoc}
     *
     * @author Danilo Fernando
     */
    @Override
    public List<Disciplina> findAll() {
        return disciplinaJpaRepository.findAll().stream()
                .map(DisciplinaPersistenceMapper::toDomain)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * @author Danilo Fernando
     */
    @Override
    public List<Disciplina> findByProfessor(UUID professorId) {
        return disciplinaJpaRepository.findByProfessores_Id(professorId).stream()
                .map(DisciplinaPersistenceMapper::toDomain)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * @author Danilo Fernando
     */
    @Override
    public List<Disciplina> findByAluno(UUID alunoId) {
        return disciplinaJpaRepository.findByAlunos_Id(alunoId).stream()
                .map(DisciplinaPersistenceMapper::toDomain)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * @author Danilo Fernando
     */
    @Override
    public Optional<Disciplina> findById(UUID id) {
        return disciplinaJpaRepository.findById(id).map(DisciplinaPersistenceMapper::toDomain);
    }

    /**
     * {@inheritDoc}
     *
     * @author Danilo Fernando
     */
    @Override
    public boolean alunoMatriculado(UUID alunoId, UUID disciplinaId) {
        return disciplinaJpaRepository.existsByIdAndAlunos_Id(disciplinaId, alunoId);
    }
}
