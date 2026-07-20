package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.Feedback;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.FeedbackJpaEntity;
import org.mapstruct.Mapper;

import java.util.LinkedHashMap;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface FeedbackPersistenceMapper {

    default FeedbackJpaEntity toJpa(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        FeedbackJpaEntity entity = new FeedbackJpaEntity();
        entity.setId(feedback.getId());
        entity.setPeriodoInicio(feedback.getPeriodoInicio());
        entity.setPeriodoFim(feedback.getPeriodoFim());
        entity.setMediaNotas(feedback.getMediaNotas());
        entity.setTotalAvaliacoes(feedback.getTotalAvaliacoes());
        entity.setAvaliacoesPorDia(new LinkedHashMap<>(feedback.getAvaliacoesPorDia()));
        entity.setAvaliacoesPorUrgencia(new LinkedHashMap<>(feedback.getAvaliacoesPorUrgencia()));
        entity.setGeradoEm(feedback.getGeradoEm());
        return entity;
    }

    default Feedback toDomain(FeedbackJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Feedback(
                entity.getId(),
                entity.getPeriodoInicio(),
                entity.getPeriodoFim(),
                entity.getMediaNotas(),
                entity.getTotalAvaliacoes(),
                new LinkedHashMap<>(entity.getAvaliacoesPorDia()),
                new LinkedHashMap<>(entity.getAvaliacoesPorUrgencia()),
                entity.getGeradoEm()
        );
    }

    default <K, V> LinkedHashMap<K, V> copyMap(Map<K, V> source) {
        if (source == null) {
            return new LinkedHashMap<>();
        }
        return new LinkedHashMap<>(source);
    }
}
