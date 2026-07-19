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

/**
 * Adaptador de persistência que implementa {@link RepositoryAvaliacaoPort}
 * delegando ao repositório JPA e traduzindo entre domínio e entidade.
 *
 * @author luisbraserv
 */
@Repository
@RequiredArgsConstructor
public class AvaliacaoRepositoryAdapter implements RepositoryAvaliacaoPort {

    private final AvaliacaoJpaRepository avaliacaoJpaRepository;

    /**
     * Persiste a avaliação e devolve a versão de domínio já com os dados gerados.
     *
     * @param  avaliacao  avaliação de domínio a persistir
     * @return avaliação de domínio salva
     *
     * @author luisbraserv
     */
    @Override
    public Avaliacao save(Avaliacao avaliacao) {
        AvaliacaoJpaEntity entity = AvaliacaoPersistenceMapper.toJpa(avaliacao);
        AvaliacaoJpaEntity saved = avaliacaoJpaRepository.save(entity);
        return AvaliacaoPersistenceMapper.toDomain(saved);
    }

    /**
     * Recupera todas as avaliações persistidas.
     *
     * @return lista de avaliações de domínio (vazia se não houver registros)
     *
     * @author luisbraserv
     */
    @Override
    public List<Avaliacao> findAll() {
        return avaliacaoJpaRepository.findAll()
                .stream()
                .map(AvaliacaoPersistenceMapper::toDomain)
                .toList();
    }

    /**
     * Recupera as avaliações enviadas dentro do período informado.
     *
     * @param  inicio  início do intervalo (inclusivo)
     * @param  fim  fim do intervalo (inclusivo)
     * @return lista de avaliações de domínio no período (vazia se não houver)
     *
     * @author luisbraserv
     */
    @Override
    public List<Avaliacao> findByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return avaliacaoJpaRepository.findByDataEnvioBetween(inicio, fim)
                .stream()
                .map(AvaliacaoPersistenceMapper::toDomain)
                .toList();
    }
}
