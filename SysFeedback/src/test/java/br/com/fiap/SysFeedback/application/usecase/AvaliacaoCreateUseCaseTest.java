package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.messaging.NotificadorUrgentePort;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryDisciplinaPort;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.domain.entity.Disciplina;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testa a regra de negócio da criação de avaliação: notificação apenas para
 * urgência ALTA e exigência de matrícula na disciplina. É um teste de unidade puro
 * (o use case é um POJO).
 *
 * @author luisbraserv
 */
class AvaliacaoCreateUseCaseTest {

    private final RepositoryAvaliacaoPort repository = mock(RepositoryAvaliacaoPort.class);
    private final RepositoryDisciplinaPort disciplinaRepository = mock(RepositoryDisciplinaPort.class);
    private final NotificadorUrgentePort notificador = mock(NotificadorUrgentePort.class);
    private final AvaliacaoCreateUseCase useCase =
            new AvaliacaoCreateUseCase(repository, disciplinaRepository, notificador);

    private final UUID disciplinaId = UUID.randomUUID();
    private final UUID alunoId = UUID.randomUUID();

    @BeforeEach
    void configurarDisciplina() {
        when(disciplinaRepository.findById(disciplinaId))
                .thenReturn(Optional.of(new Disciplina(disciplinaId, "Arquitetura", "ARQ")));
        when(repository.save(any())).thenAnswer(inv -> inv.<Avaliacao>getArgument(0));
    }

    @Test
    void notaCriticaDisparaNotificacaoDeUrgencia() {
        when(disciplinaRepository.alunoMatriculado(alunoId, disciplinaId)).thenReturn(true);

        useCase.execute(new AvaliacaoRequestDTO("Aula pessima, desorganizada", 1, disciplinaId), alunoId);

        verify(notificador, times(1)).notificarUrgente(any(Avaliacao.class));
    }

    @Test
    void notaNaoCriticaNaoDisparaNotificacao() {
        when(disciplinaRepository.alunoMatriculado(alunoId, disciplinaId)).thenReturn(true);

        useCase.execute(new AvaliacaoRequestDTO("Otima aula, muito didatica", 9, disciplinaId), alunoId);

        verify(notificador, never()).notificarUrgente(any());
    }

    @Test
    void alunoNaoMatriculadoNaoConsegueAvaliar() {
        when(disciplinaRepository.alunoMatriculado(eq(alunoId), eq(disciplinaId))).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> useCase.execute(new AvaliacaoRequestDTO("Tentando avaliar", 5, disciplinaId), alunoId));

        verify(repository, never()).save(any());
    }
}
