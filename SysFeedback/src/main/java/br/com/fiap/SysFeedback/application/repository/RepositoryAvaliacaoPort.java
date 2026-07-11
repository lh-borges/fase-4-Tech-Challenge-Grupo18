package br.com.fiap.SysFeedback.application.repository;

import br.com.fiap.SysFeedback.domain.entity.Avaliacao;

import java.time.LocalDateTime;
import java.util.List;

public interface RepositoryAvaliacaoPort {

    Avaliacao save(Avaliacao avaliacao);

    List<Avaliacao> findAll();

    /** Avaliações com data de envio dentro do período [inicio, fim] (inclusivo). */
    List<Avaliacao> findByPeriodo(LocalDateTime inicio, LocalDateTime fim);
}
