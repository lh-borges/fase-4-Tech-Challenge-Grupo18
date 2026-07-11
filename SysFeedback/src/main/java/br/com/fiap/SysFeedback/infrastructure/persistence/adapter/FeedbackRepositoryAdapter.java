package br.com.fiap.SysFeedback.infrastructure.persistence.adapter;

import br.com.fiap.SysFeedback.application.repository.RepositoryFeedbackPort;
import br.com.fiap.SysFeedback.domain.entity.Feedback;
import br.com.fiap.SysFeedback.infrastructure.mapper.FeedbackPersistenceMapper;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.FeedbackJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.FeedbackJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryAdapter implements RepositoryFeedbackPort {

    private final FeedbackJpaRepository feedbackJpaRepository;

    @Override
    public Feedback save(Feedback feedback) {
        FeedbackJpaEntity entity = FeedbackPersistenceMapper.toJpa(feedback);
        FeedbackJpaEntity saved = feedbackJpaRepository.save(entity);
        return FeedbackPersistenceMapper.toDomain(saved);
    }

    @Override
    public List<Feedback> findAll() {
        return feedbackJpaRepository.findAll()
                .stream()
                .map(FeedbackPersistenceMapper::toDomain)
                .toList();
    }
}
