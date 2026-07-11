package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;

public class AvaliacaoPersistenceMapper {

    public static AvaliacaoJpaEntity toJpa(Avaliacao avaliacao) {
        if (avaliacao == null) {
            return null;
        }
        return new AvaliacaoJpaEntity(
                avaliacao.getId(),
                avaliacao.getDescricao(),
                avaliacao.getNota(),
                avaliacao.getUrgencia(),
                avaliacao.getDataEnvio()
        );
    }

    public static Avaliacao toDomain(AvaliacaoJpaEntity entity) {
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
