package br.com.fiap.SysFeedback.infrastructure.messaging;

/**
 * Payload publicado no tópico Pub/Sub {@code feedback-urgente}.
 *
 * <p>Espelha o contrato consumido pela Cloud Function {@code notifica-urgente}
 * (record {@code AvaliacaoUrgente}). Campos conforme os "Dados para o e-mail de
 * aviso de urgência" do enunciado. Ver
 * {@code docs/adr/0003-contrato-mensageria-pubsub.md}.</p>
 */
public record AvaliacaoUrgenteMessage(String descricao, String urgencia, String dataEnvio) {
}
