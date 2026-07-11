package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.Feedback;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.FeedbackJpaEntity;

import java.util.LinkedHashMap;

public class FeedbackPersistenceMapper {

    public static FeedbackJpaEntity toJpa(Feedback feedback) {
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

    public static Feedback toDomain(FeedbackJpaEntity entity) {
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
}
