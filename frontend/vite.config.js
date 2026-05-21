import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    host: '0.0.0.0',
    allowedHosts: ['.monkeycode-ai.online'],
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/auth': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/user': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/owner': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/rental': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/file': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/avatar': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/message': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/device': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/demand': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/order': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/equipment': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/flyer': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/banner': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/wallet': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/plot': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      }
    }
  }
})