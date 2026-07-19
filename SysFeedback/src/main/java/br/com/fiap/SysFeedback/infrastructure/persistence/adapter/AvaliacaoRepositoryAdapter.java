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
    private final AvaliacaoPersistenceMapper avaliacaoPersistenceMapper;

    @Override
    public Avaliacao save(Avaliacao avaliacao) {
        AvaliacaoJpaEntity entity = avaliacaoPersistenceMapper.toJpa(avaliacao);
        AvaliacaoJpaEntity saved = avaliacaoJpaRepository.save(entity);
        return avaliacaoPersistenceMapper.toDomain(saved);
    }

    @Override
    public List<Avaliacao> findAll() {
        return avaliacaoJpaRepository.findAll()
                .stream()
                .map(avaliacaoPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Avaliacao> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return avaliacaoJpaRepository.findByDataEnvioBetween(inicio, fim)
                .stream()
                .map(avaliacaoPersistenceMapper::toDomain)
                .toList();
    }
}
