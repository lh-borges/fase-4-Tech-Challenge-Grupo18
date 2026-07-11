package br.com.fiap.SysFeedback.application.repository;

import br.com.fiap.SysFeedback.domain.entity.Feedback;

import java.util.List;

public interface RepositoryFeedbackPort {

    Feedback save(Feedback feedback);

    List<Feedback> findAll();
}
