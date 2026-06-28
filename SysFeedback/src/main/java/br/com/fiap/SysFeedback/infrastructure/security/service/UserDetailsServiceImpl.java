package br.com.fiap.SysFeedback.infrastructure.security.service;

import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

public class UserDetailsServiceImpl implements UserDetailsService {

    private final RepositoryUserPort repositoryUserPort;

    public UserDetailsServiceImpl(RepositoryUserPort repositoryUserPort) {
        this.repositoryUserPort = repositoryUserPort;
    }

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