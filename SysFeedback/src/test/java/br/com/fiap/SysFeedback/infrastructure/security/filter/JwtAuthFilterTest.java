package br.com.fiap.SysFeedback.infrastructure.security.filter;

import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.security.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JwtAuthFilterTest {

    private final JwtService jwtService = mock(JwtService.class);

    private JwtAuthFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthFilter(jwtService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveSeguirFluxoSemAutenticarQuandoHeaderAuthorizationNaoExistir() throws Exception {
        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(request, filterChain.getRequest());
        assertEquals(response, filterChain.getResponse());
        verifyNoInteractions(jwtService);
    }

    @Test
    void deveSeguirFluxoSemAutenticarQuandoHeaderNaoForBearer() throws Exception {
        request.addHeader("Authorization", "Basic token");

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(request, filterChain.getRequest());
        verifyNoInteractions(jwtService);
    }

    @Test
    void deveSeguirFluxoSemAutenticarQuandoTokenForInvalido() throws Exception {
        request.addHeader("Authorization", "Bearer token-invalido");
        when(jwtService.validateToken("token-invalido")).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(request, filterChain.getRequest());
        verify(jwtService).validateToken("token-invalido");
    }

    @Test
    void deveAutenticarUsuarioQuandoTokenForValido() throws Exception {
        request.addHeader("Authorization", "Bearer token-valido");
        when(jwtService.validateToken("token-valido")).thenReturn(true);
        when(jwtService.extractEmail("token-valido")).thenReturn(Fixture.USER_EMAIL);
        when(jwtService.extractRole("token-valido")).thenReturn("ALUNO");

        filter.doFilter(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(Fixture.USER_EMAIL, authentication.getPrincipal());
        assertNull(authentication.getCredentials());
        assertTrue(authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> "ROLE_ALUNO".equals(authority.getAuthority())));
        assertEquals(request, filterChain.getRequest());
    }

    @Test
    void deveLimparContextoQuandoJwtServiceLancarExcecao() throws Exception {
        request.addHeader("Authorization", "Bearer token-com-erro");
        when(jwtService.validateToken("token-com-erro")).thenReturn(true);
        when(jwtService.extractEmail("token-com-erro"))
                .thenThrow(new RuntimeException("Falha ao extrair email"));

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(request, filterChain.getRequest());
    }
}
