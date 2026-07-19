# notifica-urgente

Cloud Function serverless (GCP, **2ª geração**) escrita em **Quarkus**. Acionada por
evento do tópico Pub/Sub **`feedback-urgente`**, envia um e-mail ao administrador
sempre que uma avaliação **crítica (urgência ALTA, nota 0–3)** é registrada.

> **Responsabilidade única:** transformar um evento de avaliação urgente em uma
> notificação por e-mail. Não acessa banco, não gera relatório.

## Arquitetura do fluxo

```
Aluno → POST /avaliacoes (Spring Boot / Cloud Run)
             │  se urgência = ALTA
             ▼
   Pub/Sub topic: feedback-urgente
             │  (gatilho de evento, gen2 → CloudEvent)
             ▼
   Cloud Function notifica-urgente  ──SMTP──▶  e-mail para ADMIN_EMAIL
```

## Contrato da mensagem

O produtor publica no campo `data` da mensagem Pub/Sub o seguinte JSON
(ver [`AvaliacaoUrgente`](src/main/java/br/com/fiap/sysfeedback/functions/notifica/AvaliacaoUrgente.java)):

```json
{
  "descricao": "Aula muito confusa, o professor pulou etapas importantes",
  "urgencia": "ALTA",
  "dataEnvio": "2026-07-19T10:00:00"
}
```

Na 2ª geração, o Pub/Sub entrega um **CloudEvent** cujo `data` é um
`MessagePublishedData` (`{ "message": { "data": "<base64>" } }`); a função decodifica
o base64 e desserializa o JSON acima. Os campos batem 1:1 com os "Dados para o e-mail
de aviso de urgência" do enunciado.

## Variáveis de ambiente

| Variável | Origem | Descrição |
|---|---|---|
| `SMTP_USER` | env | e-mail remetente (login SMTP) |
| `SMTP_PASSWORD` | **Secret Manager** | senha de app do SMTP |
| `ADMIN_EMAIL` | env/secret | destinatário das notificações |
| `SMTP_HOST` | env (opcional) | padrão `smtp.gmail.com` |
| `SMTP_PORT` | env (opcional) | padrão `587` (STARTTLS) |

Nenhuma credencial é versionada — tudo chega por ambiente, com `SMTP_PASSWORD`
vindo do Secret Manager.

## Build e testes (via Docker — recomendado)

Não depende do Java local; roda tudo em container com Java 21 + Maven:

```bash
# a partir de functions/notifica-urgente/
docker build -t notifica-urgente:build .
```

O build compila, roda os testes (JUnit + MockMailbox) e gera `target/deployment/`,
o artefato usado pelo deploy.

Build local direto (se tiver Java 21 + Maven):

```bash
mvn clean package
```

## Deploy (Cloud Functions gen2)

Feito de forma automatizada pelo CI (ver `.github/workflows`). Manualmente:

```bash
gcloud functions deploy notifica-urgente \
  --gen2 \
  --region=southamerica-east1 \
  --runtime=java21 \
  --source=target/deployment \
  --entry-point=io.quarkus.gcp.functions.QuarkusCloudEventsFunction \
  --trigger-topic=feedback-urgente \
  --set-env-vars=SMTP_USER=...,ADMIN_EMAIL=... \
  --set-secrets=SMTP_PASSWORD=smtp-password:latest
```

O entry-point é sempre `io.quarkus.gcp.functions.QuarkusCloudEventsFunction` — o
Quarkus roteia o evento para a classe
[`NotificaUrgenteFunction`](src/main/java/br/com/fiap/sysfeedback/functions/notifica/NotificaUrgenteFunction.java).
