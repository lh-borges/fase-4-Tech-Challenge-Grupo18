package br.com.fiap.SysFeedback.application.repository;

import br.com.fiap.SysFeedback.domain.entity.Avaliacao;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Porta de persistência de avaliações.
 *
 * @author luisbraserv
 */
public interface RepositoryAvaliacaoPort {

    /**
     * Persiste uma avaliação.
     *
     * @param  avaliacao  avaliação a persistir
     * @return avaliação persistida (com id atribuído)
     *
     * @author luisbraserv
     */
    Avaliacao save(Avaliacao avaliacao);

    /**
     * Retorna todas as avaliações registradas.
     *
     * @return lista de avaliações
     *
     * @author luisbraserv
     */
    List<Avaliacao> findAll();

    /**
     * Retorna as avaliações com data de envio dentro do período [inicio, fim] (inclusivo).
     *
     * @param  inicio  início do período (inclusivo)
     * @param  fim  fim do período (inclusivo)
     * @return avaliações compreendidas no período
     *
     * @author luisbraserv
     */
    List<Avaliacao> findByPeriodo(LocalDateTime inicio, LocalDateTime fim);
}
