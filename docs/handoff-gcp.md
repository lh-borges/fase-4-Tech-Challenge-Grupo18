# Handoff GCP — o que precisa existir para a mensageria funcionar

Documento para quem administra o GCP (Gilmar). O backend (Cloud Run + Cloud SQL) já
existe; o que segue é o **novo** necessário para as funções serverless e a
mensageria. Projeto: `project-bd8cf2f9-c29c-4e0a-875` · Região: `southamerica-east1`.

> Boa parte é criada automaticamente pelo CI (`.github/workflows/deploy-functions.yml`).
> Só **os segredos** e as **permissões IAM** precisam ser feitos à mão uma vez.

## 1. APIs a habilitar (uma vez)

```bash
gcloud services enable \
  run.googleapis.com cloudfunctions.googleapis.com cloudbuild.googleapis.com \
  artifactregistry.googleapis.com pubsub.googleapis.com cloudscheduler.googleapis.com \
  secretmanager.googleapis.com eventarc.googleapis.com \
  --project=project-bd8cf2f9-c29c-4e0a-875
```

## 2. Segredos no Secret Manager (VALORES reais — só o Gilmar/grupo têm)

Já existe (usado pelo backend): `db-password`, `db-url`, `db-user`, `admin-email`.
**Criar os novos:**

```bash
PROJECT=project-bd8cf2f9-c29c-4e0a-875

# e-mail remetente (login SMTP) e senha de app do provedor (ex.: Gmail)
printf '%s' "no-reply@SEU-DOMINIO.com"   | gcloud secrets create smtp-user     --data-file=- --project=$PROJECT
printf '%s' "SENHA_DE_APP_SMTP"          | gcloud secrets create smtp-password  --data-file=- --project=$PROJECT

# chave forte e aleatória do endpoint interno (a MESMA usada pelo backend e pela função de relatório)
openssl rand -hex 32 | tr -d '\n'        | gcloud secrets create internal-api-key --data-file=- --project=$PROJECT
```

> `admin-email` (destinatário das notificações/relatório) já existe. Se quiser trocar
> o destinatário, atualize esse segredo.

## 3. Tópicos Pub/Sub

Criados pelo CI, mas podem ser pré-criados:

```bash
gcloud pubsub topics create feedback-urgente   --project=$PROJECT
gcloud pubsub topics create relatorio-semanal  --project=$PROJECT
```

## 4. Permissões (IAM) — menor privilégio

**Backend (Cloud Run) publica no tópico de urgência:**
```bash
gcloud pubsub topics add-iam-policy-binding feedback-urgente \
  --member="serviceAccount:sa-recebe-feedback@$PROJECT.iam.gserviceaccount.com" \
  --role="roles/pubsub.publisher" --project=$PROJECT
```

**Backend lê o novo segredo `internal-api-key`:**
```bash
gcloud secrets add-iam-policy-binding internal-api-key \
  --member="serviceAccount:sa-recebe-feedback@$PROJECT.iam.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor" --project=$PROJECT
```

**Service account de runtime das funções** (pode ser a padrão de Compute ou uma
dedicada — recomendada). Ela só precisa **ler** os segredos que usa:
```bash
FUNCTIONS_SA="<SA-de-runtime-das-funcoes>@$PROJECT.iam.gserviceaccount.com"
for s in smtp-user smtp-password admin-email internal-api-key; do
  gcloud secrets add-iam-policy-binding $s \
    --member="serviceAccount:$FUNCTIONS_SA" \
    --role="roles/secretmanager.secretAccessor" --project=$PROJECT
done
```

**Service account de deploy do CI** precisa dos papéis:
`roles/cloudfunctions.developer`, `roles/run.admin`, `roles/iam.serviceAccountUser`,
`roles/pubsub.editor`, `roles/cloudscheduler.admin`, `roles/artifactregistry.writer`.

## 5. Secrets do GitHub Actions (repositório)

Já existem (usados pelo deploy do backend): `GCP_WORKLOAD_IDENTITY_PROVIDER`,
`GCP_SA_EMAIL`. **Opcional novo:** `GCP_FUNCTIONS_SA` (e-mail da SA de runtime das
funções; se não definir, usa a padrão).

## 6. O que o CI cria sozinho (não precisa fazer à mão)

- Deploy das duas funções (`notifica-urgente`, `relatorio-semanal`) — gen2, Java 21.
- Cloud Scheduler `relatorio-semanal-trigger` (segunda 08:00, America/Sao_Paulo)
  publicando em `relatorio-semanal`.
- (Re)deploy do backend já injeta `INTERNAL_API_KEY` e `GCP_PROJECT_ID`.

## 7. Checklist rápido para o Gilmar

- [ ] Habilitar as APIs (passo 1)
- [ ] Criar segredos `smtp-user`, `smtp-password`, `internal-api-key` (passo 2)
- [ ] Conceder IAM: publisher no tópico + secretAccessor (passo 4)
- [ ] (Opcional) criar SA dedicada das funções e o secret `GCP_FUNCTIONS_SA`
- [ ] Confirmar que os secrets do GitHub de deploy existem (passo 5)
- [ ] Dar merge do PR na `main` → o CI faz o resto

## 8. Valor da conta SMTP

Para o e-mail funcionar, é preciso uma conta SMTP com **senha de app** (ex.: Gmail
com verificação em duas etapas → "Senhas de app"). Esse par (e-mail + senha de app)
vira os segredos `smtp-user` / `smtp-password`.
