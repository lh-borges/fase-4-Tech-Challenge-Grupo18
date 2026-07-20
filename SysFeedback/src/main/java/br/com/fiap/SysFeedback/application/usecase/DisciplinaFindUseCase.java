package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.DisciplinaResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.DisciplinaMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryDisciplinaPort;
import br.com.fiap.SysFeedback.domain.entity.Disciplina;
import br.com.fiap.SysFeedback.domain.enums.Role;

import java.util.List;
import java.util.UUID;

/**
 * Lista as disciplinas visíveis para o usuário conforme o seu perfil.
 *
 * <ul>
 *     <li>ALUNO — disciplinas em que está matriculado (para escolher qual avaliar).</li>
 *     <li>PROFESSOR — disciplinas que leciona.</li>
 *     <li>ADMIN — todas as disciplinas.</li>
 * </ul>
 *
 * @author Danilo Fernando
 */
public class DisciplinaFindUseCase {

    private final RepositoryDisciplinaPort repositoryDisciplinaPort;
    private final DisciplinaMapper disciplinaMapper;

    /**
     * Cria o caso de uso com a porta de disciplinas.
     *
     * @param  repositoryDisciplinaPort  porta de consulta de disciplinas
     *
     * @author Danilo Fernando
     */
    public DisciplinaFindUseCase(RepositoryDisciplinaPort repositoryDisciplinaPort,
                                 DisciplinaMapper disciplinaMapper) {
        this.repositoryDisciplinaPort = repositoryDisciplinaPort;
        this.disciplinaMapper = disciplinaMapper;
    }

    /**
     * Retorna as disciplinas visíveis para o usuário conforme o perfil.
     *
     * @param  role  perfil do usuário autenticado
     * @param  userId  identificador do usuário autenticado
     * @return disciplinas visíveis para o usuário
     *
     * @author Danilo Fernando
     */
    public List<DisciplinaResponseDTO> execute(Role role, UUID userId) {
        List<Disciplina> disciplinas = switch (role) {
            case ALUNO -> repositoryDisciplinaPort.findByAluno(userId);
            case PROFESSOR -> repositoryDisciplinaPort.findByProfessor(userId);
            case ADMIN -> repositoryDisciplinaPort.findAll();
        };
        return disciplinas.stream().map(disciplinaMapper::toResponse).toList();
    }
}
