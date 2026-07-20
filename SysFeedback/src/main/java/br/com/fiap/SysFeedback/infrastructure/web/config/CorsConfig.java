package br.com.fiap.SysFeedback.infrastructure.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuração de CORS para permitir que o frontend (serviço web separado) consuma
 * a API a partir de outra origem/porta.
 *
 * <p>Como a autenticação é via token JWT no header {@code Authorization} (e não por
 * cookies), habilitamos as origens de forma permissiva por padrão; em produção o
 * ideal é restringir {@code allowedOriginPatterns} ao domínio do frontend.</p>
 *
 * @author Danilo Fernando
 */
@Configuration
public class CorsConfig {

    /**
     * Define a política de CORS aplicada a todas as rotas da API.
     *
     * @return fonte de configuração de CORS usada pela cadeia de segurança
     *
     * @author Danilo Fernando
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
