# Deploy das Cloud Functions — configuração e segurança

Pipeline: [`.github/workflows/deploy-functions.yml`](../.github/workflows/deploy-functions.yml).
Dispara no push para `main` que altere `functions/**` e faz deploy de cada função
(Cloud Functions **gen2**) de forma independente.

## Segredos (GCP Secret Manager)

Nenhuma credencial fica no código. Crie uma vez (valores reais do grupo):

```bash
PROJECT=project-bd8cf2f9-c29c-4e0a-875

printf '%s' "no-reply@seu-dominio.com"      | gcloud secrets create smtp-user        --data-file=- --project=$PROJECT
printf '%s' "SUA_SENHA_DE_APP"              | gcloud secrets create smtp-password    --data-file=- --project=$PROJECT
printf '%s' "admin@seu-dominio.com"         | gcloud secrets create admin-email      --data-file=- --project=$PROJECT
printf '%s' "UMA_CHAVE_FORTE_ALEATORIA"     | gcloud secrets create internal-api-key --data-file=- --project=$PROJECT
```

> `internal-api-key` deve ser o **mesmo** valor configurado no backend
> (`INTERNAL_API_KEY`, consumido pelo `InternalApiKeyFilter`), pois a função
> `relatorio-semanal` usa essa chave para chamar `/internal/relatorio/semanal`.

## Segredos do GitHub Actions

| Secret | Uso |
|---|---|
| `GCP_WORKLOAD_IDENTITY_PROVIDER` | Federação OIDC (já usado no deploy do Cloud Run) |
| `GCP_SA_EMAIL` | Service account que o CI assume para o deploy |
| `GCP_FUNCTIONS_SA` (opcional) | SA de runtime das funções (least privilege). Se ausente, usa a SA padrão |

## IAM — princípio do menor privilégio

**SA de deploy (CI)** — apenas o necessário para publicar as funções:
`roles/cloudfunctions.developer`, `roles/run.admin` (gen2 roda sobre Cloud Run),
`roles/iam.serviceAccountUser`, `roles/pubsub.editor` (criar tópicos),
`roles/cloudscheduler.admin`, `roles/artifactregistry.writer`.

**SA de runtime das funções** — só lê os segredos que usa:
```bash
gcloud secrets add-iam-policy-binding smtp-password    --member="serviceAccount:$FUNCTIONS_SA" --role="roles/secretmanager.secretAccessor"
gcloud secrets add-iam-policy-binding smtp-user        --member="serviceAccount:$FUNCTIONS_SA" --role="roles/secretmanager.secretAccessor"
gcloud secrets add-iam-policy-binding admin-email      --member="serviceAccount:$FUNCTIONS_SA" --role="roles/secretmanager.secretAccessor"
# apenas relatorio-semanal:
gcloud secrets add-iam-policy-binding internal-api-key --member="serviceAccount:$FUNCTIONS_SA" --role="roles/secretmanager.secretAccessor"
```

**Backend (Cloud Run) — publicar no tópico** de urgência:
```bash
gcloud pubsub topics add-iam-policy-binding feedback-urgente \
  --member="serviceAccount:sa-recebe-feedback@$PROJECT.iam.gserviceaccount.com" \
  --role="roles/pubsub.publisher"
```

Assim: o Cloud Run só **publica** em `feedback-urgente`; a função de urgência é
**acionada** pelo tópico (Eventarc) e só **lê** os segredos de SMTP; a função de
relatório é acionada pelo tópico `relatorio-semanal` e lê também a `internal-api-key`.

## Tópicos e agendamento

O pipeline cria os tópicos (`feedback-urgente`, `relatorio-semanal`) de forma
idempotente e cria/atualiza o **Cloud Scheduler** `relatorio-semanal-trigger`
(toda segunda 08:00, America/Sao_Paulo) publicando em `relatorio-semanal`.

## Variáveis por função

| Função | Trigger | Segredos | Env vars |
|---|---|---|---|
| `notifica-urgente` | tópico `feedback-urgente` | smtp-user, smtp-password, admin-email | — |
| `relatorio-semanal` | tópico `relatorio-semanal` (Scheduler) | smtp-user, smtp-password, admin-email, internal-api-key | `APP_URL` |

## Backend (Cloud Run) — variáveis relacionadas

No deploy do backend, além das já existentes, defina:
`INTERNAL_API_KEY` (secret `internal-api-key`) e `GCP_PROJECT_ID` (ou confie na
auto-detecção do metadata server) para a publicação no Pub/Sub.
