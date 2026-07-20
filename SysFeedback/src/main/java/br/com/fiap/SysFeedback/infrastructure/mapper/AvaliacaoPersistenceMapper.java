package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvaliacaoPersistenceMapper {

    AvaliacaoJpaEntity toJpa(Avaliacao avaliacao);

    default Avaliacao toDomain(AvaliacaoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Avaliacao(
                entity.getId(),
                entity.getDescricao(),
                entity.getNota(),
                entity.getUrgencia(),
                entity.getDataEnvio()
        );
    }
}
