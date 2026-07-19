package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.application.mapper.AvaliacaoMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryDisciplinaPort;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.domain.entity.Disciplina;
import br.com.fiap.SysFeedback.domain.enums.Role;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Lista avaliações conforme o perfil do usuário e um filtro opcional de disciplina.
 *
 * <ul>
 *     <li>ADMIN — todas as avaliações (ou de uma disciplina, se filtrada).</li>
 *     <li>PROFESSOR — apenas das disciplinas que leciona (ou de uma delas, se filtrada),
 *         permitindo ver cada disciplina separadamente.</li>
 * </ul>
 *
 * @author Danilo Fernando
 */
public class AvaliacaoFindUseCase {

    private final RepositoryAvaliacaoPort repositoryAvaliacaoPort;
    private final RepositoryDisciplinaPort repositoryDisciplinaPort;

    /**
     * Cria o caso de uso com as portas de avaliações e disciplinas.
     *
     * @param  repositoryAvaliacaoPort  porta de consulta de avaliações
     * @param  repositoryDisciplinaPort  porta de consulta de disciplinas
     *
     * @author Danilo Fernando
     */
    public AvaliacaoFindUseCase(RepositoryAvaliacaoPort repositoryAvaliacaoPort,
                                RepositoryDisciplinaPort repositoryDisciplinaPort) {
        this.repositoryAvaliacaoPort = repositoryAvaliacaoPort;
        this.repositoryDisciplinaPort = repositoryDisciplinaPort;
    }

    /**
     * Retorna as avaliações visíveis ao usuário, opcionalmente filtradas por disciplina.
     *
     * @param  role  perfil do usuário autenticado
     * @param  userId  identificador do usuário autenticado
     * @param  disciplinaFiltro  disciplina a filtrar, ou {@code null} para todas as permitidas
     * @return avaliações visíveis, já com o nome da disciplina
     *
     * @author Danilo Fernando
     */
    public List<AvaliacaoResponseDTO> execute(Role role, UUID userId, UUID disciplinaFiltro) {
        Map<UUID, String> nomesPorDisciplina = escopoDisciplinas(role, userId).stream()
                .collect(Collectors.toMap(Disciplina::getId, Disciplina::getNome));

        List<Avaliacao> avaliacoes = buscarAvaliacoes(role, nomesPorDisciplina.keySet(), disciplinaFiltro);

        return avaliacoes.stream()
                .map(a -> AvaliacaoMapper.toResponse(a, nomesPorDisciplina.get(a.getDisciplinaId())))
                .toList();
    }

    /** Disciplinas cujo escopo o usuário pode ver (todas para admin; as lecionadas para professor). */
    private List<Disciplina> escopoDisciplinas(Role role, UUID userId) {
        return role == Role.PROFESSOR
                ? repositoryDisciplinaPort.findByProfessor(userId)
                : repositoryDisciplinaPort.findAll();
    }

    private List<Avaliacao> buscarAvaliacoes(Role role, java.util.Set<UUID> idsPermitidos, UUID disciplinaFiltro) {
        if (role == Role.ADMIN) {
            return disciplinaFiltro != null
                    ? repositoryAvaliacaoPort.findByDisciplinaIds(List.of(disciplinaFiltro))
                    : repositoryAvaliacaoPort.findAll();
        }

        // PROFESSOR: restringe às disciplinas que leciona.
        List<UUID> ids = disciplinaFiltro != null
                ? (idsPermitidos.contains(disciplinaFiltro) ? List.of(disciplinaFiltro) : List.of())
                : List.copyOf(idsPermitidos);

        return ids.isEmpty() ? List.of() : repositoryAvaliacaoPort.findByDisciplinaIds(ids);
    }
}
