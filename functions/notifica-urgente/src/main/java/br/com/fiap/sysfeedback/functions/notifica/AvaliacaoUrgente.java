package br.com.fiap.sysfeedback.functions.notifica;

/**
 * Contrato da mensagem publicada no tópico Pub/Sub {@code feedback-urgente}.
 *
 * <p>O produtor (aplicação Spring Boot, no Cloud Run) publica este JSON no campo
 * {@code data} da mensagem Pub/Sub sempre que uma avaliação com urgência ALTA é
 * registrada. Os campos correspondem exatamente aos "Dados para o e-mail de aviso
 * de urgência" exigidos no enunciado: descrição, urgência e data de envio.</p>
 *
 * <pre>{@code
 * {
 *   "descricao": "Aula muito confusa, o professor pulou etapas importantes",
 *   "urgencia": "ALTA",
 *   "dataEnvio": "2026-07-19T10:00:00"
 * }
 * }</pre>
 *
 * @param  descricao  texto da avaliação registrada pelo usuário
 * @param  urgencia   nível de urgência da avaliação (ex.: {@code ALTA})
 * @param  dataEnvio  data e hora do envio, em formato ISO-8601
 *
 * @author Danilo Fernando
 */
public record AvaliacaoUrgente(String descricao, String urgencia, String dataEnvio) {
}
