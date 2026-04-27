import { defineConfig, presetUno } from 'unocss'

export default defineConfig({
  presets: [presetUno()],
  theme: {
    colors: {
      primary: '#2563EB',
      ai: '#7C3AED',
      success: '#10B981',
      warning: '#F59E0B',
      title: '#0F172A',
      body: '#475569',
      muted: '#94A3B8',
      page: '#F6F8FC'
    },
    borderRadius: {
      card: '20px'
    },
    boxShadow: {
      card: '0 18px 46px rgba(15, 23, 42, 0.09)',
      soft: '0 8px 22px rgba(15, 23, 42, 0.06)'
    }
  },
  shortcuts: {
    'page-shell': 'min-h-screen bg-page text-body',
    'app-card': 'rounded-card bg-white border border-slate-200 shadow-soft transition-all duration-200',
    'glass-card': 'rounded-card bg-white/82 border border-slate-200/90 shadow-soft backdrop-blur-md',
    'hover-lift': 'transition-all duration-200 hover:-translate-y-1 hover:shadow-card',
    'ai-gradient': 'bg-gradient-to-br from-primary to-ai text-white',
    'soft-border': 'border border-slate-200/90',
    'section-title': 'text-title text-lg font-800 tracking-normal',
    'stat-card': 'rounded-card bg-white border border-slate-200 shadow-soft p-5 transition-all duration-200 hover:-translate-y-1 hover:shadow-card'
  }
})
