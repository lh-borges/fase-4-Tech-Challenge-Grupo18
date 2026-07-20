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
 *
 * @author Danilo Fernando
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

    /**
     * Injeta as dependências e as configurações de acesso ao backend e ao e-mail.
     *
     * @param  objectMapper    serializador Jackson usado para ler o relatório
     * @param  mailer          componente do Quarkus responsável pelo envio de e-mail
     * @param  appUrl          URL base do backend que expõe o endpoint interno
     * @param  internalApiKey  chave de API do endpoint interno do backend
     * @param  adminEmail      endereço do administrador que recebe o relatório
     *
     * @author Danilo Fernando
     */
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

    /**
     * Dispara a geração do relatório: busca os dados no backend e envia o e-mail.
     *
     * @param  event  evento do Scheduler que apenas aciona a execução (ignorado)
     *
     * @throws Exception  se a busca no backend ou o envio do e-mail falhar
     *
     * @author Danilo Fernando
     */
    @Override
    public void accept(CloudEvent event) throws Exception {
        RelatorioSemanal relatorio = buscarRelatorio();
        enviar(relatorio);
    }

    /**
     * Chama o endpoint interno do backend e desserializa o relatório semanal.
     *
     * @return o relatório semanal consolidado retornado pelo backend
     *
     * @throws IllegalStateException  se o backend responder com status diferente de 200
     * @throws Exception              se a chamada HTTP ou a desserialização falhar
     *
     * @author Danilo Fernando
     */
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

    /**
     * Monta e envia por e-mail o relatório semanal ao administrador.
     *
     * @param  relatorio  relatório semanal a ser formatado e enviado
     *
     * @author Danilo Fernando
     */
    void enviar(RelatorioSemanal relatorio) {
        String assunto = "[SysFeedback] Relatorio semanal de avaliacoes";
        mailer.send(Mail.withText(adminEmail, assunto, montarCorpo(relatorio)));
        LOG.infof("Relatorio semanal enviado para %s (total=%d, media=%.2f)",
                adminEmail, relatorio.totalAvaliacoes(), relatorio.mediaNotas());
    }

    /**
     * Monta o corpo em texto puro do e-mail com período, contagens e detalhamento.
     *
     * @param  r  relatório semanal cujos dados serão formatados
     * @return o texto do corpo do e-mail já formatado
     *
     * @author Danilo Fernando
     */
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

    /**
     * Acrescenta ao corpo as contagens do mapa, ou um aviso quando não há dados.
     *
     * @param  sb         acumulador do corpo do e-mail em construção
     * @param  contagens  mapa de chave para quantidade a ser listado
     *
     * @author Danilo Fernando
     */
    private void appendContagens(StringBuilder sb, Map<String, Long> contagens) {
        if (contagens == null || contagens.isEmpty()) {
            sb.append("  (sem dados)\n");
            return;
        }
        contagens.forEach((chave, qtd) -> sb.append("  ").append(chave).append(": ").append(qtd).append('\n'));
    }
}
