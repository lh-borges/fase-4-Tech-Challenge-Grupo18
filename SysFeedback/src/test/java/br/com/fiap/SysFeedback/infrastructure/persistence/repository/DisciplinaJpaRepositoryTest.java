package br.com.fiap.SysFeedback.infrastructure.persistence.repository;

import br.com.fiap.SysFeedback.SysFeedbackApplication;
import br.com.fiap.SysFeedback.domain.enums.Role;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.DisciplinaJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = SysFeedbackApplication.class)
@EntityScan(basePackages = "br.com.fiap.SysFeedback.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "br.com.fiap.SysFeedback.infrastructure.persistence.repository")
class DisciplinaJpaRepositoryTest {

    @Autowired
    private DisciplinaJpaRepository disciplinaRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Test
    void deveSalvarEListarDisciplina() {
        DisciplinaJpaEntity saved = disciplinaRepository.save(new DisciplinaJpaEntity("Arquitetura", "ARQ"));

        List<DisciplinaJpaEntity> result = disciplinaRepository.findAll();

        assertNotNull(saved.getId());
        assertEquals(1, result.size());
        assertEquals("Arquitetura", result.get(0).getNome());
        assertEquals("ARQ", result.get(0).getCodigo());
    }

    @Test
    void deveBuscarDisciplinasPorProfessor() {
        UserJpaEntity professor = userRepository.save(user("professor@fiap.com", Role.PROFESSOR));
        UserJpaEntity outroProfessor = userRepository.save(user("outro.professor@fiap.com", Role.PROFESSOR));
        DisciplinaJpaEntity arquitetura = disciplina("Arquitetura", "ARQ", Set.of(professor), Set.of());
        DisciplinaJpaEntity devops = disciplina("DevOps", "DEVOPS", Set.of(outroProfessor), Set.of());
        disciplinaRepository.saveAll(List.of(arquitetura, devops));

        List<DisciplinaJpaEntity> result = disciplinaRepository.findByProfessores_Id(professor.getId());

        assertEquals(1, result.size());
        assertEquals("Arquitetura", result.get(0).getNome());
    }

    @Test
    void deveBuscarDisciplinasPorAluno() {
        UserJpaEntity aluno = userRepository.save(user("aluno@fiap.com", Role.ALUNO));
        UserJpaEntity outroAluno = userRepository.save(user("outro.aluno@fiap.com", Role.ALUNO));
        DisciplinaJpaEntity arquitetura = disciplina("Arquitetura", "ARQ", Set.of(), Set.of(aluno));
        DisciplinaJpaEntity devops = disciplina("DevOps", "DEVOPS", Set.of(), Set.of(outroAluno));
        disciplinaRepository.saveAll(List.of(arquitetura, devops));

        List<DisciplinaJpaEntity> result = disciplinaRepository.findByAlunos_Id(aluno.getId());

        assertEquals(1, result.size());
        assertEquals("Arquitetura", result.get(0).getNome());
    }

    @Test
    void deveVerificarSeAlunoEstaMatriculado() {
        UserJpaEntity aluno = userRepository.save(user("aluno@fiap.com", Role.ALUNO));
        UserJpaEntity outroAluno = userRepository.save(user("outro.aluno@fiap.com", Role.ALUNO));
        DisciplinaJpaEntity disciplina =
                disciplinaRepository.save(disciplina("Arquitetura", "ARQ", Set.of(), Set.of(aluno)));

        assertTrue(disciplinaRepository.existsByIdAndAlunos_Id(disciplina.getId(), aluno.getId()));
        assertFalse(disciplinaRepository.existsByIdAndAlunos_Id(disciplina.getId(), outroAluno.getId()));
    }

    @Test
    void deveFalharAoSalvarDisciplinaSemNome() {
        DisciplinaJpaEntity entity = new DisciplinaJpaEntity(null, "ARQ");

        assertThrows(DataIntegrityViolationException.class, () -> disciplinaRepository.saveAndFlush(entity));
    }

    @Test
    void deveFalharAoSalvarDisciplinaSemCodigo() {
        DisciplinaJpaEntity entity = new DisciplinaJpaEntity("Arquitetura", null);

        assertThrows(DataIntegrityViolationException.class, () -> disciplinaRepository.saveAndFlush(entity));
    }

    private DisciplinaJpaEntity disciplina(String nome, String codigo,
                                           Set<UserJpaEntity> professores,
                                           Set<UserJpaEntity> alunos) {
        DisciplinaJpaEntity disciplina = new DisciplinaJpaEntity(nome, codigo);
        disciplina.setProfessores(new java.util.HashSet<>(professores));
        disciplina.setAlunos(new java.util.HashSet<>(alunos));
        return disciplina;
    }

    private UserJpaEntity user(String email, Role role) {
        UserJpaEntity user = new UserJpaEntity();
        user.setName("Usuario Teste");
        user.setEmail(email);
        user.setPassword("123456");
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.of(2026, 7, 20, 10, 0));
        return user;
    }
}
