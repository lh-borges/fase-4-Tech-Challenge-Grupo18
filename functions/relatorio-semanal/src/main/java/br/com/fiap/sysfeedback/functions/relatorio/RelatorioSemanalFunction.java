package br.com.fiap.sysfeedback.functions.relatorio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.CloudEventsFunction;
import io.cloudevents.CloudEvent;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Cloud Function (GCP, 2ª geração) acionada semanalmente por evento do tópico
 * Pub/Sub {@code relatorio-semanal} (publicado pelo Cloud Scheduler).
 *
 * <p><strong>Responsabilidade única:</strong> montar e enviar o relatório semanal
 * por e-mail. Não calcula nada: busca os dados já consolidados no backend
 * (endpoint interno protegido por API key) e formata a mensagem.</p>
 *
 * <p>O conteúdo do evento é irrelevante — o Scheduler apenas dá o "tique" semanal.</p>
 */
@ApplicationScoped
public class RelatorioSemanalFunction implements CloudEventsFunction {

    private static final Logger LOG = Logger.getLogger(RelatorioSemanalFunction.class);
    private static final String CAMINHO_RELATORIO = "/internal/relatorio/semanal";

    private final ObjectMapper objectMapper;
    private final Mailer mailer;
    private final String appUrl;
    private final String internalApiKey;
    private final String adminEmail;

    @Inject
    public RelatorioSemanalFunction(ObjectMapper objectMapper,
                                    Mailer mailer,
                                    @ConfigProperty(name = "app.url") String appUrl,
                                    @ConfigProperty(name = "app.internal-api-key") String internalApiKey,
                                    @ConfigProperty(name = "app.admin-email") String adminEmail) {
        this.objectMapper = objectMapper;
        this.mailer = mailer;
        this.appUrl = appUrl;
        this.internalApiKey = internalApiKey;
        this.adminEmail = adminEmail;
    }

    @Override
    public void accept(CloudEvent event) throws Exception {
        RelatorioSemanal relatorio = buscarRelatorio();
        enviar(relatorio);
    }

    /** Chama o endpoint interno do backend e desserializa o relatório. */
    RelatorioSemanal buscarRelatorio() throws Exception {
        URI uri = URI.create(appUrl + CAMINHO_RELATORIO);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("X-Internal-Api-Key", internalApiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Backend retornou status " + response.statusCode() + " ao gerar o relatorio semanal");
        }
        return objectMapper.readValue(response.body(), RelatorioSemanal.class);
    }

    /** Monta e envia o e-mail do relatório. */
    void enviar(RelatorioSemanal relatorio) {
        String assunto = "[SysFeedback] Relatorio semanal de avaliacoes";
        mailer.send(Mail.withText(adminEmail, assunto, montarCorpo(relatorio)));
        LOG.infof("Relatorio semanal enviado para %s (total=%d, media=%.2f)",
                adminEmail, relatorio.totalAvaliacoes(), relatorio.mediaNotas());
    }

    String montarCorpo(RelatorioSemanal r) {
        StringBuilder sb = new StringBuilder();
        sb.append("Relatorio semanal de avaliacoes - SysFeedback\n\n");
        sb.append("Periodo....: ").append(r.periodoInicio()).append(" a ").append(r.periodoFim()).append('\n');
        sb.append("Total......: ").append(r.totalAvaliacoes()).append(" avaliacoes\n");
        sb.append(String.format("Media notas: %.2f%n", r.mediaNotas()));

        sb.append("\nAvaliacoes por urgencia:\n");
        appendContagens(sb, r.avaliacoesPorUrgencia());

        sb.append("\nAvaliacoes por dia:\n");
        appendContagens(sb, r.avaliacoesPorDia());

        sb.append("\nDetalhe das avaliacoes (descricao | urgencia | data de envio):\n");
        if (r.avaliacoes() == null || r.avaliacoes().isEmpty()) {
            sb.append("  (nenhuma avaliacao no periodo)\n");
        } else {
            for (RelatorioSemanal.Item item : r.avaliacoes()) {
                sb.append("  - ").append(item.descricao())
                        .append(" | ").append(item.urgencia())
                        .append(" | ").append(item.dataEnvio())
                        .append('\n');
            }
        }

        sb.append("\n--\nSysFeedback - relatorio automatico (nao responda este e-mail).\n");
        return sb.toString();
    }

    private void appendContagens(StringBuilder sb, Map<String, Long> contagens) {
        if (contagens == null || contagens.isEmpty()) {
            sb.append("  (sem dados)\n");
            return;
        }
        contagens.forEach((chave, qtd) -> sb.append("  ").append(chave).append(": ").append(qtd).append('\n'));
    }
}
