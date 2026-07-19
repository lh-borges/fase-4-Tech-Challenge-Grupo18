package br.com.fiap.SysFeedback.infrastructure.mapper;

import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;

/**
 * Conversor entre a entidade de domínio {@link Avaliacao} e a entidade de
 * persistência {@link AvaliacaoJpaEntity}.
 *
 * @author luisbraserv
 */
public class AvaliacaoPersistenceMapper {

    /**
     * Converte uma avaliação de domínio na entidade JPA correspondente.
     *
     * @param  avaliacao  avaliação de domínio a converter
     * @return entidade JPA equivalente, ou {@code null} se a entrada for nula
     *
     * @author luisbraserv
     */
    public static AvaliacaoJpaEntity toJpa(Avaliacao avaliacao) {
        if (avaliacao == null) {
            return null;
        }
        return new AvaliacaoJpaEntity(
                avaliacao.getId(),
                avaliacao.getDescricao(),
                avaliacao.getNota(),
                avaliacao.getUrgencia(),
                avaliacao.getDataEnvio()
        );
    }

    /**
     * Converte uma entidade JPA na avaliação de domínio correspondente.
     *
     * @param  entity  entidade JPA a converter
     * @return avaliação de domínio equivalente, ou {@code null} se a entrada for nula
     *
     * @author luisbraserv
     */
    public static Avaliacao toDomain(AvaliacaoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Avaliacao(
                entity.getId(),
                entity.getDescricao(),
                entity.getNota(),
                entity.getUrgencia(),
                entity.getDataEnvio()
        );
    }
}
