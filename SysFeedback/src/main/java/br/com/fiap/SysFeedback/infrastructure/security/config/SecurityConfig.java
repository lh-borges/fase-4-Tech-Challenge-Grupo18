package br.com.fiap.SysFeedback.infrastructure.security.config;

import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.infrastructure.security.filter.InternalApiKeyFilter;
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

/**
 * Configuração central de segurança da aplicação: define a cadeia de filtros,
 * as regras de autorização por rota e os beans de autenticação.
 *
 * @author Thiago de Jesus
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final InternalApiKeyFilter internalApiKeyFilter;
    private final RepositoryUserPort repositoryUserPort;

    /**
     * Cria a configuração de segurança injetando os filtros e a porta de repositório.
     *
     * @param  jwtAuthFilter        filtro de autenticação via token JWT
     * @param  internalApiKeyFilter filtro de autenticação por API key interna
     * @param  repositoryUserPort   porta de acesso aos dados de usuário
     *
     * @author Thiago de Jesus
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          InternalApiKeyFilter internalApiKeyFilter,
                          RepositoryUserPort repositoryUserPort) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.internalApiKeyFilter = internalApiKeyFilter;
        this.repositoryUserPort = repositoryUserPort;
    }

    /**
     * Fornece o serviço de carregamento de usuários usado pela autenticação.
     *
     * @return implementação de {@link UserDetailsService} baseada no repositório de usuários
     *
     * @author Thiago de Jesus
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(repositoryUserPort);
    }

    /**
     * Fornece o codificador de senhas BCrypt utilizado pela aplicação.
     *
     * @return instância de {@link PasswordEncoder} baseada em BCrypt
     *
     * @author Thiago de Jesus
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura o provedor de autenticação DAO com o serviço de usuários e o codificador de senhas.
     *
     * @return provedor {@link DaoAuthenticationProvider} configurado
     *
     * @author Thiago de Jesus
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /**
     * Expõe o gerenciador de autenticação a partir da configuração do Spring Security.
     *
     * @param  config  configuração de autenticação do Spring Security
     * @return o {@link AuthenticationManager} configurado
     *
     * @throws Exception  quando não é possível obter o gerenciador de autenticação
     *
     * @author Thiago de Jesus
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    /**
     * Define a cadeia de filtros de segurança: desabilita CSRF, aplica política de
     * sessão stateless, registra as regras de autorização por rota (endpoints públicos,
     * rotas internas máquina-a-máquina e controle de acesso por perfil) e adiciona os
     * filtros de API key interna e de autenticação JWT antes do filtro padrão do Spring.
     *
     * @param  http  objeto de configuração de segurança HTTP
     * @return a {@link SecurityFilterChain} construída
     *
     * @throws Exception  quando ocorre falha ao montar a cadeia de filtros
     *
     * @author Thiago de Jesus
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        // 1. Endpoints Públicos
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Rotas internas (maquina-a-maquina): a cadeia libera aqui,
                        // mas o InternalApiKeyFilter exige a API key (X-Internal-Api-Key).
                        .requestMatchers("/internal/**").permitAll()

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

                        // Avaliações: usuário (ALUNO) envia, dono (PROFESSOR/ADMIN) lê
                        .requestMatchers(HttpMethod.POST, "/avaliacoes/**").hasRole("ALUNO")
                        .requestMatchers(HttpMethod.GET, "/avaliacoes/**").hasAnyRole("PROFESSOR", "ADMIN")

                        // Feedback consolidado: gerado e lido pelo dono (PROFESSOR/ADMIN)
                        .requestMatchers(HttpMethod.GET, "/feedback/**").hasAnyRole("PROFESSOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/feedback/**").hasAnyRole("PROFESSOR", "ADMIN")

                        // 4. Bloqueio residual global
                        .anyRequest().authenticated()
                )
                // Ambos os filtros custom rodam antes da autenticacao padrao.
                // A ancora precisa ser um filtro conhecido do Spring Security
                // (UsernamePasswordAuthenticationFilter), nao um filtro custom.
                .addFilterBefore(internalApiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}