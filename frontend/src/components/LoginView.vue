<script setup>
import { ref } from 'vue'
import { api } from '../api.js'

const emit = defineEmits(['login'])

const email = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

async function submit() {
  loading.value = true
  error.value = ''
  try {
    const res = await api.login(email.value, password.value)
    emit('login', res.token)
  } catch (e) {
    error.value = e.status === 401 ? 'E-mail ou senha inválidos.' : 'Falha ao entrar. Tente novamente.'
  } finally {
    loading.value = false
  }
}

function preencher(e) {
  email.value = e
  password.value = '123456'
}
</script>

<template>
  <div class="login-wrap">
    <form class="card login" @submit.prevent="submit">
      <div class="brand big">Sys<span>Feedback</span></div>
      <p class="sub">Entre para avaliar suas aulas</p>

      <label>E-mail</label>
      <input v-model="email" type="email" placeholder="voce@fiap.com" required />

      <label>Senha</label>
      <input v-model="password" type="password" placeholder="••••••" required />

      <button class="primary" :disabled="loading">
        {{ loading ? 'Entrando…' : 'Entrar' }}
      </button>

      <p v-if="error" class="error">{{ error }}</p>

      <div class="quick">
        <span>Acesso rápido (seed):</span>
        <button type="button" @click="preencher('aluno@fiap.com')">Aluno</button>
        <button type="button" @click="preencher('professor@fiap.com')">Professor</button>
        <button type="button" @click="preencher('admin@fiap.com')">Admin</button>
      </div>
    </form>
  </div>
</template>
