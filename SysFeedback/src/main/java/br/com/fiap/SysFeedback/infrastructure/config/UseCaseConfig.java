package br.com.fiap.SysFeedback.infrastructure.config;

import br.com.fiap.SysFeedback.application.messaging.NotificadorUrgentePort;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryDisciplinaPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryFeedbackPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.application.security.PasswordEncoderPort;
import br.com.fiap.SysFeedback.application.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Fábrica (wiring manual) dos casos de uso da aplicação.
 *
 * <p>Os use cases são POJOs independentes de framework; esta configuração os
 * expõe como beans, injetando as portas (repositórios, segurança, mensageria)
 * resolvidas pelos adapters de infraestrutura. Mantém a camada de aplicação livre
 * de anotações do Spring.</p>
 *
 * @author Thiago de Jesus
 */
@Configuration
public class UseCaseConfig {

    /**
     * Cria o caso de uso de cadastro de usuário.
     *
     * @param  repository  porta de persistência de usuários
     * @param  passwordEncoderPort  porta de codificação de senha
     * @return o caso de uso de criação de usuário
     *
     * @author Thiago de Jesus
     */
    @Bean
    public UserCreateUseCase userCreateUseCase(
            RepositoryUserPort repository,
            PasswordEncoderPort passwordEncoderPort) {

        return new UserCreateUseCase(repository, passwordEncoderPort);
    }

    /**
     * Cria o caso de uso de busca de usuário por e-mail.
     *
     * @param  repository  porta de persistência de usuários
     * @return o caso de uso de busca por e-mail
     *
     * @author Thiago de Jesus
     */
    @Bean
    public UserFindByEmailUseCase userFindByEmailUseCase(RepositoryUserPort repository) {
        return new UserFindByEmailUseCase(repository);
    }

    /**
     * Cria o caso de uso de listagem de todos os usuários.
     *
     * @param  repository  porta de persistência de usuários
     * @return o caso de uso de listagem de usuários
     *
     * @author Thiago de Jesus
     */
    @Bean
    public UserFindAllUseCase userFindAllUseCase(RepositoryUserPort repository) {
        return new UserFindAllUseCase(repository);
    }

    /**
     * Cria o caso de uso de atualização de usuário.
     *
     * @param  repository  porta de persistência de usuários
     * @param  passwordEncoderPort  porta de codificação de senha
     * @return o caso de uso de atualização de usuário
     *
     * @author Thiago de Jesus
     */
    @Bean
    public UserUpdateUseCase userUpdateUseCase(
            RepositoryUserPort repository,
            PasswordEncoderPort passwordEncoderPort) {

        return new UserUpdateUseCase(repository, passwordEncoderPort);
    }

    /**
     * Cria o caso de uso de remoção de usuário.
     *
     * @param  repository  porta de persistência de usuários
     * @return o caso de uso de remoção de usuário
     *
     * @author Thiago de Jesus
     */
    @Bean
    public UserDeleteUseCase userDeleteUseCase(RepositoryUserPort repository) {
        return new UserDeleteUseCase(repository);
    }

    // ----- Avaliação -----

    /**
     * Cria o caso de uso de registro de avaliação, com notificação de urgência.
     *
     * @param  repository  porta de persistência de avaliações
     * @param  notificadorUrgentePort  porta de notificação de avaliações críticas
     * @return o caso de uso de criação de avaliação
     *
     * @author Thiago de Jesus
     */
    @Bean
    public AvaliacaoCreateUseCase avaliacaoCreateUseCase(
            RepositoryAvaliacaoPort repository,
            RepositoryDisciplinaPort disciplinaRepository,
            NotificadorUrgentePort notificadorUrgentePort) {

        return new AvaliacaoCreateUseCase(repository, disciplinaRepository, notificadorUrgentePort);
    }

    /**
     * Cria o caso de uso de listagem de avaliações ciente do perfil (professor vê
     * apenas as disciplinas que leciona; admin vê todas).
     *
     * @param  repository  porta de persistência de avaliações
     * @param  disciplinaRepository  porta de consulta de disciplinas
     * @return o caso de uso de listagem de avaliações
     *
     * @author Danilo Fernando
     */
    @Bean
    public AvaliacaoFindUseCase avaliacaoFindUseCase(RepositoryAvaliacaoPort repository,
                                                     RepositoryDisciplinaPort disciplinaRepository) {
        return new AvaliacaoFindUseCase(repository, disciplinaRepository);
    }

    /**
     * Cria o caso de uso de listagem de disciplinas escopado pelo perfil.
     *
     * @param  disciplinaRepository  porta de consulta de disciplinas
     * @return o caso de uso de listagem de disciplinas
     *
     * @author Danilo Fernando
     */
    @Bean
    public DisciplinaFindUseCase disciplinaFindUseCase(RepositoryDisciplinaPort disciplinaRepository) {
        return new DisciplinaFindUseCase(disciplinaRepository);
    }

    // ----- Feedback -----

    /**
     * Cria o caso de uso de geração de feedback consolidado por período.
     *
     * @param  avaliacaoRepository  porta de persistência de avaliações
     * @param  feedbackRepository  porta de persistência de feedbacks
     * @return o caso de uso de geração de feedback
     *
     * @author Thiago de Jesus
     */
    @Bean
    public FeedbackGenerateUseCase feedbackGenerateUseCase(
            RepositoryAvaliacaoPort avaliacaoRepository,
            RepositoryFeedbackPort feedbackRepository) {

        return new FeedbackGenerateUseCase(avaliacaoRepository, feedbackRepository);
    }

    /**
     * Cria o caso de uso de listagem de feedbacks gerados.
     *
     * @param  repository  porta de persistência de feedbacks
     * @return o caso de uso de listagem de feedbacks
     *
     * @author Thiago de Jesus
     */
    @Bean
    public FeedbackFindAllUseCase feedbackFindAllUseCase(RepositoryFeedbackPort repository) {
        return new FeedbackFindAllUseCase(repository);
    }

    /**
     * Cria o caso de uso de geração do relatório semanal (consumido pelo endpoint
     * interno e, por ele, pela Cloud Function {@code relatorio-semanal}).
     *
     * @param  avaliacaoRepository  porta de persistência de avaliações
     * @param  feedbackRepository  porta de persistência de feedbacks
     * @return o caso de uso de geração do relatório semanal
     *
     * @author Danilo Fernando
     */
    @Bean
    public RelatorioSemanalGenerateUseCase relatorioSemanalGenerateUseCase(
            RepositoryAvaliacaoPort avaliacaoRepository,
            RepositoryFeedbackPort feedbackRepository) {

        return new RelatorioSemanalGenerateUseCase(avaliacaoRepository, feedbackRepository);
    }
}
