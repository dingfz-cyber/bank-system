import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/bankImg': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/product': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/banner': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/apply': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
