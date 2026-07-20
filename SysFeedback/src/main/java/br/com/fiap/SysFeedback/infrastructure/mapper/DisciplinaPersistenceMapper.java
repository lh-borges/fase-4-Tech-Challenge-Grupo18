package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.Disciplina;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.DisciplinaJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Conversor entre a entidade de domínio {@link Disciplina} e a entidade de
 * persistência {@link DisciplinaJpaEntity}.
 */
@Mapper(componentModel = "spring")
public interface DisciplinaPersistenceMapper {

    default Disciplina toDomain(DisciplinaJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Disciplina(entity.getId(), entity.getNome(), entity.getCodigo());
    }

    @Mapping(target = "professores", ignore = true)
    @Mapping(target = "alunos", ignore = true)
    DisciplinaJpaEntity toJpa(Disciplina disciplina);
}
