package br.com.fiap.SysFeedback.infrastructure.persistence.adapter;

import br.com.fiap.SysFeedback.domain.entity.Disciplina;
import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.mapper.DisciplinaPersistenceMapper;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.DisciplinaJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.DisciplinaJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DisciplinaRepositoryAdapterTest {

    private final DisciplinaJpaRepository disciplinaJpaRepository = mock(DisciplinaJpaRepository.class);
    private final DisciplinaPersistenceMapper disciplinaPersistenceMapper = mock(DisciplinaPersistenceMapper.class);

    private DisciplinaRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new DisciplinaRepositoryAdapter(disciplinaJpaRepository, disciplinaPersistenceMapper);
    }

    @Test
    void deveListarTodasDisciplinasMapeadasParaDominio() {
        DisciplinaJpaEntity entity = disciplinaJpaEntity();
        Disciplina disciplina = disciplina();
        when(disciplinaJpaRepository.findAll()).thenReturn(List.of(entity));
        when(disciplinaPersistenceMapper.toDomain(entity)).thenReturn(disciplina);

        List<Disciplina> result = adapter.findAll();

        assertEquals(1, result.size());
        assertEquals(disciplina, result.get(0));
        verify(disciplinaJpaRepository).findAll();
        verify(disciplinaPersistenceMapper).toDomain(entity);
    }

    @Test
    void deveBuscarDisciplinasPorProfessor() {
        UUID professorId = UUID.randomUUID();
        DisciplinaJpaEntity entity = disciplinaJpaEntity();
        Disciplina disciplina = disciplina();
        when(disciplinaJpaRepository.findByProfessores_Id(professorId)).thenReturn(List.of(entity));
        when(disciplinaPersistenceMapper.toDomain(entity)).thenReturn(disciplina);

        List<Disciplina> result = adapter.findByProfessor(professorId);

        assertEquals(List.of(disciplina), result);
        verify(disciplinaJpaRepository).findByProfessores_Id(professorId);
    }

    @Test
    void deveBuscarDisciplinasPorAluno() {
        UUID alunoId = UUID.randomUUID();
        DisciplinaJpaEntity entity = disciplinaJpaEntity();
        Disciplina disciplina = disciplina();
        when(disciplinaJpaRepository.findByAlunos_Id(alunoId)).thenReturn(List.of(entity));
        when(disciplinaPersistenceMapper.toDomain(entity)).thenReturn(disciplina);

        List<Disciplina> result = adapter.findByAluno(alunoId);

        assertEquals(List.of(disciplina), result);
        verify(disciplinaJpaRepository).findByAlunos_Id(alunoId);
    }

    @Test
    void deveBuscarDisciplinaPorIdQuandoExistir() {
        DisciplinaJpaEntity entity = disciplinaJpaEntity();
        Disciplina disciplina = disciplina();
        when(disciplinaJpaRepository.findById(Fixture.DISCIPLINA_ID)).thenReturn(Optional.of(entity));
        when(disciplinaPersistenceMapper.toDomain(entity)).thenReturn(disciplina);

        Optional<Disciplina> result = adapter.findById(Fixture.DISCIPLINA_ID);

        assertTrue(result.isPresent());
        assertEquals(disciplina, result.get());
        verify(disciplinaJpaRepository).findById(Fixture.DISCIPLINA_ID);
    }

    @Test
    void deveRetornarVazioQuandoDisciplinaNaoExistir() {
        when(disciplinaJpaRepository.findById(Fixture.DISCIPLINA_ID)).thenReturn(Optional.empty());

        Optional<Disciplina> result = adapter.findById(Fixture.DISCIPLINA_ID);

        assertTrue(result.isEmpty());
        verify(disciplinaJpaRepository).findById(Fixture.DISCIPLINA_ID);
    }

    @Test
    void deveVerificarSeAlunoEstaMatriculado() {
        UUID alunoId = Fixture.USER_ID;
        UUID disciplinaId = Fixture.DISCIPLINA_ID;
        when(disciplinaJpaRepository.existsByIdAndAlunos_Id(disciplinaId, alunoId)).thenReturn(true);

        boolean result = adapter.alunoMatriculado(alunoId, disciplinaId);

        assertTrue(result);
        verify(disciplinaJpaRepository).existsByIdAndAlunos_Id(disciplinaId, alunoId);
    }

    @Test
    void deveRetornarFalseQuandoAlunoNaoEstaMatriculado() {
        UUID alunoId = Fixture.USER_ID;
        UUID disciplinaId = Fixture.DISCIPLINA_ID;
        when(disciplinaJpaRepository.existsByIdAndAlunos_Id(disciplinaId, alunoId)).thenReturn(false);

        boolean result = adapter.alunoMatriculado(alunoId, disciplinaId);

        assertFalse(result);
        verify(disciplinaJpaRepository).existsByIdAndAlunos_Id(disciplinaId, alunoId);
    }

    private DisciplinaJpaEntity disciplinaJpaEntity() {
        DisciplinaJpaEntity entity = new DisciplinaJpaEntity(Fixture.DISCIPLINA_NOME, "ARQ");
        entity.setId(Fixture.DISCIPLINA_ID);
        return entity;
    }

    private Disciplina disciplina() {
        return new Disciplina(Fixture.DISCIPLINA_ID, Fixture.DISCIPLINA_NOME, "ARQ");
    }
}
