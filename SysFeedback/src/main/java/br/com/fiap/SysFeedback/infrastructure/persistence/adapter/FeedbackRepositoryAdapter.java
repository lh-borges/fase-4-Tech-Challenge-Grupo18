package br.com.fiap.SysFeedback.infrastructure.persistence.adapter;

import br.com.fiap.SysFeedback.application.repository.RepositoryFeedbackPort;
import br.com.fiap.SysFeedback.domain.entity.Feedback;
import br.com.fiap.SysFeedback.infrastructure.mapper.FeedbackPersistenceMapper;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.FeedbackJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.FeedbackJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Adaptador de persistência que implementa {@link RepositoryFeedbackPort}
 * delegando ao repositório JPA e traduzindo entre domínio e entidade.
 *
 * @author luisbraserv
 */
@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryAdapter implements RepositoryFeedbackPort {

    private final FeedbackJpaRepository feedbackJpaRepository;
    private final FeedbackPersistenceMapper feedbackPersistenceMapper;

    /**
     * Persiste o feedback e devolve a versão de domínio já com os dados gerados.
     *
     * @param  feedback  feedback de domínio a persistir
     * @return feedback de domínio salvo
     *
     * @author luisbraserv
     */
    @Override
    public Feedback save(Feedback feedback) {
        FeedbackJpaEntity entity = feedbackPersistenceMapper.toJpa(feedback);
        FeedbackJpaEntity saved = feedbackJpaRepository.save(entity);
        return feedbackPersistenceMapper.toDomain(saved);
    }

    /**
     * Recupera todos os feedbacks persistidos.
     *
     * @return lista de feedbacks de domínio (vazia se não houver registros)
     *
     * @author luisbraserv
     */
    @Override
    public List<Feedback> findAll() {
        return feedbackJpaRepository.findAll()
                .stream()
                .map(feedbackPersistenceMapper::toDomain)
                .toList();
    }
}
