package br.com.fiap.SysFeedback.infrastructure.web.controller;

import br.com.fiap.SysFeedback.application.dto.DisciplinaResponseDTO;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.application.usecase.DisciplinaFindUseCase;
import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.web.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DisciplinaControllerTest {

    private final DisciplinaFindUseCase disciplinaFindUseCase = mock(DisciplinaFindUseCase.class);
    private final RepositoryUserPort repositoryUserPort = mock(RepositoryUserPort.class);
    private final Authentication authentication = mock(Authentication.class);

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        DisciplinaController controller = new DisciplinaController(disciplinaFindUseCase, repositoryUserPort);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        when(authentication.getName()).thenReturn(Fixture.USER_EMAIL);
    }

    @Test
    void deveListarDisciplinasDoUsuarioAutenticado() throws Exception {
        DisciplinaResponseDTO response =
                new DisciplinaResponseDTO(Fixture.DISCIPLINA_ID, Fixture.DISCIPLINA_NOME, "ARQ");
        when(repositoryUserPort.findByEmail(Fixture.USER_EMAIL)).thenReturn(Optional.of(Fixture.user()));
        when(disciplinaFindUseCase.execute(Fixture.USER_ROLE, Fixture.USER_ID)).thenReturn(List.of(response));

        mockMvc.perform(get("/disciplinas").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(Fixture.DISCIPLINA_ID.toString()))
                .andExpect(jsonPath("$[0].nome").value(Fixture.DISCIPLINA_NOME))
                .andExpect(jsonPath("$[0].codigo").value("ARQ"));

        verify(repositoryUserPort).findByEmail(Fixture.USER_EMAIL);
        verify(disciplinaFindUseCase).execute(Fixture.USER_ROLE, Fixture.USER_ID);
    }

    @Test
    void deveRetornarForbiddenQuandoUsuarioAutenticadoNaoForEncontrado() throws Exception {
        when(repositoryUserPort.findByEmail(Fixture.USER_EMAIL)).thenReturn(Optional.empty());

        mockMvc.perform(get("/disciplinas").principal(authentication))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Usuário autenticado não encontrado"))
                .andExpect(jsonPath("$.path").value("/disciplinas"));
    }

    @Test
    void deveRetornarInternalServerErrorQuandoUseCaseFalhar() throws Exception {
        when(repositoryUserPort.findByEmail(Fixture.USER_EMAIL)).thenReturn(Optional.of(Fixture.user()));
        when(disciplinaFindUseCase.execute(Fixture.USER_ROLE, Fixture.USER_ID))
                .thenThrow(new RuntimeException("Falha ao listar disciplinas"));

        mockMvc.perform(get("/disciplinas").principal(authentication))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno inesperado"))
                .andExpect(jsonPath("$.path").value("/disciplinas"));
    }
}
