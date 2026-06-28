package br.com.fiap.SysFeedback.infrastructure.security.config;

import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.infrastructure.security.filter.JwtAuthFilter;
import br.com.fiap.SysFeedback.infrastructure.security.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final RepositoryUserPort repositoryUserPort;  // ← ADICIONE

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, RepositoryUserPort repositoryUserPort) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.repositoryUserPort = repositoryUserPort;
    }

    @Bean
    public UserDetailsService userDetailsService() {  // ← ADICIONE ISSO
        return new UserDetailsServiceImpl(repositoryUserPort);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        // 1. Endpoints Públicos
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 2. Regras específicas do contexto /users (Centralizadas aqui!)
                        .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/email/*").hasAnyRole("ADMIN", "PROFESSOR")
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")

                        // O PUT aceita qualquer usuário autenticado na camada de filtro.
                        // (A validação se o ID pertence ao próprio usuário logado deve ser feita dentro do UseCase/Service)
                        .requestMatchers(HttpMethod.PUT, "/users/**").authenticated()

                        // 3. Outros contextos do sistema
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/feedback/**").hasAnyRole("PROFESSOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/feedback/**").hasAnyRole("PROFESSOR", "ADMIN")

                        // 4. Bloqueio residual global
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}