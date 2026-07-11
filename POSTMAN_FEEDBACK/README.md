# POSTMAN_FEEDBACK

Collection do Postman com **todos os endpoints** da API SysFeedback (Tech Challenge Fase 4 - Grupo 18).

## Como usar

1. No Postman: **Import** > selecione `SysFeedback.postman_collection.json`.
2. Ajuste a variável de collection `baseUrl` se necessário (padrão `http://localhost:8080`).
3. Rode **Auth > Login** com um dos usuários criados pelo seed (ex.: `aluno@fiap.com` / `123456`) — o token JWT é salvo automaticamente na variável `{{token}}` e usado como `Bearer` nas demais requisições. Se preferir criar usuários na mão, use **Auth > Registrar**.
4. Chame os endpoints conforme a role logada.

> Ao trocar de perfil (ex.: de ALUNO para ADMIN), basta rodar o **Login** novamente com o outro e-mail — o `{{token}}` é sobrescrito.

## Endpoints

| Contexto | Método | Rota | Autorização |
|---|---|---|---|
| Auth | POST | `/auth/registrar` | público |
| Auth | POST | `/auth/login` | público |
| Users | POST | `/users` | ADMIN |
| Users | GET | `/users` | ADMIN |
| Users | GET | `/users/email/{email}` | ADMIN, PROFESSOR |
| Users | PUT | `/users/{id}` | autenticado |
| Users | DELETE | `/users/{id}` | ADMIN |
| Avaliações | POST | `/avaliacoes` | ALUNO |
| Avaliações | GET | `/avaliacoes` | PROFESSOR, ADMIN |
| Feedback | POST | `/feedback` | PROFESSOR, ADMIN |
| Feedback | GET | `/feedback` | PROFESSOR, ADMIN |

## Variáveis da collection

- `baseUrl` — URL base da API.
- `token` — preenchido automaticamente pelo Login.
- `userId` — defina manualmente com o `id` de um usuário para os endpoints `PUT`/`DELETE /users`.
