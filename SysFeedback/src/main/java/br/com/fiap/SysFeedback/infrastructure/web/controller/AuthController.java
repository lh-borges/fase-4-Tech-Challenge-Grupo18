package br.com.fiap.SysFeedback.infrastructure.web.controller;

import br.com.fiap.SysFeedback.application.dto.UserRequestDTO;
import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.usecase.UserCreateUseCase;
import br.com.fiap.SysFeedback.infrastructure.security.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST de autenticação responsável pelo registro de novos usuários
 * e pela emissão de tokens JWT no login.
 *
 * @author Thiago de Jesus
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserCreateUseCase userCreateUseCase;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra um novo usuário a partir dos dados informados.
     *
     * @param  request  dados de criação do usuário
     * @return resposta HTTP 201 com os dados do usuário criado
     *
     * @author Thiago de Jesus
     */
    @PostMapping("/registrar")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO user = userCreateUseCase.execute(request);
        return ResponseEntity.status(201).body(user);
    }

    /**
     * Autentica o usuário e retorna um token JWT em caso de sucesso.
     *
     * @param  request  credenciais de login (e-mail e senha)
     * @return resposta HTTP 200 com o token gerado, ou HTTP 401 em caso de credenciais inválidas
     *
     * @author Thiago de Jesus
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        try {

            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );


            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .orElse("ALUNO"); // Fallback caso venha sem role


            if (role.startsWith("ROLE_")) {
                role = role.substring(5);
            }


            String token = jwtService.generateToken(request.email(), role);
            return ResponseEntity.ok(new TokenResponse(token, "Bearer"));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * DTO de entrada com as credenciais de login.
     *
     * @author Thiago de Jesus
     */
    public record LoginRequest(String email, String password) {}

    /**
     * DTO de saída com o token JWT emitido e o seu tipo.
     *
     * @author Thiago de Jesus
     */
    public record TokenResponse(String token, String type) {}
}
