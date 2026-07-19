package br.com.fiap.SysFeedback.application.repository;

import br.com.fiap.SysFeedback.domain.entity.Feedback;

import java.util.List;

/**
 * Porta de persistência de feedbacks.
 *
 * @author luisbraserv
 */
public interface RepositoryFeedbackPort {

    /**
     * Persiste um feedback.
     *
     * @param  feedback  feedback a persistir
     * @return feedback persistido (com id atribuído)
     *
     * @author luisbraserv
     */
    Feedback save(Feedback feedback);

    /**
     * Retorna todos os feedbacks registrados.
     *
     * @return lista de feedbacks
     *
     * @author luisbraserv
     */
    List<Feedback> findAll();
}
