# lessons.md — Registro de decisões e aprendizados

Diário técnico do projeto: o que fizemos, **por quê**, e o que aprendemos.
Serve de base para a documentação final e o roteiro do vídeo.

---

## 2026-07-19 — Entendimento do desafio e desenho da mensageria

**Situação inicial.** O backend Spring Boot (`SysFeedback/`) já estava pronto:
Clean Architecture, JWT, CRUD de usuários, avaliações, feedback consolidado, seed e
deploy no Cloud Run. **Faltava toda a parte serverless/mensageria** — que é o núcleo
da Fase 4.

**Requisitos-chave do enunciado (mensageria).**
- Notificação automática de itens críticos → e-mail ao admin (descrição, urgência, data).
- Relatório semanal com média (descrição, urgência, data, qtd/dia, qtd/urgência).
- Mínimo 2 funções serverless, **responsabilidade única** (isso vale nota).

**Decisões tomadas** (detalhes nos ADRs):
- GCP, arquitetura orientada a eventos com **Pub/Sub** (ADR 0001).
- **Quarkus** para as funções, JVM primeiro e **native depois** (ADR 0002).
- Contrato de mensagem fixado (ADR 0003).
- **Monorepo**: a separação de responsabilidades é arquitetural (pastas + deploy e
  IAM independentes), não precisa de múltiplos repositórios. Facilita avaliação e vídeo.

---

## 2026-07-19 — Função 1: `notifica-urgente`

**O que construímos.** Cloud Function gen2 em Quarkus, acionada pelo tópico
`feedback-urgente`, que envia e-mail ao admin via SMTP (`quarkus-mailer`).

**Aprendizados / armadilhas:**

1. **gen2 = CloudEvents.** Na 2ª geração o Pub/Sub NÃO entrega o payload direto: vem
   um `CloudEvent` cujo `data` é um `MessagePublishedData`
   (`{ "message": { "data": "<base64>" } }`). O payload de negócio está em
   `message.data`, **codificado em base64**. Esquecer o decode base64 é o erro clássico.
   Entry-point do Quarkus: `io.quarkus.gcp.functions.QuarkusCloudEventsFunction`.

2. **Testar sem construir CloudEvent.** A interface `CloudEvent` é extensa e trabalhosa
   de instanciar em teste. Extraímos o núcleo (`notificar(byte[])`) do `accept(CloudEvent)`;
   os testes chamam o núcleo com o JSON do envelope, cobrindo o decode base64 e o envio
   sem precisar de um `CloudEvent` real.

3. **MockMailbox.** O `quarkus-mailer` traz `MockMailbox` para asserção de e-mails em
   teste. O mock só liga por padrão em dev/test; em produção definimos
   `quarkus.mailer.mock=false` explicitamente para garantir envio real.

4. **Build reprodutível via Docker.** Java local é 17, projeto é 21 → todo build roda
   em `maven:3.9-eclipse-temurin-21`. Zero dependência do ambiente local.

5. **Segredos fora do código.** Toda credencial vem de env/Secret Manager
   (`SMTP_PASSWORD`). Nada sensível versionado.

**Pendências relacionadas:** publisher no Spring Boot precisa publicar o JSON do
contrato (ADR 0003) no tópico `feedback-urgente` quando a avaliação for ALTA.

---

## 2026-07-19 — Publisher Pub/Sub no Spring Boot (produtor)

Lado produtor que fecha o ciclo da função 1. Ao registrar avaliação **ALTA**, o
backend publica no tópico `feedback-urgente`.

- Porta `NotificadorUrgentePort` (application) + adapter `PubSubNotificadorAdapter`
  (infrastructure) — Clean Architecture preservada. O use case decide o que é
  crítico; a infra só transporta.
- **Best-effort:** falha de publicação nunca quebra o cadastro da avaliação.
- **Degrada sem GCP:** `gcp.project-id` vazio → loga e ignora (dev local e
  `contextLoads` seguem funcionando). Em prod, projeto explícito + ADC.
- **`libraries-bom`** alinha as versões do ecossistema GCP.
- Armadilha: `com.google.cloud.ServiceOptions` está em `google-cloud-core` (não vem
  transitivo do pubsub) — removido, usamos só o project-id configurado.

## 2026-07-19 — Endpoint interno de relatório

A função de relatório precisa dos dados agregados, mas `/feedback` exige JWT de
usuário. Criamos `POST /internal/relatorio/semanal` protegido por **API key**
(`X-Internal-Api-Key`), acesso máquina-a-máquina.

- `InternalApiKeyFilter` (fail-closed, comparação em tempo constante) guarda
  `/internal/**`; `SecurityConfig` libera o path e registra o filtro.
- Armadilha: `addFilterBefore(x, JwtAuthFilter.class)` falha — a âncora precisa ser
  um filtro **conhecido** do Spring Security (`UsernamePasswordAuthenticationFilter`),
  não um filtro custom.
- Reutiliza o domínio `Feedback` (média + contagens) e persiste o snapshot; devolve
  também a lista de avaliações para o e-mail.

## 2026-07-19 — Função 2: relatorio-semanal + CI

- Função Quarkus gen2 acionada por Cloud Scheduler → Pub/Sub `relatorio-semanal`.
  Busca o relatório via `HttpClient` (JDK, sem dependência extra) no endpoint interno
  e envia o e-mail. Núcleo (`montarCorpo`/`enviar`) separado do HTTP para testar.
- Quarkus GCF gera `target/deployment/` com o runner jar — é o `--source` do
  `gcloud functions deploy`.
- CI `deploy-functions.yml`: matriz por função, deploy por *path*, tópicos e
  Scheduler idempotentes, segredos via Secret Manager, IAM mínimo documentado.

**Integração:** todas as branches mergeadas na `main`. Único conflito real —
`application.properties` (blocos gcp.* e internal.api-key inseridos no mesmo ponto) —
resolvido mantendo ambos.
