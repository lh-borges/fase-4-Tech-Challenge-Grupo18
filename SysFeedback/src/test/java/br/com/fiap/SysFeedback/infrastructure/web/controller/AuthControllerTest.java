package br.com.fiap.SysFeedback.infrastructure.web.controller;

import br.com.fiap.SysFeedback.application.usecase.UserCreateUseCase;
import br.com.fiap.SysFeedback.domain.exception.EmailAlreadyExistsException;
import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.security.service.JwtService;
import br.com.fiap.SysFeedback.infrastructure.web.handler.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private final UserCreateUseCase userCreateUseCase = mock(UserCreateUseCase.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthController controller =
                new AuthController(userCreateUseCase, authenticationManager, jwtService, passwordEncoder);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveRegistrarUsuario() throws Exception {
        when(userCreateUseCase.execute(any())).thenReturn(Fixture.userResponseDTO());

        mockMvc.perform(post("/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Fixture.userRequestDTOValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(Fixture.USER_ID.toString()))
                .andExpect(jsonPath("$.email").value(Fixture.USER_EMAIL));

        verify(userCreateUseCase).execute(any());
    }

    @Test
    void deveFazerLoginERetornarToken() throws Exception {
        var authentication = new UsernamePasswordAuthenticationToken(
                Fixture.USER_EMAIL,
                Fixture.USER_PASSWORD,
                List.of(new SimpleGrantedAuthority("ROLE_ALUNO"))
        );
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(Fixture.USER_EMAIL, "ALUNO")).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", Fixture.USER_EMAIL,
                                "password", Fixture.USER_PASSWORD
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    void deveRetornarUnauthorizedQuandoCredenciaisForemInvalidas() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", Fixture.USER_EMAIL,
                                "password", "senha-errada"
                        ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornarConflictQuandoRegistrarEmailDuplicado() throws Exception {
        when(userCreateUseCase.execute(any())).thenThrow(new EmailAlreadyExistsException(Fixture.USER_EMAIL));

        mockMvc.perform(post("/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Fixture.userRequestDTOValido())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists: " + Fixture.USER_EMAIL))
                .andExpect(jsonPath("$.path").value("/auth/registrar"));
    }

    @Test
    void deveRetornarBadRequestQuandoLoginTiverJsonMalformado() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"aluno@fiap.com\","))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Corpo da requisição ausente ou mal formatado"));
    }

    @Test
    void deveRetornarInternalServerErrorQuandoJwtFalhar() throws Exception {
        var authentication = new UsernamePasswordAuthenticationToken(
                Fixture.USER_EMAIL,
                Fixture.USER_PASSWORD,
                List.of(new SimpleGrantedAuthority("ROLE_ALUNO"))
        );
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(Fixture.USER_EMAIL, "ALUNO"))
                .thenThrow(new IllegalStateException("Falha ao gerar token"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", Fixture.USER_EMAIL,
                                "password", Fixture.USER_PASSWORD
                        ))))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno inesperado"));
    }
}
