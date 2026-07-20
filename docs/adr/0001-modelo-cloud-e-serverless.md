# ADR 0001 — Modelo de cloud e estratégia serverless

- **Status:** Aceito
- **Data:** 2026-07-19
- **Contexto:** Tech Challenge Fase 4 — FIAP — Grupo 18

## Contexto

O enunciado exige uma plataforma de feedback hospedada em nuvem, com **funções
serverless (mínimo 2, com responsabilidade única)**, que:

1. receba feedbacks dos alunos;
2. **notifique automaticamente** os administradores sobre itens críticos;
3. gere um **relatório semanal** com média das avaliações.

Também pede ambiente seguro, deploy automatizado dos componentes atualizáveis
(ex.: funções) e monitoramento.

## Decisão

Adotamos **Google Cloud Platform (GCP)** com arquitetura **orientada a eventos**:

| Componente | Serviço GCP | Responsabilidade |
|---|---|---|
| API / backend | **Cloud Run** (Spring Boot) | Recebe avaliações, expõe endpoints, **publica eventos** e serve os dados do relatório |
| Mensageria | **Pub/Sub** (`feedback-urgente`, `relatorio-semanal`) | Desacopla produtor (API) dos consumidores (funções) |
| Função 1 | **Cloud Functions gen2** `notifica-urgente` | Envia e-mail ao admin em avaliações ALTA (event-driven) |
| Função 2 | **Cloud Functions gen2** `relatorio-semanal` | Gera e envia o relatório semanal (time-driven, via Cloud Scheduler → Pub/Sub) |
| Agendamento | **Cloud Scheduler** | Dispara o relatório semanal |
| Segredos | **Secret Manager** | `SMTP_PASSWORD` e demais credenciais |
| Observabilidade | **Cloud Logging / Monitoring** | Logs e métricas nativas de Cloud Run e Functions |

As duas funções têm **naturezas distintas** — uma reage a um fato (evento), a outra
ao relógio (agendamento) — o que justifica e evidencia a separação de
responsabilidades cobrada na avaliação.

## Por que Cloud Functions (gen2) e não Cloud Run para as funções?

- O enunciado fala literalmente em **"funções serverless"**; Cloud Functions é a
  correspondência direta.
- A **2ª geração roda sobre a infraestrutura do Cloud Run** por baixo, então
  mantemos o modelo de container sem abrir mão do gatilho nativo de Pub/Sub
  (`--trigger-topic`), evitando peças extras como Eventarc.
- IAM mais simples: cada função recebe apenas as permissões que precisa.

## Consequências

- **Positivas:** baixo acoplamento, escala a zero (custo baixo — importante com
  créditos limitados), deploy independente por componente, separação clara de
  responsabilidades.
- **Negativas / atenção:** latência de *cold start* (mitigada depois com Quarkus
  native — ver [ADR 0002](0002-quarkus-para-cloud-functions.md)); a função de
  relatório precisa de credencial para ler dados do backend
  (ver [ADR 0003](0003-contrato-mensageria-pubsub.md) e endpoint interno).
