package br.com.fiap.SysFeedback.infrastructure.messaging;

import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.fixture.Fixture;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class PubSubNotificadorAdapterTest {

    @Test
    void deveIgnorarNotificacaoQuandoProjectIdForNulo() {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        PubSubNotificadorAdapter adapter =
                new PubSubNotificadorAdapter(objectMapper, null, "feedback-urgente");

        assertDoesNotThrow(() -> adapter.notificarUrgente(Fixture.avaliacao()));
        verifyNoInteractions(objectMapper);
    }

    @Test
    void deveIgnorarNotificacaoQuandoProjectIdForVazio() {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        PubSubNotificadorAdapter adapter =
                new PubSubNotificadorAdapter(objectMapper, "   ", "feedback-urgente");

        assertDoesNotThrow(() -> adapter.notificarUrgente(Fixture.avaliacao()));
        verifyNoInteractions(objectMapper);
    }

    @Test
    void deveIgnorarNotificacaoLocalMesmoComAvaliacaoNula() {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        PubSubNotificadorAdapter adapter =
                new PubSubNotificadorAdapter(objectMapper, "", "feedback-urgente");

        assertDoesNotThrow(() -> adapter.notificarUrgente((Avaliacao) null));
        verifyNoInteractions(objectMapper);
    }
}
