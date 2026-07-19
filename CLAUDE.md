# CLAUDE.md — Guia do projeto (SysFeedback / Tech Challenge Fase 4 — Grupo 18)

Contexto operacional para quem (pessoa ou agente) for trabalhar neste repositório.

## O que é

Plataforma de feedback de aulas. Alunos enviam avaliações (nota 0–10 + descrição);
professores/admins leem e recebem **notificações de itens críticos** e **relatório
semanal**. Foco da Fase 4: **cloud + serverless + mensageria** no GCP.

## Arquitetura (visão rápida)

- `SysFeedback/` — **Spring Boot** (Java 21), Clean/Hexagonal, roda no **Cloud Run**.
  Recebe avaliações, publica evento em Pub/Sub quando urgência = ALTA, e serve os
  dados do relatório.
- `functions/notifica-urgente/` — **Quarkus** Cloud Function (gen2), gatilho Pub/Sub
  `feedback-urgente`, envia e-mail ao admin. Responsabilidade única: notificar.
- `functions/relatorio-semanal/` — **Quarkus** Cloud Function (gen2), agendada via
  Cloud Scheduler → Pub/Sub, envia o relatório semanal. Responsabilidade única: relatar.
- `docs/adr/` — decisões de arquitetura (ADRs).

Detalhes das decisões: ver `docs/adr/` e `docs/lessons.md`.

## Convenções (IMPORTANTES)

- **Build sempre via Docker.** A máquina local tem Java 17; o projeto é Java 21.
  Não depender do Java local. Imagem base: `maven:3.9-eclipse-temurin-21`.
- **Commits:** Conventional Commits (`feat:`, `fix:`, `chore:`, `docs:`, ...).
- **Autoria dos commits:** `Danilo Fernando <danilo.bossanova@hotmail.com>`.
  **NUNCA** adicionar trailer `Co-Authored-By` nem qualquer menção a IA — é trabalho
  acadêmico avaliado.
- **Branches por demanda:**
  - `feat/pubsub-publisher` — publicar eventos no Spring Boot
  - `feat/endpoint-interno-relatorio` — endpoint interno para o relatório
  - `feat/function-notifica-urgente` — função 1
  - `feat/function-relatorio-semanal` — função 2
  - `chore/ci-deploy-functions` — deploy automatizado das funções
  - `docs/readme-arquitetura` — documentação

## Infra GCP

- Projeto: `project-bd8cf2f9-c29c-4e0a-875` · Região: `southamerica-east1`
- App (Cloud Run): https://sysfeedback-service-esdfvnerla-rj.a.run.app
- Tópicos Pub/Sub: `feedback-urgente`, `relatorio-semanal`
- Segredos (Secret Manager): `SMTP_PASSWORD` (e demais credenciais sensíveis)
- Variáveis das funções: `SMTP_USER`, `SMTP_PASSWORD`, `ADMIN_EMAIL`, `APP_URL`
- Observabilidade: Cloud Logging / Monitoring (nativo)

## Estado (2026-07-19)

- [x] Backend Spring Boot completo (auth JWT, avaliações, feedback, seed, deploy Cloud Run)
- [x] Função 1 `notifica-urgente` (Quarkus) — implementada e testada
- [ ] Publisher Pub/Sub no Spring Boot
- [ ] Endpoint interno de relatório
- [ ] Função 2 `relatorio-semanal`
- [ ] CI de deploy das funções
- [ ] Migração das funções para native (GraalVM)
- [ ] README raiz + roteiro do vídeo
