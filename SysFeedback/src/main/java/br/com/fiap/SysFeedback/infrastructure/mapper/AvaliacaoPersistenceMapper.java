package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AvaliacaoPersistenceMapper {

    default AvaliacaoJpaEntity toJpa(Avaliacao avaliacao) {
        if (avaliacao == null) {
            return null;
        }

        AvaliacaoJpaEntity entity = new AvaliacaoJpaEntity();
        entity.setId(avaliacao.getId());
        entity.setDescricao(avaliacao.getDescricao());
        entity.setNota(avaliacao.getNota());
        entity.setUrgencia(avaliacao.getUrgencia());
        entity.setDataEnvio(avaliacao.getDataEnvio());
        return entity;
    }

    default Avaliacao toDomain(AvaliacaoJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Avaliacao(
                entity.getId(),
                entity.getDescricao(),
                entity.getNota(),
                entity.getUrgencia(),
                entity.getDataEnvio(),
                disciplinaId(entity),
                alunoId(entity)
        );
    }

    private UUID disciplinaId(AvaliacaoJpaEntity entity) {
        return entity.getDisciplina() == null ? null : entity.getDisciplina().getId();
    }

    private UUID alunoId(AvaliacaoJpaEntity entity) {
        return entity.getAluno() == null ? null : entity.getAluno().getId();
    }
}
