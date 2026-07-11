package br.com.fiap.SysFeedback.infrastructure.persistence.adapter;

import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.infrastructure.mapper.AvaliacaoPersistenceMapper;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.AvaliacaoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AvaliacaoRepositoryAdapter implements RepositoryAvaliacaoPort {

    private final AvaliacaoJpaRepository avaliacaoJpaRepository;

    @Override
    public Avaliacao save(Avaliacao avaliacao) {
        AvaliacaoJpaEntity entity = AvaliacaoPersistenceMapper.toJpa(avaliacao);
        AvaliacaoJpaEntity saved = avaliacaoJpaRepository.save(entity);
        return AvaliacaoPersistenceMapper.toDomain(saved);
    }

    @Override
    public List<Avaliacao> findAll() {
        return avaliacaoJpaRepository.findAll()
                .stream()
                .map(AvaliacaoPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Avaliacao> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return avaliacaoJpaRepository.findByDataEnvioBetween(inicio, fim)
                .stream()
                .map(AvaliacaoPersistenceMapper::toDomain)
                .toList();
    }
}
