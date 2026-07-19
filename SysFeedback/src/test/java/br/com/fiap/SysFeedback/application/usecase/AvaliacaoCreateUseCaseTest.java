package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.messaging.NotificadorUrgentePort;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testa a regra de negócio de notificação: apenas avaliações com urgência ALTA
 * (nota 0–3) disparam a notificação de urgência. É um teste de unidade puro — o
 * use case é um POJO, sem Spring nem banco.
 *
 * @author Danilo Fernando
 */
class AvaliacaoCreateUseCaseTest {

    private final RepositoryAvaliacaoPort repository = mock(RepositoryAvaliacaoPort.class);
    private final NotificadorUrgentePort notificador = mock(NotificadorUrgentePort.class);
    private final AvaliacaoCreateUseCase useCase = new AvaliacaoCreateUseCase(repository, notificador);

    /**
     * Garante que uma nota crítica (ALTA) dispara a notificação de urgência.
     *
     * @author Danilo Fernando
     */
    @Test
    void notaCriticaDisparaNotificacaoDeUrgencia() {
        when(repository.save(any())).thenAnswer(inv -> inv.<Avaliacao>getArgument(0));

        useCase.execute(new AvaliacaoRequestDTO("Aula pessima, desorganizada", 1)); // nota 1 -> ALTA

        verify(notificador, times(1)).notificarUrgente(any(Avaliacao.class));
    }

    /**
     * Garante que uma nota não crítica (BAIXA) não dispara notificação.
     *
     * @author Danilo Fernando
     */
    @Test
    void notaNaoCriticaNaoDisparaNotificacao() {
        when(repository.save(any())).thenAnswer(inv -> inv.<Avaliacao>getArgument(0));

        useCase.execute(new AvaliacaoRequestDTO("Otima aula, muito didatica", 9)); // nota 9 -> BAIXA

        verify(notificador, never()).notificarUrgente(any());
    }
}
