package br.com.fiap.SysFeedback.infrastructure.messaging;

import br.com.fiap.SysFeedback.application.messaging.NotificadorUrgentePort;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Adapter Pub/Sub para {@link NotificadorUrgentePort}: publica o evento de
 * avaliação urgente no tópico {@code feedback-urgente}.
 *
 * <p><strong>Best-effort:</strong> qualquer falha ao publicar é logada e engolida —
 * jamais quebra o cadastro da avaliação do aluno. As credenciais vêm do ambiente
 * (Application Default Credentials da service account no Cloud Run).</p>
 *
 * <p>Um {@link Publisher} é criado por chamada e encerrado ao final. Dado o baixo
 * volume de avaliações críticas, o custo é irrelevante e evita canais gRPC
 * ociosos/obsoletos entre requisições.</p>
 */
@Component
public class PubSubNotificadorAdapter implements NotificadorUrgentePort {

    private static final Logger log = LoggerFactory.getLogger(PubSubNotificadorAdapter.class);

    private final ObjectMapper objectMapper;
    private final String projectId;
    private final String topicId;

    public PubSubNotificadorAdapter(
            ObjectMapper objectMapper,
            @Value("${gcp.project-id:}") String projectId,
            @Value("${gcp.pubsub.topic.feedback-urgente:feedback-urgente}") String topicId) {
        this.objectMapper = objectMapper;
        this.projectId = projectId;
        this.topicId = topicId;
    }

    @Override
    public void notificarUrgente(Avaliacao avaliacao) {
        if (projectId == null || projectId.isBlank()) {
            log.warn("gcp.project-id nao configurado; notificacao de urgencia ignorada "
                    + "(esperado em ambiente local sem GCP).");
            return;
        }

        TopicName topicName = TopicName.of(projectId, topicId);
        Publisher publisher = null;
        try {
            publisher = Publisher.newBuilder(topicName).build();

            AvaliacaoUrgenteMessage payload = new AvaliacaoUrgenteMessage(
                    avaliacao.getDescricao(),
                    avaliacao.getUrgencia().name(),
                    avaliacao.getDataEnvio().toString());

            ByteString data = ByteString.copyFromUtf8(objectMapper.writeValueAsString(payload));
            PubsubMessage message = PubsubMessage.newBuilder().setData(data).build();

            String messageId = publisher.publish(message).get(10, TimeUnit.SECONDS);
            log.info("Avaliacao urgente publicada no topico '{}' (messageId={})", topicId, messageId);
        } catch (Exception e) {
            log.error("Falha ao publicar avaliacao urgente no Pub/Sub (topico '{}'): {}",
                    topicId, e.getMessage());
        } finally {
            encerrar(publisher);
        }
    }

    private void encerrar(Publisher publisher) {
        if (publisher == null) {
            return;
        }
        try {
            publisher.shutdown();
            publisher.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
