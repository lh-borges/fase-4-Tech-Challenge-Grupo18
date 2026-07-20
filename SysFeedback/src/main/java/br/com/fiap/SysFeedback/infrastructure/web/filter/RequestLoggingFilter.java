package br.com.fiap.SysFeedback.infrastructure.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de observabilidade que registra, para cada requisição HTTP, o método, a
 * rota, o status de resposta e a duração.
 *
 * <p>Roda como filtro mais externo (antes da segurança), de modo que até respostas
 * 401/403 são registradas. Permite acompanhar todo o tráfego nos logs (ex.:
 * {@code docker compose logs -f app}) e serve de rastro (trace) básico local.</p>
 *
 * @author Danilo Fernando
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("HTTP");

    /**
     * Registra a requisição após o processamento, com status e duração.
     *
     * @param  request  requisição HTTP recebida
     * @param  response  resposta HTTP produzida
     * @param  filterChain  cadeia de filtros a prosseguir
     *
     * @throws ServletException  em falha de processamento da cadeia
     * @throws IOException  em falha de E/S
     *
     * @author Danilo Fernando
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long inicio = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duracaoMs = System.currentTimeMillis() - inicio;
            log.info("{} {} -> {} ({} ms)",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), duracaoMs);
        }
    }
}
