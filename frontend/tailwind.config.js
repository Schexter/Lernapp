/** @type {import('tailwindcss').Config} */
export default {
  content: ["./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#2F6FED',
          50: '#F0F5FF',
          100: '#E7EEFF', 
          500: '#2F6FED',
          600: '#1D4ED8',
          900: '#1E3A8A'
        },
        accent: {
          DEFAULT: '#10B981',
          50: '#E8FFF6',
          500: '#10B981',
          600: '#059669'
        },
        warning: '#F59E0B',
        danger: '#EF4444',
        surface: '#FFFFFF'
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      spacing: {
        '18': '4.5rem',
        '88': '22rem',
      }
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
  ],
}

