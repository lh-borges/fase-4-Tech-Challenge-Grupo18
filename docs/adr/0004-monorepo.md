# ADR 0004 — Monorepo

- **Status:** Aceito
- **Data:** 2026-07-19
- **Contexto:** Tech Challenge Fase 4 — FIAP — Grupo 18

## Contexto

A solução tem três deployables (backend + duas funções). O enunciado pede
"repositório aberto com o código-fonte" e avalia a **separação correta de serviços
e responsabilidades**. Precisávamos decidir entre um repositório único (monorepo) ou
múltiplos repositórios.

## Decisão

Adotar **monorepo**, com fronteiras claras por diretório
(`SysFeedback/`, `functions/notifica-urgente/`, `functions/relatorio-semanal/`) e
**deploy e IAM independentes** por componente.

## Justificativa

- A separação de responsabilidades cobrada é **arquitetural**, não física: pastas
  isoladas + pipelines por *path* + service accounts distintas já a demonstram.
- O CI dispara por caminho: mudou `functions/notifica-urgente/**` → só aquela função
  é reconstruída/deployada. Cada componente é atualizável isoladamente.
- Facilita a avaliação e a gravação do vídeo: um único ponto para clonar, ler e
  demonstrar, com histórico e ADRs juntos.
- Múltiplos repositórios só adicionariam atrito (vários READMEs, pipelines e
  contextos) sem ganho para os critérios avaliados.

## Consequências

- **Positivas:** visão unificada, histórico e decisões no mesmo lugar, deploy
  independente por path, menor sobrecarga operacional.
- **Atenção:** é preciso manter o isolamento real entre módulos (build, deploy e
  permissões separados) para que o monorepo não vire um acoplamento acidental. Os
  workflows por path e as SAs por componente garantem isso.
