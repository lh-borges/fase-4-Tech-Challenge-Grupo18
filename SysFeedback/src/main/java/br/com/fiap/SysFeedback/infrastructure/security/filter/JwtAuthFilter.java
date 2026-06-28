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

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

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
