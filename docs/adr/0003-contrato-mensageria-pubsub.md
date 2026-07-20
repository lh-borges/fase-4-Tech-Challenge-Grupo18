# ADR 0003 — Contrato de mensageria (Pub/Sub) e tópicos

- **Status:** Aceito
- **Data:** 2026-07-19
- **Contexto:** Tech Challenge Fase 4 — FIAP — Grupo 18

## Contexto

A comunicação entre o backend (Cloud Run) e as funções serverless é assíncrona,
via **Pub/Sub**. Precisamos fixar os tópicos e o **formato das mensagens** para que
produtor e consumidores evoluam de forma independente sem quebrar o contrato.

## Decisão

### Tópicos

| Tópico | Produtor | Consumidor | Gatilho |
|---|---|---|---|
| `feedback-urgente` | Spring Boot (ao salvar avaliação ALTA) | `notifica-urgente` | evento de mensagem |
| `relatorio-semanal` | Cloud Scheduler | `relatorio-semanal` | agendamento semanal |

### Payload de `feedback-urgente`

Publicado no campo `data` da mensagem Pub/Sub, em JSON UTF-8:

```json
{
  "descricao": "string — texto da avaliação",
  "urgencia": "ALTA | MEDIA | BAIXA",
  "dataEnvio": "ISO-8601 local, ex.: 2026-07-19T10:00:00"
}
```

Os campos são exatamente os "Dados para o e-mail de aviso de urgência" do
enunciado (descrição, urgência, data de envio). Apenas avaliações **ALTA** são
publicadas — a regra de negócio de urgência vive no domínio do backend.

### Envelope na 2ª geração (CloudEvents)

Cloud Functions gen2 entrega um **CloudEvent** cujo `data` é um
`MessagePublishedData`:

```json
{ "message": { "data": "<payload acima em base64>", "attributes": {}, "messageId": "..." },
  "subscription": "projects/.../subscriptions/..." }
```

A função decodifica `message.data` (base64) e desserializa o JSON de negócio.

## Consequências

- **Positivas:** contrato explícito e versionável; produtor e consumidor
  independentes; payload mínimo (só o necessário para o e-mail).
- **Atenção:** mudanças no payload exigem atualizar produtor e consumidor juntos;
  por isso o contrato está documentado aqui e materializado no record
  `AvaliacaoUrgente` da função.
