package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.Feedback;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.FeedbackJpaEntity;

import java.util.LinkedHashMap;

/**
 * Conversor entre a entidade de domínio {@link Feedback} e a entidade de
 * persistência {@link FeedbackJpaEntity}, copiando os mapas de contagem para
 * isolar as instâncias.
 *
 * @author luisbraserv
 */
public class FeedbackPersistenceMapper {

    /**
     * Converte um feedback de domínio na entidade JPA correspondente.
     *
     * @param  feedback  feedback de domínio a converter
     * @return entidade JPA equivalente, ou {@code null} se a entrada for nula
     *
     * @author luisbraserv
     */
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

    /**
     * Converte uma entidade JPA no feedback de domínio correspondente.
     *
     * @param  entity  entidade JPA a converter
     * @return feedback de domínio equivalente, ou {@code null} se a entrada for nula
     *
     * @author luisbraserv
     */
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
