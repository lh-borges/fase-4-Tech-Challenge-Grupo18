package br.com.fiap.SysFeedback.infrastructure.persistence.repository;

import br.com.fiap.SysFeedback.infrastructure.persistence.entity.FeedbackJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório Spring Data JPA para {@link FeedbackJpaEntity}.
 *
 * @author luisbraserv
 */
@Repository
public interface FeedbackJpaRepository extends JpaRepository<FeedbackJpaEntity, UUID> {
}
