package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.Disciplina;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.DisciplinaJpaEntity;

/**
 * Conversor entre a entidade de domínio {@link Disciplina} e a entidade de
 * persistência {@link DisciplinaJpaEntity}.
 *
 * @author Danilo Fernando
 */
public class DisciplinaPersistenceMapper {

    /**
     * Converte a entidade JPA na disciplina de domínio (apenas dados básicos).
     *
     * @param  entity  entidade JPA a converter
     * @return disciplina de domínio, ou {@code null} se a entrada for nula
     *
     * @author Danilo Fernando
     */
    public static Disciplina toDomain(DisciplinaJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Disciplina(entity.getId(), entity.getNome(), entity.getCodigo());
    }
}
