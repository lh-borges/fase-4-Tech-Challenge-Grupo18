package br.com.fiap.SysFeedback.infrastructure.config;

import br.com.fiap.SysFeedback.application.mapper.AvaliacaoMapper;
import br.com.fiap.SysFeedback.application.mapper.FeedbackMapper;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
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
            PasswordEncoderPort passwordEncoderPort,
            UserMapper userMapper) {

        return new UserCreateUseCase(repository, passwordEncoderPort, userMapper);
    }

    @Bean
    public UserFindByEmailUseCase userFindByEmailUseCase(RepositoryUserPort repository,
                                                         UserMapper userMapper) {
        return new UserFindByEmailUseCase(repository, userMapper);
    }

    @Bean
    public UserFindAllUseCase userFindAllUseCase(RepositoryUserPort repository,
                                                 UserMapper userMapper) {
        return new UserFindAllUseCase(repository, userMapper);
    }

    @Bean
    public UserUpdateUseCase userUpdateUseCase(
            RepositoryUserPort repository,
            PasswordEncoderPort passwordEncoderPort,
            UserMapper userMapper) {

        return new UserUpdateUseCase(repository, passwordEncoderPort, userMapper);
    }

    @Bean
    public UserDeleteUseCase userDeleteUseCase(RepositoryUserPort repository) {
        return new UserDeleteUseCase(repository);
    }

    // ----- Avaliação -----

    @Bean
    public AvaliacaoCreateUseCase avaliacaoCreateUseCase(RepositoryAvaliacaoPort repository,
                                                         AvaliacaoMapper avaliacaoMapper) {
        return new AvaliacaoCreateUseCase(repository, avaliacaoMapper);
    }

    @Bean
    public AvaliacaoFindAllUseCase avaliacaoFindAllUseCase(RepositoryAvaliacaoPort repository,
                                                           AvaliacaoMapper avaliacaoMapper) {
        return new AvaliacaoFindAllUseCase(repository, avaliacaoMapper);
    }

    // ----- Feedback -----

    @Bean
    public FeedbackGenerateUseCase feedbackGenerateUseCase(
            RepositoryAvaliacaoPort avaliacaoRepository,
            RepositoryFeedbackPort feedbackRepository,
            FeedbackMapper feedbackMapper) {

        return new FeedbackGenerateUseCase(avaliacaoRepository, feedbackRepository, feedbackMapper);
    }

    @Bean
    public FeedbackFindAllUseCase feedbackFindAllUseCase(RepositoryFeedbackPort repository,
                                                         FeedbackMapper feedbackMapper) {
        return new FeedbackFindAllUseCase(repository, feedbackMapper);
    }
}
