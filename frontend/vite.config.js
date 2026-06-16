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
      },
      '/news': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/dashboard': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/card': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/transaction': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/payee': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/message': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/log': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/points': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/wealth': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/family': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/utility': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/db': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/system': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
