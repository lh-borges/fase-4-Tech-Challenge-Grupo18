package br.com.fiap.SysFeedback.infrastructure.config;

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
}