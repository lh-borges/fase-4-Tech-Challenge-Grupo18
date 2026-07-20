# SysFeedback — Frontend

Frontend web simples (**Vue 3 + Vite**) para o SysFeedback. Uma única tela de login
para todos os perfis; o app identifica quem é quem pela **role** contida no JWT e
mostra a interface adequada.

- **ALUNO** → formulário para enviar avaliação (descrição + nota 0–10, com prévia da
  urgência).
- **PROFESSOR / ADMIN** → lista das avaliações recebidas, com resumo (total, média,
  urgentes) e destaque de urgência por cor.

## Como funciona a identificação de perfil

O login (`POST /auth/login`) retorna um JWT. O frontend decodifica o payload do token
(claim `role` e `sub`/e-mail) e renderiza a tela conforme o perfil — sem telas
separadas por tipo de usuário.

## Rodar em desenvolvimento

Requer Node 20+. A partir de `frontend/`:

```bash
npm install
npm run dev      # http://localhost:5173
```

A URL da API é `http://localhost:8080` por padrão. Para apontar a outra, defina
`VITE_API_BASE` (ex.: `VITE_API_BASE=https://sua-api npm run build`).

## Rodar com Docker (junto do backend)

Na raiz do repositório, o `docker-compose.yml` já sobe **banco + backend + frontend**:

```bash
docker compose up --build
```

- Frontend: http://localhost:8081
- Backend:  http://localhost:8080

Usuários do seed (senha `123456`): `aluno@fiap.com`, `professor@fiap.com`, `admin@fiap.com`.

> O backend habilita CORS para permitir o consumo pelo frontend a partir de outra
> origem.

## Estrutura

```
src/
  api.js                 # chamadas HTTP + decode do JWT
  App.vue                # orquestra login e roteia por role
  components/
    LoginView.vue        # tela de login (todos os perfis)
    AlunoView.vue        # envio de avaliação
    GestorView.vue       # lista + resumo (professor/admin)
  style.css              # estilos
```
