package br.com.fiap.SysFeedback.infrastructure.persistence.repository;

import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AvaliacaoJpaRepository extends JpaRepository<AvaliacaoJpaEntity, UUID> {

    List<AvaliacaoJpaEntity> findByDataEnvioBetween(LocalDateTime inicio, LocalDateTime fim);
}
