import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Frontend do SysFeedback. A URL da API pode ser sobrescrita em build via VITE_API_BASE.
export default defineConfig({
  plugins: [vue()],
  server: { port: 5173, host: true }
})
