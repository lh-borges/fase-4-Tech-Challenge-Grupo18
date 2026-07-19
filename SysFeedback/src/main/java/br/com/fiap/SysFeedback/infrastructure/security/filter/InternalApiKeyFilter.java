package br.com.fiap.SysFeedback.infrastructure.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Protege as rotas {@code /internal/**} com uma API key estática
 * (header {@code X-Internal-Api-Key}), para acesso máquina-a-máquina das Cloud
 * Functions — sem JWT de usuário.
 *
 * <p><strong>Fail-closed:</strong> se a chave não estiver configurada
 * ({@code internal.api-key} vazio) ou não bater, responde 401. A comparação é em
 * tempo constante para evitar ataques de timing.</p>
 */
@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Internal-Api-Key";
    private static final String INTERNAL_PREFIX = "/internal/";

    private final String apiKey;

    public InternalApiKeyFilter(@Value("${internal.api-key:}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().startsWith(INTERNAL_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String fornecida = request.getHeader(HEADER);
        if (!chaveValida(fornecida)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("API key interna ausente ou invalida.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean chaveValida(String fornecida) {
        if (apiKey == null || apiKey.isBlank() || fornecida == null) {
            return false;
        }
        byte[] esperada = apiKey.getBytes(StandardCharsets.UTF_8);
        byte[] recebida = fornecida.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(esperada, recebida);
    }
}
