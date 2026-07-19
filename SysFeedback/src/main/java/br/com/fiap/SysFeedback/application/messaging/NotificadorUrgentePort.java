package br.com.fiap.SysFeedback.application.messaging;

import br.com.fiap.SysFeedback.domain.entity.Avaliacao;

/**
 * Porta de saída para notificar avaliações críticas (urgência ALTA).
 *
 * <p>A implementação (adapter na camada de infraestrutura) publica um evento na
 * mensageria (Pub/Sub, tópico {@code feedback-urgente}); o consumidor é a Cloud
 * Function {@code notifica-urgente}, que envia o e-mail ao administrador.</p>
 *
 * <p>Contrato do payload em {@code docs/adr/0003-contrato-mensageria-pubsub.md}.</p>
 */
public interface NotificadorUrgentePort {

    /**
     * Publica o evento de avaliação urgente. É best-effort: uma falha de
     * publicação não deve impedir o registro da avaliação.
     */
    void notificarUrgente(Avaliacao avaliacao);
}
