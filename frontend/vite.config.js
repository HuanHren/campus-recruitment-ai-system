import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import UnoCSS from '@unocss/vite'
import Icons from 'unplugin-icons/vite'

export default defineConfig({
  plugins: [
    vue(),
    UnoCSS(),
    Icons({
      compiler: 'vue3',
      autoInstall: true
    })
  ],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
