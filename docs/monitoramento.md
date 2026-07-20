# Monitoramento e observabilidade

A solução usa a stack nativa do GCP — **Cloud Logging** e **Cloud Monitoring** —
que já instrumenta Cloud Run e Cloud Functions sem código adicional. Este documento
descreve onde observar e o que alertar.

## Logs

Todos os componentes emitem logs estruturados capturados pelo Cloud Logging.

| Componente | Onde ver |
|---|---|
| Backend (Cloud Run) | Console → Cloud Run → `sysfeedback-service` → aba **LOGS** |
| notifica-urgente | Console → Cloud Functions → `notifica-urgente` → **LOGS** |
| relatorio-semanal | Console → Cloud Functions → `relatorio-semanal` → **LOGS** |

Exemplos de consulta (Logs Explorer):

```
# Publicação de avaliação urgente pelo backend
resource.type="cloud_run_revision"
textPayload:"Avaliacao urgente publicada"

# Envio de e-mail pela função de urgência
resource.type="cloud_function"
resource.labels.function_name="notifica-urgente"
textPayload:"E-mail de avaliacao urgente enviado"

# Erros em qualquer função
resource.type="cloud_function" severity>=ERROR
```

Os logs de negócio foram pensados para rastrear o fluxo ponta a ponta: publicação
(backend) → recebimento/envio (função). Cada passo loga um evento identificável.

## Métricas

Métricas automáticas disponíveis no Cloud Monitoring, por componente:

- **Cloud Run:** contagem de requisições, latências (p50/p95/p99), instâncias ativas,
  uso de CPU/memória, taxa de erro 5xx.
- **Cloud Functions:** execuções, tempo de execução, erros, memória, cold starts.
- **Pub/Sub:** mensagens publicadas/entregues, idade da mensagem mais antiga não
  confirmada (`oldest_unacked_message_age`) — indica atraso no processamento.

## Alertas recomendados

Políticas de alerta (Cloud Monitoring → Alerting) sugeridas:

| Alerta | Condição | Por quê |
|---|---|---|
| Erros nas funções | taxa de execuções com status `error` > 0 em 5 min | Falha ao enviar e-mail (SMTP, secret errado) |
| Backlog no Pub/Sub | `oldest_unacked_message_age` > 10 min | Função não está consumindo o tópico |
| Erros 5xx no backend | taxa de 5xx > 1% em 5 min | Problema na API |
| Falha do Scheduler | job `relatorio-semanal-trigger` com última execução falha | Relatório semanal não disparou |

Notificação dos alertas por e-mail para o administrador (mesmo canal do sistema).

## Rastreamento do fluxo (health check manual)

1. Criar uma avaliação nota 0–3 (ALTA) → log do backend "Avaliacao urgente publicada"
   → log da função "E-mail de avaliacao urgente enviado" → e-mail recebido.
2. Publicar manualmente em `relatorio-semanal`
   (`gcloud pubsub topics publish relatorio-semanal --message=run`) → log da função
   `relatorio-semanal` → e-mail com o relatório.
