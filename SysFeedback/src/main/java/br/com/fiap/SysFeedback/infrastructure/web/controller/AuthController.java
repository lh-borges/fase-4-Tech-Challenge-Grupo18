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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserCreateUseCase userCreateUseCase;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // Registro
    @PostMapping("/registrar")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO user = userCreateUseCase.execute(request);
        return ResponseEntity.status(201).body(user);
    }

    // Login
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

    // DTOs
    public record LoginRequest(String email, String password) {}
    public record TokenResponse(String token, String type) {}
}
