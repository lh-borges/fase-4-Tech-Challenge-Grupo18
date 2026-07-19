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
