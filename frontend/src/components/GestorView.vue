<script setup>
import { ref, computed, onMounted } from 'vue'
import { api } from '../api.js'

const props = defineProps({ token: String, role: String })

const avaliacoes = ref([])
const loading = ref(true)
const error = ref('')

const media = computed(() => {
  if (!avaliacoes.value.length) return '—'
  const soma = avaliacoes.value.reduce((s, a) => s + a.nota, 0)
  return (soma / avaliacoes.value.length).toFixed(1)
})

const urgentes = computed(() => avaliacoes.value.filter((a) => a.urgencia === 'ALTA').length)

async function carregar() {
  loading.value = true
  error.value = ''
  try {
    const dados = await api.listarAvaliacoes(props.token)
    // Mais recentes primeiro.
    avaliacoes.value = [...dados].sort((a, b) => (a.dataEnvio < b.dataEnvio ? 1 : -1))
  } catch (e) {
    error.value = 'Não foi possível carregar. ' + e.message
  } finally {
    loading.value = false
  }
}

function fmt(d) {
  return new Date(d).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })
}

onMounted(carregar)
</script>

<template>
  <div class="gestor">
    <div class="stats">
      <div class="stat">
        <span class="n">{{ avaliacoes.length }}</span>
        <span class="l">avaliações</span>
      </div>
      <div class="stat">
        <span class="n">{{ media }}</span>
        <span class="l">média das notas</span>
      </div>
      <div class="stat danger">
        <span class="n">{{ urgentes }}</span>
        <span class="l">urgentes (ALTA)</span>
      </div>
    </div>

    <div class="list-head">
      <h2>Avaliações recebidas</h2>
      <button class="ghost" @click="carregar" :disabled="loading">
        {{ loading ? 'Atualizando…' : 'Atualizar' }}
      </button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading" class="muted">Carregando…</p>
    <p v-else-if="!avaliacoes.length" class="muted">Nenhuma avaliação ainda.</p>

    <div v-for="a in avaliacoes" :key="a.id" class="card avaliacao">
      <div class="av-top">
        <span class="nota-pill" :class="a.urgencia.toLowerCase()">{{ a.nota }}</span>
        <span class="badge" :class="a.urgencia.toLowerCase()">{{ a.urgencia }}</span>
        <span class="data">{{ fmt(a.dataEnvio) }}</span>
      </div>
      <p class="desc">{{ a.descricao }}</p>
    </div>
  </div>
</template>
