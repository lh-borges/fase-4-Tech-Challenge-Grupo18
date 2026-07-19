package br.com.fiap.sysfeedback.functions.notifica;

import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes da função de notificação. Cobrem a parte mais sensível — desembrulhar o
 * envelope Pub/Sub (base64 dentro de {@code message.data}) — e o envio do e-mail,
 * usando o {@link MockMailbox} do Quarkus (ativado no profile de teste).
 *
 * @author Danilo Fernando
 */
@QuarkusTest
class NotificaUrgenteFunctionTest {

    @Inject
    NotificaUrgenteFunction function;

    @Inject
    MockMailbox mailbox;

    /**
     * Limpa a caixa de e-mail simulada antes de cada teste.
     *
     * @author Danilo Fernando
     */
    @BeforeEach
    void limparCaixa() {
        mailbox.clear();
    }

    /**
     * Verifica que a avaliação é extraída corretamente do envelope Pub/Sub.
     *
     * @throws Exception  se a extração do payload falhar
     *
     * @author Danilo Fernando
     */
    @Test
    void extraiAvaliacaoDoEnvelopePubSub() throws Exception {
        String interno = "{\"descricao\":\"Aula muito confusa\",\"urgencia\":\"ALTA\",\"dataEnvio\":\"2026-07-19T10:00:00\"}";
        byte[] cloudEventData = envelopePubSub(interno);

        AvaliacaoUrgente avaliacao = function.extrairAvaliacao(cloudEventData);

        assertNotNull(avaliacao);
        assertEquals("Aula muito confusa", avaliacao.descricao());
        assertEquals("ALTA", avaliacao.urgencia());
        assertEquals("2026-07-19T10:00:00", avaliacao.dataEnvio());
    }

    /**
     * Verifica que um único e-mail é enviado ao admin com os dados da avaliação.
     *
     * @throws Exception  se o processamento do evento falhar
     *
     * @author Danilo Fernando
     */
    @Test
    void enviaEmailComOsDadosDaAvaliacao() throws Exception {
        String interno = "{\"descricao\":\"Nao recomendo, desorganizada\",\"urgencia\":\"ALTA\",\"dataEnvio\":\"2026-07-18T09:30:00\"}";
        byte[] cloudEventData = envelopePubSub(interno);

        function.notificar(cloudEventData);

        var enviados = mailbox.getMessagesSentTo("admin@fiap.com");
        assertEquals(1, enviados.size(), "deveria ter enviado exatamente um e-mail ao admin");
        String corpo = enviados.get(0).getText();
        assertTrue(corpo.contains("Nao recomendo, desorganizada"), "corpo deve conter a descricao");
        assertTrue(corpo.contains("ALTA"), "corpo deve conter a urgencia");
        assertTrue(corpo.contains("2026-07-18T09:30:00"), "corpo deve conter a data de envio");
    }

    /**
     * Monta o JSON {@code MessagePublishedData} com o payload em base64.
     *
     * @param  payloadJson  JSON interno da avaliação a ser embrulhado
     * @return os bytes do envelope Pub/Sub com o payload codificado em base64
     *
     * @author Danilo Fernando
     */
    private static byte[] envelopePubSub(String payloadJson) {
        String base64 = Base64.getEncoder()
                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String envelope = "{\"message\":{\"data\":\"" + base64 + "\"}}";
        return envelope.getBytes(StandardCharsets.UTF_8);
    }
}
