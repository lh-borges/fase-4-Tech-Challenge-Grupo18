package br.com.fiap.SysFeedback.infrastructure.config;

import br.com.fiap.SysFeedback.application.mapper.AvaliacaoMapper;
import br.com.fiap.SysFeedback.application.mapper.DisciplinaMapper;
import br.com.fiap.SysFeedback.application.mapper.FeedbackMapper;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.messaging.NotificadorUrgentePort;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryDisciplinaPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryFeedbackPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.application.security.PasswordEncoderPort;
import br.com.fiap.SysFeedback.application.usecase.AvaliacaoCreateUseCase;
import br.com.fiap.SysFeedback.application.usecase.AvaliacaoFindAllUseCase;
import br.com.fiap.SysFeedback.application.usecase.AvaliacaoFindUseCase;
import br.com.fiap.SysFeedback.application.usecase.DisciplinaFindUseCase;
import br.com.fiap.SysFeedback.application.usecase.FeedbackFindAllUseCase;
import br.com.fiap.SysFeedback.application.usecase.FeedbackGenerateUseCase;
import br.com.fiap.SysFeedback.application.usecase.RelatorioSemanalGenerateUseCase;
import br.com.fiap.SysFeedback.application.usecase.UserCreateUseCase;
import br.com.fiap.SysFeedback.application.usecase.UserDeleteUseCase;
import br.com.fiap.SysFeedback.application.usecase.UserFindAllUseCase;
import br.com.fiap.SysFeedback.application.usecase.UserFindByEmailUseCase;
import br.com.fiap.SysFeedback.application.usecase.UserUpdateUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wiring manual dos casos de uso da aplicação.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public UserCreateUseCase userCreateUseCase(RepositoryUserPort repository,
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
    public UserUpdateUseCase userUpdateUseCase(RepositoryUserPort repository,
                                               PasswordEncoderPort passwordEncoderPort,
                                               UserMapper userMapper) {
        return new UserUpdateUseCase(repository, passwordEncoderPort, userMapper);
    }

    @Bean
    public UserDeleteUseCase userDeleteUseCase(RepositoryUserPort repository) {
        return new UserDeleteUseCase(repository);
    }

    @Bean
    public AvaliacaoCreateUseCase avaliacaoCreateUseCase(RepositoryAvaliacaoPort repository,
                                                         RepositoryDisciplinaPort disciplinaRepository,
                                                         NotificadorUrgentePort notificadorUrgentePort) {
        return new AvaliacaoCreateUseCase(repository, disciplinaRepository, notificadorUrgentePort);
    }

    @Bean
    public AvaliacaoFindUseCase avaliacaoFindUseCase(RepositoryAvaliacaoPort repository,
                                                     RepositoryDisciplinaPort disciplinaRepository) {
        return new AvaliacaoFindUseCase(repository, disciplinaRepository);
    }

    @Bean
    public AvaliacaoFindAllUseCase avaliacaoFindAllUseCase(RepositoryAvaliacaoPort repository,
                                                           AvaliacaoMapper avaliacaoMapper) {
        return new AvaliacaoFindAllUseCase(repository, avaliacaoMapper);
    }

    @Bean
    public DisciplinaFindUseCase disciplinaFindUseCase(RepositoryDisciplinaPort disciplinaRepository,
                                                       DisciplinaMapper disciplinaMapper) {
        return new DisciplinaFindUseCase(disciplinaRepository, disciplinaMapper);
    }

    @Bean
    public FeedbackGenerateUseCase feedbackGenerateUseCase(RepositoryAvaliacaoPort avaliacaoRepository,
                                                           RepositoryFeedbackPort feedbackRepository,
                                                           FeedbackMapper feedbackMapper) {
        return new FeedbackGenerateUseCase(avaliacaoRepository, feedbackRepository, feedbackMapper);
    }

    @Bean
    public FeedbackFindAllUseCase feedbackFindAllUseCase(RepositoryFeedbackPort repository,
                                                         FeedbackMapper feedbackMapper) {
        return new FeedbackFindAllUseCase(repository, feedbackMapper);
    }

    @Bean
    public RelatorioSemanalGenerateUseCase relatorioSemanalGenerateUseCase(
            RepositoryAvaliacaoPort avaliacaoRepository,
            RepositoryFeedbackPort feedbackRepository) {
        return new RelatorioSemanalGenerateUseCase(avaliacaoRepository, feedbackRepository);
    }
}
