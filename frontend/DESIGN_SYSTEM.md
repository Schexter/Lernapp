# 🎨 Fachinformatiker Lernapp - Design System Guide

> **Comprehensive Design Rules for Claude Code Implementation**  
> Based on ChatGPT Frontend Design Blueprint

## 📋 Design Principles

### Core UX Principles
1. **Klarheit vor Komplexität** - Wenige Primäraktionen je Screen
2. **Fokus & Flow** - Lernen hat Vorrang, Nebensachen sind sekundär  
3. **Motivation statt Druck** - Fortschritt sichtbar, positive Microcopy
4. **Zwei Modi trennen** - Lernmodus (mit Hilfe) vs. Prüfungsmodus (ohne Hilfe)
5. **Barrierearm & schnell** - Tastaturbedienung, Screenreader, hohe Kontraste

### Design Adjectives
**Zielgefühl:** ruhig, kompetent, modern, freundlich, konzentriert

## 🎨 Color System

### CSS Custom Properties
```css
:root {
  /* Primary Colors */
  --color-primary: #2F6FED;
  --color-primary-soft: #E7EEFF;
  --color-primary-dark: #1D4ED8;
  
  /* Semantic Colors */
  --color-accent: #10B981;        /* Success/Progress */
  --color-warning: #F59E0B;       /* Hints/Warnings */
  --color-danger: #EF4444;        /* Errors/Wrong answers */
  
  /* Neutral Grays */
  --color-gray-900: #0F172A;      /* Headlines */
  --color-gray-700: #334155;      /* Body text */
  --color-gray-500: #64748B;      /* Muted text */
  --color-gray-200: #E5E7EB;      /* Borders/Dividers */
  --color-gray-100: #F1F5F9;      /* Light backgrounds */
  
  /* Surface Colors */
  --color-surface: #FFFFFF;       /* Cards/Canvas */
  --color-background: #FAFBFC;    /* Page background */
  
  /* Status Colors for Learning */
  --color-correct: #10B981;       /* Correct answers */
  --color-correct-bg: #E8FFF6;    /* Correct background */
  --color-incorrect: #EF4444;     /* Wrong answers */
  --color-incorrect-bg: #FFECEC;  /* Wrong background */
  --color-marked: #E7EEFF;        /* Marked questions */
  --color-neutral: #F1F5F9;       /* Unanswered */
}

/* Dark Mode Variables */
[data-theme="dark"] {
  --color-surface: #0B1020;
  --color-background: #050A14;
  --color-gray-900: #E6EAF3;
  --color-gray-700: #B8BCC8;
  --color-gray-500: #8B8FA3;
  --color-gray-200: #2D3748;
  --color-gray-100: #1A202C;
  --color-primary: #6F9CFF;       /* Slightly desaturated */
}
```

### TailwindCSS Color Palette
```javascript
// tailwind.config.js
module.exports = {
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
        correct: '#10B981',
        incorrect: '#EF4444',
        marked: '#E7EEFF'
      }
    }
  }
}
```

## 🔤 Typography System

### Font Stack
```css
:root {
  /* Primary Font - Headlines & UI */
  --font-primary: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui, sans-serif;
  
  /* Secondary Font - Body Text */
  --font-body: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui, sans-serif;
  
  /* Monospace - Code */
  --font-mono: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
}
```

### Type Scale
```css
:root {
  /* Font Sizes */
  --text-xs: 0.75rem;    /* 12px - Captions, timestamps */
  --text-sm: 0.875rem;   /* 14px - Small labels */
  --text-base: 1rem;     /* 16px - Body text */
  --text-lg: 1.125rem;   /* 18px - Large body */
  --text-xl: 1.25rem;    /* 20px - H3 */
  --text-2xl: 1.5rem;    /* 24px - H2 */
  --text-3xl: 2rem;      /* 32px - H1 */
  
  /* Line Heights */
  --leading-tight: 1.25;
  --leading-normal: 1.5;
  --leading-relaxed: 1.75;
  
  /* Font Weights */
  --font-normal: 400;
  --font-medium: 500;
  --font-semibold: 600;
  --font-bold: 700;
}
```

### Usage Guidelines
```css
/* Headlines */
.heading-1 { font-size: var(--text-3xl); font-weight: var(--font-semibold); line-height: var(--leading-tight); }
.heading-2 { font-size: var(--text-2xl); font-weight: var(--font-semibold); line-height: var(--leading-tight); }
.heading-3 { font-size: var(--text-xl); font-weight: var(--font-medium); line-height: var(--leading-normal); }

/* Body Text */
.body-large { font-size: var(--text-lg); line-height: var(--leading-normal); }
.body-normal { font-size: var(--text-base); line-height: var(--leading-normal); }
.body-small { font-size: var(--text-sm); line-height: var(--leading-normal); }

/* UI Elements */
.caption { font-size: var(--text-xs); color: var(--color-gray-500); }
.label { font-size: var(--text-sm); font-weight: var(--font-medium); }
```

## 📐 Spacing & Layout

### Spacing Scale
```css
:root {
  --space-1: 0.25rem;   /* 4px */
  --space-2: 0.5rem;    /* 8px */
  --space-3: 0.75rem;   /* 12px */
  --space-4: 1rem;      /* 16px */
  --space-5: 1.25rem;   /* 20px */
  --space-6: 1.5rem;    /* 24px */
  --space-8: 2rem;      /* 32px */
  --space-10: 2.5rem;   /* 40px */
  --space-12: 3rem;     /* 48px */
  --space-16: 4rem;     /* 64px */
  --space-20: 5rem;     /* 80px */
}
```

### Border Radius
```css
:root {
  --radius-sm: 0.375rem;    /* 6px - Small elements */
  --radius-md: 0.5rem;      /* 8px - Buttons, inputs */
  --radius-lg: 0.75rem;     /* 12px - Cards */
  --radius-xl: 1rem;        /* 16px - Large cards */
  --radius-2xl: 1.5rem;     /* 24px - Modals */
  --radius-full: 9999px;    /* Pills, avatars */
}
```

### Shadows
```css
:root {
  --shadow-sm: 0 1px 2px rgba(15, 23, 42, 0.04);
  --shadow-md: 0 4px 8px rgba(15, 23, 42, 0.08);
  --shadow-lg: 0 12px 24px rgba(15, 23, 42, 0.12);
  --shadow-xl: 0 24px 48px rgba(15, 23, 42, 0.16);
}
```

## 🧩 Component Library

### Button System
```css
/* Button Base */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-3) var(--space-6);
  border-radius: var(--radius-md);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  transition: all 150ms ease-out;
  cursor: pointer;
  border: none;
  min-height: 44px; /* Touch-friendly */
}

/* Button Variants */
.btn-primary {
  background-color: var(--color-primary);
  color: white;
}
.btn-primary:hover {
  background-color: var(--color-primary-dark);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.btn-secondary {
  background-color: transparent;
  color: var(--color-primary);
  border: 1px solid var(--color-gray-200);
}
.btn-secondary:hover {
  background-color: var(--color-primary-soft);
}

.btn-ghost {
  background-color: transparent;
  color: var(--color-gray-700);
}
.btn-ghost:hover {
  background-color: var(--color-gray-100);
}

/* Button Sizes */
.btn-sm { padding: var(--space-2) var(--space-4); font-size: var(--text-sm); }
.btn-lg { padding: var(--space-4) var(--space-8); font-size: var(--text-lg); }
```

### Card System
```css
.card {
  background-color: var(--color-surface);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--color-gray-200);
}

.card-interactive {
  transition: all 200ms ease-out;
  cursor: pointer;
}
.card-interactive:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-xl);
  border-color: var(--color-primary-soft);
}
```

### Progress Components
```css
/* Progress Ring */
.progress-ring {
  width: 120px;
  height: 120px;
  position: relative;
}

/* Linear Progress */
.progress-linear {
  width: 100%;
  height: 8px;
  background-color: var(--color-gray-100);
  border-radius: var(--radius-full);
  overflow: hidden;
}
.progress-linear-fill {
  height: 100%;
  background-color: var(--color-accent);
  transition: width 300ms ease-out;
}

/* Segmented Progress (für Prüfungen) */
.progress-segmented {
  display: flex;
  gap: var(--space-1);
}
.progress-segment {
  flex: 1;
  height: 4px;
  background-color: var(--color-gray-200);
  border-radius: var(--radius-full);
}
.progress-segment.completed { background-color: var(--color-accent); }
.progress-segment.current { background-color: var(--color-primary); }
```

## 📱 Responsive Design

### Breakpoints
```css
:root {
  --breakpoint-sm: 640px;   /* Mobile landscape */
  --breakpoint-md: 768px;   /* Tablet */
  --breakpoint-lg: 1024px;  /* Desktop */
  --breakpoint-xl: 1280px;  /* Large desktop */
}
```

### Grid System
```css
/* Container */
.container {
  width: 100%;
  max-width: 1240px;
  margin: 0 auto;
  padding: 0 var(--space-4);
}

/* Grid Layouts */
.grid-12 {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: var(--space-6);
}

/* Responsive Grid */
@media (max-width: 768px) {
  .grid-12 { grid-template-columns: repeat(4, 1fr); gap: var(--space-4); }
}
@media (min-width: 769px) and (max-width: 1024px) {
  .grid-12 { grid-template-columns: repeat(8, 1fr); gap: var(--space-5); }
}
```

### Layout Templates
```css
/* Lernmodus Layout (Desktop) */
.learning-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: var(--space-8);
}

/* Mobile: Stack vertically */
@media (max-width: 768px) {
  .learning-layout {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }
}

/* Prüfungsmodus Layout (fokussiert) */
.exam-layout {
  max-width: 800px;
  margin: 0 auto;
  padding: var(--space-6);
}
```

## 🎯 Learning-Specific Components

### Question Status Colors
```css
.question-correct {
  background-color: var(--color-correct-bg);
  border-left: 4px solid var(--color-correct);
}

.question-incorrect {
  background-color: var(--color-incorrect-bg);
  border-left: 4px solid var(--color-incorrect);
}

.question-marked {
  background-color: var(--color-marked);
  border-left: 4px solid var(--color-primary);
}

.question-neutral {
  background-color: var(--color-neutral);
  border-left: 4px solid var(--color-gray-200);
}
```

### Answer Options
```css
.answer-option {
  padding: var(--space-4);
  border: 2px solid var(--color-gray-200);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 150ms ease-out;
  margin-bottom: var(--space-3);
}

.answer-option:hover {
  border-color: var(--color-primary-soft);
  background-color: var(--color-primary-soft);
}

.answer-option.selected {
  border-color: var(--color-primary);
  background-color: var(--color-primary-soft);
}

.answer-option.correct {
  border-color: var(--color-correct);
  background-color: var(--color-correct-bg);
}

.answer-option.incorrect {
  border-color: var(--color-incorrect);
  background-color: var(--color-incorrect-bg);
}
```

### Navigation Components
```css
/* Fragennavigator */
.question-navigator {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: var(--space-2);
}

.question-nav-item {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all 150ms ease-out;
}

.question-nav-item.unanswered { background-color: var(--color-gray-200); }
.question-nav-item.answered { background-color: var(--color-accent); color: white; }
.question-nav-item.marked { background-color: var(--color-primary); color: white; }
.question-nav-item.current { box-shadow: 0 0 0 2px var(--color-primary); }
```

## ⚡ Interactive States

### Hover Effects
```css
.interactive {
  transition: all 150ms ease-out;
}

.interactive:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.card-hover:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-xl);
  border-color: var(--color-primary-soft);
}
```

### Focus States (Accessibility)
```css
.focusable:focus {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

.focus-ring:focus {
  box-shadow: 0 0 0 3px var(--color-primary-soft);
}
```

### Loading States
```css
.skeleton {
  background: linear-gradient(90deg, var(--color-gray-100) 25%, var(--color-gray-200) 50%, var(--color-gray-100) 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid var(--color-gray-200);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
```

## 🌙 Dark Mode

### Implementation Strategy
```css
/* Automatic OS preference detection */
@media (prefers-color-scheme: dark) {
  :root {
    /* Dark mode variables */
  }
}

/* Manual toggle support */
[data-theme="dark"] {
  /* Dark mode overrides */
}
```

### Dark Mode Colors
```css
[data-theme="dark"] {
  --color-surface: #0B1020;
  --color-background: #050A14;
  --color-gray-900: #E6EAF3;
  --color-gray-700: #B8BCC8;
  --color-gray-500: #8B8FA3;
  --color-gray-200: #2D3748;
  --color-gray-100: #1A202C;
  --color-primary: #6F9CFF;
  --shadow-lg: 0 12px 24px rgba(0, 0, 0, 0.3);
}
```

## 📝 Microcopy Guidelines

### Tone of Voice
- **Deutsch** für alle Texte
- **Freundlich und ermutigend**
- **Präzise und hilfreich**
- **Nie demotivierend**

### Beispiele
```typescript
const microcopy = {
  // Positive Bestärkung
  correctAnswer: "Sehr gut! Das war richtig.",
  wrongAnswer: "Fast! Schau dir die Erklärung an.",
  progressComplete: "Toll! Tagesziel erreicht.",
  
  // Navigation
  continueButton: "Weiter",
  backButton: "Zurück", 
  submitButton: "Abgeben",
  
  // Status Messages
  examStarted: "Prüfung gestartet. Viel Erfolg!",
  timeWarning: "Noch 5 Minuten verbleibend",
  examSubmitted: "Prüfung erfolgreich abgegeben",
  
  // Empty States
  noQuestions: "Hier wird bald dein Lernfortschritt angezeigt.",
  noResults: "Starte dein erstes Quiz!"
}
```

## 🧪 Animation Guidelines

### Timing & Easing
```css
:root {
  --duration-fast: 150ms;
  --duration-normal: 250ms;
  --duration-slow: 400ms;
  
  --easing-ease-out: cubic-bezier(0.25, 0.46, 0.45, 0.94);
  --easing-bounce: cubic-bezier(0.68, -0.55, 0.265, 1.55);
}
```

### Animation Principles
1. **Subtle ist besser** - Animationen unterstützen, lenken nicht ab
2. **Feedback geben** - Antwort-Feedback mit sanfter Animation
3. **Orientierung bieten** - Seitenwechsel mit Slide-Animationen
4. **Respektiere Präferenzen** - `prefers-reduced-motion` beachten

```css
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
```

## 📦 Component Checklist

Jede Komponente sollte folgende Eigenschaften haben:

### ✅ Required Features
- [ ] **Responsive Design** (Mobile, Tablet, Desktop)
- [ ] **Dark Mode Support** 
- [ ] **Accessibility** (ARIA, Keyboard Navigation)
- [ ] **Loading States**
- [ ] **Error States**
- [ ] **Focus Management**
- [ ] **TypeScript Types**

### ✅ Optional Features
- [ ] **Animations** (wenn sinnvoll)
- [ ] **Skeleton Loading**
- [ ] **Tooltips/Help Text**
- [ ] **Keyboard Shortcuts**

## 🎯 Implementation Priorities

1. **Core UI Components** (Button, Card, Input)
2. **Layout Components** (Header, Sidebar, Container)
3. **Learning Components** (Question, Progress, Navigator)
4. **Modal/Overlay Components**
5. **Form Components**
6. **Animation & Polish**

---

**Diese Design-Regeln sind die Grundlage für alle Frontend-Entwicklung der Fachinformatiker Lernapp.**

*Erstellt von Hans Hahn basierend auf ChatGPT Frontend Design - Alle Rechte vorbehalten*