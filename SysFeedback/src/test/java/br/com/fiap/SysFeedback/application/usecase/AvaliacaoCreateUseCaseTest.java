package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.application.messaging.NotificadorUrgentePort;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryDisciplinaPort;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.domain.entity.Disciplina;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.domain.exception.AvaliacaoInvalidaException;
import br.com.fiap.SysFeedback.domain.exception.UnauthorizedOperationException;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AvaliacaoCreateUseCaseTest {

    private final RepositoryAvaliacaoPort repository = mock(RepositoryAvaliacaoPort.class);
    private final RepositoryDisciplinaPort disciplinaRepository = mock(RepositoryDisciplinaPort.class);
    private final NotificadorUrgentePort notificador = mock(NotificadorUrgentePort.class);
    private final AvaliacaoCreateUseCase useCase =
            new AvaliacaoCreateUseCase(repository, disciplinaRepository, notificador);

    @BeforeEach
    void setUp() {
        when(disciplinaRepository.findById(Fixture.DISCIPLINA_ID))
                .thenReturn(Optional.of(new Disciplina(Fixture.DISCIPLINA_ID, Fixture.DISCIPLINA_NOME, "ARQ")));
        when(disciplinaRepository.alunoMatriculado(Fixture.USER_ID, Fixture.DISCIPLINA_ID))
                .thenReturn(true);
        when(repository.save(any(Avaliacao.class))).thenAnswer(invocation -> {
            Avaliacao avaliacao = invocation.getArgument(0);
            avaliacao.setId(Fixture.AVALIACAO_ID);
            return avaliacao;
        });
    }

    @Test
    void deveCriarAvaliacao() {
        AvaliacaoResponseDTO response = useCase.execute(Fixture.avaliacaoRequestDTOValida(), Fixture.USER_ID);

        assertEquals(Fixture.AVALIACAO_ID, response.id());
        assertEquals(Fixture.DESCRICAO_AVALIACAO, response.descricao());
        assertEquals(Fixture.NOTA_AVALIACAO, response.nota());
        assertEquals(Urgencia.BAIXA, response.urgencia());
        assertEquals(Fixture.DISCIPLINA_ID, response.disciplinaId());
        assertEquals(Fixture.DISCIPLINA_NOME, response.disciplinaNome());
        verify(repository).save(any(Avaliacao.class));
        verify(notificador, never()).notificarUrgente(any());
    }

    @Test
    void deveNotificarQuandoAvaliacaoForUrgente() {
        AvaliacaoRequestDTO request = new AvaliacaoRequestDTO("Aula confusa e sem suporte", 2, Fixture.DISCIPLINA_ID);

        AvaliacaoResponseDTO response = useCase.execute(request, Fixture.USER_ID);

        assertEquals(Urgencia.ALTA, response.urgencia());
        verify(notificador).notificarUrgente(any(Avaliacao.class));
    }

    @Test
    void naoDeveNotificarQuandoAvaliacaoNaoForUrgente() {
        AvaliacaoRequestDTO request = new AvaliacaoRequestDTO("Aula clara e objetiva", 8, Fixture.DISCIPLINA_ID);

        useCase.execute(request, Fixture.USER_ID);

        verify(notificador, never()).notificarUrgente(any());
    }

    @Test
    void deveLancarExcecaoQuandoDisciplinaNaoExistir() {
        when(disciplinaRepository.findById(Fixture.DISCIPLINA_ID)).thenReturn(Optional.empty());

        UnauthorizedOperationException exception = assertThrows(
                UnauthorizedOperationException.class,
                () -> useCase.execute(Fixture.avaliacaoRequestDTOValida(), Fixture.USER_ID)
        );

        assertEquals("Disciplina não encontrada", exception.getMessage());
        verify(repository, never()).save(any());
        verify(notificador, never()).notificarUrgente(any());
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoEstiverMatriculado() {
        when(disciplinaRepository.alunoMatriculado(Fixture.USER_ID, Fixture.DISCIPLINA_ID))
                .thenReturn(false);

        UnauthorizedOperationException exception = assertThrows(
                UnauthorizedOperationException.class,
                () -> useCase.execute(Fixture.avaliacaoRequestDTOValida(), Fixture.USER_ID)
        );

        assertEquals("Aluno não está matriculado nesta disciplina", exception.getMessage());
        verify(repository, never()).save(any());
        verify(notificador, never()).notificarUrgente(any());
    }

    @Test
    void deveLancarExcecaoQuandoNotaForInvalida() {
        AvaliacaoRequestDTO request = new AvaliacaoRequestDTO("Descricao valida", 11, Fixture.DISCIPLINA_ID);

        assertThrows(AvaliacaoInvalidaException.class,
                () -> useCase.execute(request, Fixture.USER_ID));
        verify(repository, never()).save(any());
    }

    @Test
    void devePropagarExcecaoDoRepositorioAoSalvar() {
        doThrow(new RuntimeException("Erro ao salvar avaliacao"))
                .when(repository).save(any(Avaliacao.class));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> useCase.execute(Fixture.avaliacaoRequestDTOValida(), Fixture.USER_ID));

        assertEquals("Erro ao salvar avaliacao", exception.getMessage());
        verify(notificador, never()).notificarUrgente(any());
    }
}
