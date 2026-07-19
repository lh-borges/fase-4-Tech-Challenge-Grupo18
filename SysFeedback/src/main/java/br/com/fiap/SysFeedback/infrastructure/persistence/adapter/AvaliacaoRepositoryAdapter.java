package br.com.fiap.SysFeedback.infrastructure.persistence.adapter;

import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.infrastructure.mapper.AvaliacaoPersistenceMapper;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.AvaliacaoJpaRepository;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.DisciplinaJpaRepository;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Adaptador de persistência que implementa {@link RepositoryAvaliacaoPort}
 * delegando ao repositório JPA e resolvendo as relações com disciplina e aluno.
 *
 * @author luisbraserv
 */
@Repository
@RequiredArgsConstructor
public class AvaliacaoRepositoryAdapter implements RepositoryAvaliacaoPort {

    private final AvaliacaoJpaRepository avaliacaoJpaRepository;
    private final DisciplinaJpaRepository disciplinaJpaRepository;
    private final UserJpaRepository userJpaRepository;

    /**
     * Persiste a avaliação, associando disciplina e aluno por referência, e devolve
     * a versão de domínio já com os dados gerados.
     *
     * @param  avaliacao  avaliação de domínio a persistir
     * @return avaliação de domínio salva
     *
     * @author Danilo Fernando
     */
    @Override
    public Avaliacao save(Avaliacao avaliacao) {
        AvaliacaoJpaEntity entity = new AvaliacaoJpaEntity();
        entity.setId(avaliacao.getId());
        entity.setDescricao(avaliacao.getDescricao());
        entity.setNota(avaliacao.getNota());
        entity.setUrgencia(avaliacao.getUrgencia());
        entity.setDataEnvio(avaliacao.getDataEnvio());
        entity.setDisciplina(disciplinaJpaRepository.getReferenceById(avaliacao.getDisciplinaId()));
        if (avaliacao.getAlunoId() != null) {
            entity.setAluno(userJpaRepository.getReferenceById(avaliacao.getAlunoId()));
        }

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
     * Recupera as avaliações pertencentes às disciplinas informadas.
     *
     * @param  disciplinaIds  identificadores das disciplinas
     * @return avaliações das disciplinas (vazia se a lista for vazia)
     *
     * @author Danilo Fernando
     */
    @Override
    public List<Avaliacao> findByDisciplinaIds(List<UUID> disciplinaIds) {
        if (disciplinaIds == null || disciplinaIds.isEmpty()) {
            return List.of();
        }
        return avaliacaoJpaRepository.findByDisciplina_IdIn(disciplinaIds)
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
