package br.com.fiap.SysFeedback.infrastructure.security.service;

import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementação de {@link UserDetailsService} que carrega os dados de autenticação
 * do usuário a partir da porta de repositório do domínio.
 *
 * @author Thiago de Jesus
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private final RepositoryUserPort repositoryUserPort;

    /**
     * Cria o serviço injetando a porta de acesso aos dados de usuário.
     *
     * @param  repositoryUserPort  porta de acesso aos dados de usuário
     *
     * @author Thiago de Jesus
     */
    public UserDetailsServiceImpl(RepositoryUserPort repositoryUserPort) {
        this.repositoryUserPort = repositoryUserPort;
    }

    /**
     * Carrega o usuário pelo e-mail e o converte para {@link UserDetails} do Spring Security.
     *
     * @param  email  e-mail utilizado como nome de usuário na autenticação
     * @return detalhes do usuário para o Spring Security
     *
     * @throws UsernameNotFoundException  quando nenhum usuário é encontrado com o e-mail informado
     *
     * @author Thiago de Jesus
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repositoryUserPort.findByEmail(email)
                .map(user -> User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Email não encontrado: " + email));
    }
}