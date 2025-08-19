/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#2F6FED',
          soft: '#E7EEFF',
          dark: '#1D4ED8',
        },
        accent: '#10B981',
        warning: '#F59E0B',
        danger: '#EF4444',
        gray: {
          900: '#0F172A',
          700: '#334155',
          500: '#64748B',
          200: '#E5E7EB',
          100: '#F1F5F9',
        },
        surface: '#FFFFFF',
        background: '#FAFBFC',
        correct: {
          DEFAULT: '#10B981',
          bg: '#E8FFF6',
        },
        incorrect: {
          DEFAULT: '#EF4444',
          bg: '#FFECEC',
        },
      },
    },
  },
  plugins: [],
}