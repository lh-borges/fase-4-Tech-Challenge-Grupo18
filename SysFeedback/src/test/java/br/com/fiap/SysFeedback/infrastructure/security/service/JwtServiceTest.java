package br.com.fiap.SysFeedback.infrastructure.security.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET =
            "1234567890123456789012345678901234567890123456789012345678901234";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 60_000L);
    }

    @Test
    void deveGerarTokenEExtrairEmailERole() {
        String token = jwtService.generateToken("aluno@fiap.com", "ALUNO");

        assertEquals("aluno@fiap.com", jwtService.extractEmail(token));
        assertEquals("ALUNO", jwtService.extractRole(token));
        assertFalse(jwtService.isTokenExpired(token));
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void deveRetornarFalseQuandoTokenForInvalido() {
        assertFalse(jwtService.validateToken("token-invalido"));
    }

    @Test
    void deveRetornarFalseQuandoTokenEstiverExpirado() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1_000L);

        String token = jwtService.generateToken("aluno@fiap.com", "ALUNO");

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenExpired(token));
        assertFalse(jwtService.validateToken(token));
    }

    @Test
    void deveRetornarFalseQuandoTokenForAssinadoComOutroSegredo() {
        String token = jwtService.generateToken("aluno@fiap.com", "ALUNO");

        JwtService outroJwtService = new JwtService();
        ReflectionTestUtils.setField(
                outroJwtService,
                "jwtSecret",
                "abcdefghijklmnopqrstuvwxyz123456abcdefghijklmnopqrstuvwxyz123456"
        );
        ReflectionTestUtils.setField(outroJwtService, "jwtExpiration", 60_000L);

        assertFalse(outroJwtService.validateToken(token));
    }

    @Test
    void deveLancarExcecaoAoExtrairEmailDeTokenMalformado() {
        assertThrows(MalformedJwtException.class, () -> jwtService.extractEmail("token-invalido"));
    }

    @Test
    void deveLancarExcecaoAoExtrairRoleDeTokenMalformado() {
        assertThrows(MalformedJwtException.class, () -> jwtService.extractRole("token-invalido"));
    }

    @Test
    void deveFalharAoGerarTokenComSegredoFraco() {
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "segredo-curto");

        assertThrows(WeakKeyException.class, () -> jwtService.generateToken("aluno@fiap.com", "ALUNO"));
    }

    @Test
    void deveLancarExcecaoAoValidarExpiracaoDeTokenMalformado() {
        assertThrows(MalformedJwtException.class, () -> jwtService.isTokenExpired("token-invalido"));
    }
}
