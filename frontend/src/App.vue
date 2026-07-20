<script setup>
import { ref, computed, onMounted } from 'vue'
import { decodeJwt } from './api.js'
import LoginView from './components/LoginView.vue'
import AlunoView from './components/AlunoView.vue'
import GestorView from './components/GestorView.vue'

const token = ref(localStorage.getItem('sf_token') || '')
const claims = computed(() => (token.value ? decodeJwt(token.value) : {}))
const role = computed(() => claims.value.role || '')
const email = computed(() => claims.value.sub || '')

function onLogin(t) {
  token.value = t
  localStorage.setItem('sf_token', t)
}

function logout() {
  token.value = ''
  localStorage.removeItem('sf_token')
}

// Descarta token expirado ao abrir.
onMounted(() => {
  const exp = claims.value.exp
  if (exp && exp * 1000 < Date.now()) logout()
})
</script>

<template>
  <div class="app">
    <header v-if="token" class="topbar">
      <div class="brand">Sys<span>Feedback</span></div>
      <div class="user">
        <span class="badge role" :class="role.toLowerCase()">{{ role }}</span>
        <span class="email">{{ email }}</span>
        <button class="ghost" @click="logout">Sair</button>
      </div>
    </header>

    <main class="content">
      <LoginView v-if="!token" @login="onLogin" />
      <AlunoView v-else-if="role === 'ALUNO'" :token="token" />
      <GestorView v-else :token="token" :role="role" />
    </main>
  </div>
</template>
