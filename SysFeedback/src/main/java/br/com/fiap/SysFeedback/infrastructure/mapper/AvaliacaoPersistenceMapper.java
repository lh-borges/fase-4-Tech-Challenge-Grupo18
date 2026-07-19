package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;

/**
 * Conversor da entidade de persistência {@link AvaliacaoJpaEntity} para a entidade
 * de domínio {@link Avaliacao}.
 *
 * <p>A conversão para JPA (incluindo as relações com disciplina e aluno) é feita no
 * {@code AvaliacaoRepositoryAdapter}, que tem acesso às referências gerenciadas.</p>
 *
 * @author luisbraserv
 */
public class AvaliacaoPersistenceMapper {

    /**
     * Converte uma entidade JPA na avaliação de domínio, incluindo os identificadores
     * de disciplina e aluno.
     *
     * @param  entity  entidade JPA a converter
     * @return avaliação de domínio equivalente, ou {@code null} se a entrada for nula
     *
     * @author luisbraserv
     */
    public static Avaliacao toDomain(AvaliacaoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Avaliacao(
                entity.getId(),
                entity.getDescricao(),
                entity.getNota(),
                entity.getUrgencia(),
                entity.getDataEnvio(),
                entity.getDisciplina() != null ? entity.getDisciplina().getId() : null,
                entity.getAluno() != null ? entity.getAluno().getId() : null
        );
    }
}
