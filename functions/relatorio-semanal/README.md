# relatorio-semanal

Cloud Function serverless (GCP, **2ª geração**) em **Quarkus**. Acionada
**semanalmente** (Cloud Scheduler → tópico Pub/Sub **`relatorio-semanal`**), busca o
relatório consolidado no backend e o envia por e-mail ao administrador.

> **Responsabilidade única:** montar e enviar o relatório semanal. Não calcula nada
> — os dados já vêm agregados do backend.

## Arquitetura do fluxo

```
Cloud Scheduler (semanal)
        │  publica
        ▼
Pub/Sub topic: relatorio-semanal
        │  (gatilho de evento, gen2 → CloudEvent)
        ▼
Cloud Function relatorio-semanal
        │  POST {APP_URL}/internal/relatorio/semanal  (header X-Internal-Api-Key)
        ▼
   Spring Boot (Cloud Run) devolve o relatorio consolidado
        │
        └──SMTP──▶ e-mail para ADMIN_EMAIL
```

O "tique" do Scheduler apenas dispara a função; o conteúdo da mensagem é irrelevante.

## Relatório (conteúdo do e-mail)

Conforme os "Dados para o relatório semanal" do enunciado:
descrição, urgência e data de envio de cada avaliação, além de **média das notas**,
**quantidade por dia** e **quantidade por urgência**.

## Variáveis de ambiente

| Variável | Origem | Descrição |
|---|---|---|
| `SMTP_USER` | env | e-mail remetente (login SMTP) |
| `SMTP_PASSWORD` | **Secret Manager** | senha de app do SMTP |
| `ADMIN_EMAIL` | env/secret | destinatário do relatório |
| `APP_URL` | env | URL do backend Spring Boot (Cloud Run) |
| `INTERNAL_API_KEY` | **Secret Manager** | chave do endpoint interno `/internal/**` |
| `SMTP_HOST` / `SMTP_PORT` | env (opcional) | padrão `smtp.gmail.com` / `587` |

## Build e testes (via Docker)

```bash
# a partir de functions/relatorio-semanal/
docker build -t relatorio-semanal:build .
```

Compila, roda os testes (formatação + envio via MockMailbox) e gera `target/deployment/`.

## Deploy (Cloud Functions gen2)

Automatizado no CI. Manualmente:

```bash
gcloud functions deploy relatorio-semanal \
  --gen2 \
  --region=southamerica-east1 \
  --runtime=java21 \
  --source=target/deployment \
  --entry-point=io.quarkus.gcp.functions.QuarkusCloudEventsFunction \
  --trigger-topic=relatorio-semanal \
  --set-env-vars=SMTP_USER=...,ADMIN_EMAIL=...,APP_URL=https://... \
  --set-secrets=SMTP_PASSWORD=smtp-password:latest,INTERNAL_API_KEY=internal-api-key:latest
```

O agendamento é feito por um Cloud Scheduler publicando no tópico `relatorio-semanal`
(ex.: toda segunda 08:00). Ver a documentação de deploy na raiz do repositório.
