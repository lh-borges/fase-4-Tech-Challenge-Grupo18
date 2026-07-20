package br.com.fiap.SysFeedback.infrastructure.web;

import br.com.fiap.SysFeedback.application.usecase.UserCreateUseCase;
import br.com.fiap.SysFeedback.application.usecase.UserDeleteUseCase;
import br.com.fiap.SysFeedback.application.usecase.UserFindAllUseCase;
import br.com.fiap.SysFeedback.application.usecase.UserFindByEmailUseCase;
import br.com.fiap.SysFeedback.application.usecase.UserUpdateUseCase;
import br.com.fiap.SysFeedback.domain.exception.EmailAlreadyExistsException;
import br.com.fiap.SysFeedback.domain.exception.UserNotFoundException;
import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.web.handler.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private final UserCreateUseCase userCreateUseCase = mock(UserCreateUseCase.class);
    private final UserFindByEmailUseCase userFindByEmailUseCase = mock(UserFindByEmailUseCase.class);
    private final UserFindAllUseCase userFindAllUseCase = mock(UserFindAllUseCase.class);
    private final UserUpdateUseCase userUpdateUseCase = mock(UserUpdateUseCase.class);
    private final UserDeleteUseCase userDeleteUseCase = mock(UserDeleteUseCase.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        UserController controller = new UserController(
                userCreateUseCase,
                userFindByEmailUseCase,
                userFindAllUseCase,
                userUpdateUseCase,
                userDeleteUseCase
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveCriarUsuario() throws Exception {
        when(userCreateUseCase.execute(any())).thenReturn(Fixture.userResponseDTO());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Fixture.userRequestDTOValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(Fixture.USER_ID.toString()))
                .andExpect(jsonPath("$.email").value(Fixture.USER_EMAIL))
                .andExpect(jsonPath("$.role").value(Fixture.USER_ROLE.name()));

        verify(userCreateUseCase).execute(any());
    }

    @Test
    void deveListarUsuarios() throws Exception {
        when(userFindAllUseCase.execute()).thenReturn(List.of(Fixture.userResponseDTO()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(Fixture.USER_ID.toString()))
                .andExpect(jsonPath("$[0].email").value(Fixture.USER_EMAIL));
    }

    @Test
    void deveBuscarUsuarioPorEmail() throws Exception {
        when(userFindByEmailUseCase.execute(Fixture.USER_EMAIL)).thenReturn(Fixture.userResponseDTO());

        mockMvc.perform(get("/users/email/{email}", Fixture.USER_EMAIL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Fixture.USER_ID.toString()))
                .andExpect(jsonPath("$.email").value(Fixture.USER_EMAIL));
    }

    @Test
    void deveAtualizarUsuario() throws Exception {
        when(userUpdateUseCase.execute(any(), any())).thenReturn(Fixture.userResponseDTO());

        mockMvc.perform(put("/users/{id}", Fixture.USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Fixture.userUpdateDTOValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Fixture.USER_ID.toString()));
    }

    @Test
    void deveExcluirUsuario() throws Exception {
        mockMvc.perform(delete("/users/{id}", Fixture.USER_ID))
                .andExpect(status().isNoContent());

        verify(userDeleteUseCase).execute(Fixture.USER_ID);
    }

    @Test
    void deveRetornarNotFoundQuandoUsuarioNaoExistir() throws Exception {
        when(userFindByEmailUseCase.execute(Fixture.USER_EMAIL))
                .thenThrow(new UserNotFoundException(Fixture.USER_EMAIL));

        mockMvc.perform(get("/users/email/{email}", Fixture.USER_EMAIL))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with email: " + Fixture.USER_EMAIL));
    }

    @Test
    void deveRetornarConflictQuandoEmailJaExistir() throws Exception {
        when(userCreateUseCase.execute(any())).thenThrow(new EmailAlreadyExistsException(Fixture.USER_EMAIL));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Fixture.userRequestDTOValido())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists: " + Fixture.USER_EMAIL));
    }

    @Test
    void deveRetornarBadRequestQuandoIdNaoForUuid() throws Exception {
        mockMvc.perform(delete("/users/{id}", "id-invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parâmetro 'id' com valor inválido"));
    }

    @Test
    void deveRetornarBadRequestQuandoCriacaoTiverJsonMalformado() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Aluno\","))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Corpo da requisição ausente ou mal formatado"))
                .andExpect(jsonPath("$.path").value("/users"));
    }

    @Test
    void deveRetornarBadRequestQuandoAtualizacaoTiverBodyAusente() throws Exception {
        mockMvc.perform(put("/users/{id}", Fixture.USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Corpo da requisição ausente ou mal formatado"));
    }

    @Test
    void deveRetornarInternalServerErrorQuandoListagemFalhar() throws Exception {
        when(userFindAllUseCase.execute()).thenThrow(new RuntimeException("Falha ao listar usuarios"));

        mockMvc.perform(get("/users"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno inesperado"))
                .andExpect(jsonPath("$.path").value("/users"));
    }

    @Test
    void deveRetornarInternalServerErrorQuandoExclusaoFalhar() throws Exception {
        org.mockito.Mockito.doThrow(new RuntimeException("Falha ao excluir usuario"))
                .when(userDeleteUseCase).execute(Fixture.USER_ID);

        mockMvc.perform(delete("/users/{id}", Fixture.USER_ID))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno inesperado"));
    }
}
