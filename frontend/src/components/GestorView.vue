<script setup>
import { ref, computed, onMounted } from 'vue'
import { api } from '../api.js'

const props = defineProps({ token: String, role: String })

const disciplinas = ref([])
const filtro = ref('')
const avaliacoes = ref([])
const loading = ref(true)
const error = ref('')

const media = computed(() => {
  if (!avaliacoes.value.length) return '—'
  const soma = avaliacoes.value.reduce((s, a) => s + a.nota, 0)
  return (soma / avaliacoes.value.length).toFixed(1)
})

const urgentes = computed(() => avaliacoes.value.filter((a) => a.urgencia === 'ALTA').length)

async function carregarDisciplinas() {
  try {
    disciplinas.value = await api.listarDisciplinas(props.token)
  } catch (e) {
    error.value = 'Não foi possível carregar as disciplinas. ' + e.message
  }
}

async function carregar() {
  loading.value = true
  error.value = ''
  try {
    const dados = await api.listarAvaliacoes(props.token, filtro.value || undefined)
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

onMounted(async () => {
  await carregarDisciplinas()
  await carregar()
})
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
      <div class="filtro">
        <select v-model="filtro" class="select" @change="carregar">
          <option value="">
            {{ role === 'ADMIN' ? 'Todas as disciplinas' : 'Minhas disciplinas' }}
          </option>
          <option v-for="d in disciplinas" :key="d.id" :value="d.id">
            {{ d.nome }} ({{ d.codigo }})
          </option>
        </select>
        <button class="ghost" @click="carregar" :disabled="loading">
          {{ loading ? '…' : 'Atualizar' }}
        </button>
      </div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>
    <p v-else-if="loading" class="muted">Carregando…</p>
    <p v-else-if="!avaliacoes.length" class="muted">Nenhuma avaliação para este filtro.</p>

    <div v-for="a in avaliacoes" :key="a.id" class="card avaliacao">
      <div class="av-top">
        <span class="nota-pill" :class="a.urgencia.toLowerCase()">{{ a.nota }}</span>
        <span class="badge" :class="a.urgencia.toLowerCase()">{{ a.urgencia }}</span>
        <span v-if="a.disciplinaNome" class="disciplina-tag">{{ a.disciplinaNome }}</span>
        <span class="data">{{ fmt(a.dataEnvio) }}</span>
      </div>
      <p class="desc">{{ a.descricao }}</p>
    </div>
  </div>
</template>
