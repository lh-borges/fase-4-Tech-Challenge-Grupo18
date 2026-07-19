<script setup>
import { ref, computed } from 'vue'
import { api } from '../api.js'

const props = defineProps({ token: String })

const descricao = ref('')
const nota = ref(7)
const loading = ref(false)
const sucesso = ref(false)
const error = ref('')

const urgencia = computed(() =>
  nota.value <= 3 ? 'ALTA' : nota.value <= 6 ? 'MEDIA' : 'BAIXA'
)

async function enviar() {
  loading.value = true
  error.value = ''
  sucesso.value = false
  try {
    await api.criarAvaliacao(props.token, descricao.value, nota.value)
    sucesso.value = true
    descricao.value = ''
    nota.value = 7
  } catch (e) {
    error.value = 'Não foi possível enviar. ' + e.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="card form">
    <h2>Avaliar aula</h2>
    <p class="muted">Seu feedback ajuda a melhorar as próximas aulas.</p>

    <label>Como foi a aula?</label>
    <textarea
      v-model="descricao"
      rows="4"
      maxlength="1000"
      placeholder="Escreva seu feedback…"
    ></textarea>

    <div class="nota-row">
      <label>Nota: <strong class="nota-val">{{ nota }}</strong></label>
      <span class="badge" :class="urgencia.toLowerCase()">urgência {{ urgencia }}</span>
    </div>
    <input
      type="range"
      min="0"
      max="10"
      v-model.number="nota"
      class="slider"
      :class="urgencia.toLowerCase()"
    />
    <div class="scale"><span>0</span><span>5</span><span>10</span></div>

    <button class="primary" :disabled="loading || !descricao.trim()" @click="enviar">
      {{ loading ? 'Enviando…' : 'Enviar avaliação' }}
    </button>

    <p v-if="sucesso" class="success">✓ Avaliação enviada. Obrigado!</p>
    <p v-if="error" class="error">{{ error }}</p>
  </div>
</template>
