# ADR 0002 — Quarkus como framework das Cloud Functions

- **Status:** Aceito
- **Data:** 2026-07-19
- **Contexto:** Tech Challenge Fase 4 — FIAP — Grupo 18

## Contexto

Este é um trabalho de pós-graduação em **arquitetura Java**; a stack deve ser Java
de ponta a ponta. As funções serverless precisam:

- ser leves (cold start baixo em ambiente serverless);
- integrar com Pub/Sub (gatilho) e SMTP (envio de e-mail);
- rodar em Cloud Functions gen2 com `gcloud functions deploy`.

## Decisão

Usar **Quarkus** (extensão `quarkus-google-cloud-functions`) para as duas funções.

- Entry-point de deploy para gatilho Pub/Sub (gen2 / CloudEvents):
  `io.quarkus.gcp.functions.QuarkusCloudEventsFunction`. A classe de negócio
  implementa `com.google.cloud.functions.CloudEventsFunction`.
- Envio de e-mail via extensão `quarkus-mailer` (SMTP), com credenciais por
  variável de ambiente / Secret Manager.
- **Estratégia de evolução:** começar em **modo JVM** (build simples, feedback
  rápido) e, com o fluxo funcionando ponta a ponta, **migrar para compilação
  nativa (GraalVM)** para reduzir o cold start a dezenas de milissegundos e
  diminuir a imagem — o diferencial de engenharia serverless.

## Alternativas consideradas

- **Spring Cloud Function:** coerente com o backend (Spring Boot), mas mais pesado
  e com cold start maior — contra a proposta serverless enxuta.
- **Node.js / Python:** mais leves, porém quebram a premissa "tudo Java" da pós.

## Build reprodutível

A máquina de desenvolvimento tem Java 17; o projeto exige Java 21. Para eliminar
incompatibilidades, **todo build roda em Docker** (imagem `maven:3.9-eclipse-temurin-21`),
idêntico ao CI. Ver `functions/notifica-urgente/Dockerfile`.

## Consequências

- **Positivas:** Java em tudo, cold start baixo (especialmente native), integração
  de primeira classe com GCP e SMTP, build reprodutível.
- **Negativas / atenção:** o build nativo é mais sensível (reflexão/serialização);
  por isso a adoção é faseada (JVM → native) e sempre via Docker.
