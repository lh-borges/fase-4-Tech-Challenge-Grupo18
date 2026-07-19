package br.com.fiap.sysfeedback.functions.notifica;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.CloudEventsFunction;
import io.cloudevents.CloudEvent;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Cloud Function (GCP, 2ª geração) acionada por evento do tópico Pub/Sub
 * {@code feedback-urgente}.
 *
 * <p><strong>Responsabilidade única:</strong> notificar o administrador por e-mail
 * quando uma avaliação com urgência ALTA (nota 0–3) é registrada. Não persiste,
 * não consulta o banco e não gera relatórios — apenas transforma um evento em um
 * aviso.</p>
 *
 * <p>Na 2ª geração de Cloud Functions, um gatilho de Pub/Sub entrega um
 * {@link CloudEvent} cujo {@code data} é um JSON no formato
 * {@code MessagePublishedData}:</p>
 *
 * <pre>{@code
 * {
 *   "message": { "data": "<payload em base64>", "attributes": { ... }, "messageId": "..." },
 *   "subscription": "projects/.../subscriptions/..."
 * }
 * }</pre>
 *
 * <p>O {@code message.data} é o {@link AvaliacaoUrgente} publicado pela aplicação,
 * codificado em base64.</p>
 */
@ApplicationScoped
public class NotificaUrgenteFunction implements CloudEventsFunction {

    private static final Logger LOG = Logger.getLogger(NotificaUrgenteFunction.class);

    private final ObjectMapper objectMapper;
    private final Mailer mailer;
    private final String adminEmail;

    @Inject
    public NotificaUrgenteFunction(ObjectMapper objectMapper,
                                   Mailer mailer,
                                   @ConfigProperty(name = "app.admin-email") String adminEmail) {
        this.objectMapper = objectMapper;
        this.mailer = mailer;
        this.adminEmail = adminEmail;
    }

    @Override
    public void accept(CloudEvent event) throws Exception {
        if (event.getData() == null) {
            LOG.warn("Evento recebido sem payload (data nulo). Ignorando.");
            return;
        }
        notificar(event.getData().toBytes());
    }

    /**
     * Núcleo da função: desembrulha o envelope Pub/Sub, monta e envia o e-mail.
     * Recebe os bytes do {@code data} do CloudEvent (o JSON
     * {@code MessagePublishedData}), o que permite testar sem construir um
     * {@link CloudEvent} completo.
     */
    void notificar(byte[] cloudEventData) throws Exception {
        AvaliacaoUrgente avaliacao = extrairAvaliacao(cloudEventData);
        if (avaliacao == null) {
            return;
        }

        String assunto = "[SysFeedback] Avaliacao urgente recebida (urgencia " + avaliacao.urgencia() + ")";
        String corpo = montarCorpo(avaliacao);

        mailer.send(Mail.withText(adminEmail, assunto, corpo));
        LOG.infof("E-mail de avaliacao urgente enviado para %s (urgencia=%s, dataEnvio=%s)",
                adminEmail, avaliacao.urgencia(), avaliacao.dataEnvio());
    }

    /**
     * Desembrulha o CloudEvent do Pub/Sub: lê {@code message.data}, decodifica o
     * base64 e converte o JSON interno em {@link AvaliacaoUrgente}.
     */
    AvaliacaoUrgente extrairAvaliacao(byte[] cloudEventData) throws Exception {
        JsonNode raiz = objectMapper.readTree(cloudEventData);
        JsonNode dataBase64 = raiz.path("message").path("data");

        if (dataBase64.isMissingNode() || dataBase64.isNull()) {
            LOG.warn("CloudEvent sem 'message.data'. Payload inesperado. Ignorando.");
            return null;
        }

        byte[] payload = Base64.getDecoder().decode(dataBase64.asText());
        LOG.debugf("Payload Pub/Sub decodificado: %s", new String(payload, StandardCharsets.UTF_8));

        return objectMapper.readValue(payload, AvaliacaoUrgente.class);
    }

    private String montarCorpo(AvaliacaoUrgente avaliacao) {
        return """
                Uma nova avaliacao critica foi registrada na plataforma SysFeedback.

                Descricao...: %s
                Urgencia....: %s
                Data de envio: %s

                Recomenda-se acompanhar este feedback com prioridade.

                --
                SysFeedback - notificacao automatica (nao responda este e-mail).
                """.formatted(
                avaliacao.descricao(),
                avaliacao.urgencia(),
                avaliacao.dataEnvio());
    }
}
