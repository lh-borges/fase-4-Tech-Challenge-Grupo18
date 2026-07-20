package br.com.fiap.SysFeedback.infrastructure.messaging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvaliacaoUrgenteMessageTest {

    @Test
    void deveCriarPayloadComCampos() {
        AvaliacaoUrgenteMessage message =
                new AvaliacaoUrgenteMessage("Aula confusa", "ALTA", "2026-07-20T10:00:00");

        assertEquals("Aula confusa", message.descricao());
        assertEquals("ALTA", message.urgencia());
        assertEquals("2026-07-20T10:00:00", message.dataEnvio());
    }

    @Test
    void deveCompararRecordsComMesmosValoresComoIguais() {
        AvaliacaoUrgenteMessage message =
                new AvaliacaoUrgenteMessage("Aula confusa", "ALTA", "2026-07-20T10:00:00");
        AvaliacaoUrgenteMessage outro =
                new AvaliacaoUrgenteMessage("Aula confusa", "ALTA", "2026-07-20T10:00:00");

        assertEquals(message, outro);
        assertEquals(message.hashCode(), outro.hashCode());
    }

    @Test
    void deveDiferenciarRecordsComValoresDiferentes() {
        AvaliacaoUrgenteMessage message =
                new AvaliacaoUrgenteMessage("Aula confusa", "ALTA", "2026-07-20T10:00:00");
        AvaliacaoUrgenteMessage outro =
                new AvaliacaoUrgenteMessage("Aula boa", "BAIXA", "2026-07-20T11:00:00");

        assertNotEquals(message, outro);
    }

    @Test
    void deveIncluirCamposNoToString() {
        String texto = new AvaliacaoUrgenteMessage(
                "Aula confusa", "ALTA", "2026-07-20T10:00:00").toString();

        assertTrue(texto.contains("Aula confusa"));
        assertTrue(texto.contains("ALTA"));
        assertTrue(texto.contains("2026-07-20T10:00:00"));
    }
}
