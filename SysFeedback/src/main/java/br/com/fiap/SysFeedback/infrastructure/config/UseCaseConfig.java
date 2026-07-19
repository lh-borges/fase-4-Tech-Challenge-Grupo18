package br.com.fiap.SysFeedback.infrastructure.config;

import br.com.fiap.SysFeedback.application.messaging.NotificadorUrgentePort;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryFeedbackPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.application.security.PasswordEncoderPort;
import br.com.fiap.SysFeedback.application.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public UserCreateUseCase userCreateUseCase(
            RepositoryUserPort repository,
            PasswordEncoderPort passwordEncoderPort) {

        return new UserCreateUseCase(repository, passwordEncoderPort);
    }

    @Bean
    public UserFindByEmailUseCase userFindByEmailUseCase(RepositoryUserPort repository) {
        return new UserFindByEmailUseCase(repository);
    }

    @Bean
    public UserFindAllUseCase userFindAllUseCase(RepositoryUserPort repository) {
        return new UserFindAllUseCase(repository);
    }

    @Bean
    public UserUpdateUseCase userUpdateUseCase(
            RepositoryUserPort repository,
            PasswordEncoderPort passwordEncoderPort) {

        return new UserUpdateUseCase(repository, passwordEncoderPort);
    }

    @Bean
    public UserDeleteUseCase userDeleteUseCase(RepositoryUserPort repository) {
        return new UserDeleteUseCase(repository);
    }

    // ----- Avaliação -----

    @Bean
    public AvaliacaoCreateUseCase avaliacaoCreateUseCase(
            RepositoryAvaliacaoPort repository,
            NotificadorUrgentePort notificadorUrgentePort) {

        return new AvaliacaoCreateUseCase(repository, notificadorUrgentePort);
    }

    @Bean
    public AvaliacaoFindAllUseCase avaliacaoFindAllUseCase(RepositoryAvaliacaoPort repository) {
        return new AvaliacaoFindAllUseCase(repository);
    }

    // ----- Feedback -----

    @Bean
    public FeedbackGenerateUseCase feedbackGenerateUseCase(
            RepositoryAvaliacaoPort avaliacaoRepository,
            RepositoryFeedbackPort feedbackRepository) {

        return new FeedbackGenerateUseCase(avaliacaoRepository, feedbackRepository);
    }

    @Bean
    public FeedbackFindAllUseCase feedbackFindAllUseCase(RepositoryFeedbackPort repository) {
        return new FeedbackFindAllUseCase(repository);
    }

    @Bean
    public RelatorioSemanalGenerateUseCase relatorioSemanalGenerateUseCase(
            RepositoryAvaliacaoPort avaliacaoRepository,
            RepositoryFeedbackPort feedbackRepository) {

        return new RelatorioSemanalGenerateUseCase(avaliacaoRepository, feedbackRepository);
    }
}