package br.com.fiap.SysFeedback.infrastructure.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço responsável por gerar, validar e extrair informações de tokens JWT.
 *
 * @author Thiago de Jesus
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpiration;

    /**
     * Deriva a chave de assinatura HMAC a partir do segredo configurado.
     *
     * @return chave secreta utilizada para assinar e verificar tokens
     *
     * @author Thiago de Jesus
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Gera um token JWT contendo o e-mail como subject e a role como claim.
     *
     * @param  email  e-mail do usuário (subject do token)
     * @param  role   perfil do usuário armazenado como claim
     * @return token JWT assinado e compactado
     *
     * @author Thiago de Jesus
     */
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, email);
    }

    /**
     * Monta e assina o token JWT com os claims, o subject, a data de emissão e a expiração.
     *
     * @param  claims   claims adicionais a serem incluídos no token
     * @param  subject  identificador do subject do token
     * @return token JWT assinado e compactado
     *
     * @author Thiago de Jesus
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrai o e-mail (subject) contido no token.
     *
     * @param  token  token JWT a ser analisado
     * @return e-mail armazenado no subject do token
     *
     * @author Thiago de Jesus
     */
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrai a role armazenada como claim no token.
     *
     * @param  token  token JWT a ser analisado
     * @return perfil (role) armazenado no token
     *
     * @author Thiago de Jesus
     */
    public String extractRole(String token) {
        return (String) getClaims(token).get("role");
    }

    /**
     * Verifica se o token já está expirado.
     *
     * @param  token  token JWT a ser verificado
     * @return {@code true} se o token estiver expirado, caso contrário {@code false}
     *
     * @author Thiago de Jesus
     */
    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    /**
     * Valida a assinatura e a expiração do token.
     *
     * @param  token  token JWT a ser validado
     * @return {@code true} se o token for válido e não expirado, caso contrário {@code false}
     *
     * @author Thiago de Jesus
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Analisa o token, verifica a assinatura e retorna os claims contidos no payload.
     *
     * @param  token  token JWT a ser analisado
     * @return claims extraídos do payload do token
     *
     * @author Thiago de Jesus
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}