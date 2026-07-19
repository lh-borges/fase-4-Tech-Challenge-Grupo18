package br.com.fiap.SysFeedback.infrastructure.persistence.repository;

import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositório Spring Data JPA para {@link AvaliacaoJpaEntity}.
 *
 * @author luisbraserv
 */
@Repository
public interface AvaliacaoJpaRepository extends JpaRepository<AvaliacaoJpaEntity, UUID> {

    /**
     * Busca as avaliações cuja data de envio está dentro do intervalo informado.
     *
     * @param  inicio  início do intervalo (inclusivo)
     * @param  fim  fim do intervalo (inclusivo)
     * @return lista de avaliações no período
     *
     * @author luisbraserv
     */
    List<AvaliacaoJpaEntity> findByDataEnvioBetween(LocalDateTime inicio, LocalDateTime fim);

    /**
     * Busca as avaliações pertencentes às disciplinas informadas.
     *
     * @param  disciplinaIds  identificadores das disciplinas
     * @return avaliações das disciplinas informadas
     *
     * @author Danilo Fernando
     */
    List<AvaliacaoJpaEntity> findByDisciplina_IdIn(List<UUID> disciplinaIds);
}
