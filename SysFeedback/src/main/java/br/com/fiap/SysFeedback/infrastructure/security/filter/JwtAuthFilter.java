package br.com.fiap.SysFeedback.infrastructure.security.filter;

import br.com.fiap.SysFeedback.infrastructure.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro executado uma vez por requisição que valida o token JWT do header
 * {@code Authorization} e popula o contexto de segurança do Spring quando válido.
 *
 * @author Thiago de Jesus
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /**
     * Extrai e valida o token JWT da requisição; em caso de token válido, autentica
     * o usuário no contexto de segurança e segue a cadeia de filtros. Requisições sem
     * token ou com token inválido seguem sem autenticação para o Spring tratar.
     *
     * @param  request      requisição HTTP recebida
     * @param  response     resposta HTTP associada
     * @param  filterChain  cadeia de filtros a ser prosseguida
     *
     * @throws ServletException  quando ocorre erro no processamento do servlet
     * @throws IOException       quando ocorre erro de entrada/saída
     *
     * @author Thiago de Jesus
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extrai o header Authorization
        String authHeader = request.getHeader("Authorization");

        // Se não tem Authorization, deixa passar (Spring vai validar depois)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Remove "Bearer " e pega o token
            String token = authHeader.substring(7);

            // 3. Valida token
            if (!jwtService.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 4. Extrai email e role do token
            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);


            // 5. Cria autenticação
            var authority = new SimpleGrantedAuthority("ROLE_" + role);
            var authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    Collections.singleton(authority)
            );

            // 6. Seta no contexto do Spring
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // Se der erro ao processar token, deixa o Spring rejeitar
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
