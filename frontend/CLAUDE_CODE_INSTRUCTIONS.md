# Frontend Development Instructions für Claude Code

## 🎯 PROJEKT KONTEXT
- **Fachinformatiker Lernapp** - React Frontend für Spring Boot Backend
- **Design Basis:** ChatGPT Frontend Blueprint (siehe DESIGN_SYSTEM.md)
- **Tech Stack:** React 18 + TypeScript + TailwindCSS + shadcn/ui

## 📂 PROJEKTSTRUKTUR
```
frontend/
├── src/
│   ├── components/          # Wiederverwendbare UI-Komponenten
│   │   ├── ui/             # shadcn/ui Basis-Komponenten
│   │   ├── forms/          # Login, Register Formulare
│   │   ├── layout/         # Header, Sidebar, Footer
│   │   └── learning/       # Lern-spezifische Komponenten
│   ├── pages/              # Seiten-Komponenten
│   ├── hooks/              # Custom React Hooks
│   ├── services/           # API Services
│   ├── types/              # TypeScript Definitionen
│   └── utils/              # Hilfsfunktionen
├── public/
├── DESIGN_SYSTEM.md        # Vollständige Design-Regeln
└── package.json
```

## 🎨 DESIGN SYSTEM REGELN

### Farb-Palette (STRIKT befolgen)
```css
:root {
  --color-primary: #2F6FED;
  --color-primary-soft: #E7EEFF;
  --color-accent: #10B981;
  --color-warning: #F59E0B;
  --color-danger: #EF4444;
  --color-gray-900: #0F172A;
  --color-gray-700: #334155;
  --color-surface: #FFFFFF;
}
```

### TailwindCSS Klassen verwenden
```typescript
// Buttons
"bg-primary hover:bg-primary-600 text-white px-6 py-3 rounded-lg"

// Cards
"bg-surface shadow-lg rounded-xl p-6 border border-gray-200"

// Status Colors
"bg-green-50 border-green-500" // Richtige Antwort
"bg-red-50 border-red-500"     // Falsche Antwort
"bg-blue-50 border-primary"    // Markierte Frage
```

## 🧩 KOMPONENTEN-PRIORITÄTEN

### Phase 1: Core Components (ZUERST)
1. **Button** - Primary, Secondary, Ghost variants
2. **Card** - Für Kurse, Fragen, Dashboard-Kacheln
3. **Input** - Text, Email, Password fields
4. **Layout** - Container, Header, Sidebar

### Phase 2: Learning Components
1. **Question** - Frage-Anzeige mit Multiple Choice
2. **Progress** - Ring und Linear Progress Bars
3. **Navigation** - Fragennavigator (Grid mit Status)
4. **Timer** - Countdown für Prüfungsmodus

### Phase 3: Advanced
1. **Modal/Drawer** - Overlays für Info-Panels
2. **Toast** - Benachrichtigungen
3. **Skeleton** - Loading States

## 🔌 API INTEGRATION

### Backend Endpoints (Spring Boot auf :8080)
```typescript
// Authentication
POST /api/auth/login
POST /api/auth/register
GET  /api/auth/me

// Questions
GET  /api/questions
GET  /api/questions/{id}

// Learning Progress (TODO)
GET  /api/progress
POST /api/progress/update
```

### API Service Struktur
```typescript
// services/api.ts
export const api = {
  auth: {
    login: (credentials) => fetch('/api/auth/login', {...}),
    register: (userData) => fetch('/api/auth/register', {...}),
  },
  questions: {
    getAll: () => fetch('/api/questions'),
    getById: (id) => fetch(`/api/questions/${id}`),
  }
}
```

## 📱 RESPONSIVE DESIGN

### Breakpoints (TailwindCSS)
```typescript
// Mobile First Approach
"w-full"           // Default (Mobile)
"md:w-1/2"         // Tablet (768px+)
"lg:w-1/3"         // Desktop (1024px+)
"xl:max-w-6xl"     // Large (1280px+)
```

### Layout Patterns
```typescript
// Desktop: Sidebar + Content
"lg:grid lg:grid-cols-[240px_1fr] lg:gap-8"

// Mobile: Stack vertically
"space-y-4 lg:space-y-0"

// Prüfungsmodus: Zentriert, fokussiert
"max-w-4xl mx-auto px-6"
```

## 🎯 LERN-SPEZIFISCHE REGELN

### Question Component
```typescript
interface Question {
  id: number;
  questionText: string;
  options: string[];      // A, B, C, D
  correctAnswer: string;
  explanation?: string;
  category: string;
  difficulty: "EASY" | "MEDIUM" | "HARD";
}
```

### Status Farben
```typescript
const statusColors = {
  correct: "bg-green-50 border-green-500 text-green-700",
  incorrect: "bg-red-50 border-red-500 text-red-700", 
  marked: "bg-blue-50 border-primary text-blue-700",
  neutral: "bg-gray-50 border-gray-200 text-gray-700"
}
```

### Navigation States
```typescript
// Fragennavigator Grid
"grid grid-cols-10 gap-2"

// Question Status Buttons
const getQuestionStatus = (answered, correct, marked) => {
  if (marked) return "bg-primary text-white";
  if (answered && correct) return "bg-accent text-white";
  if (answered && !correct) return "bg-red-500 text-white";
  return "bg-gray-200 text-gray-700";
}
```

## 🧪 TESTING & QUALITÄT

### TypeScript Strict Mode
```typescript
// Alle Komponenten müssen typisiert sein
interface ButtonProps {
  variant: "primary" | "secondary" | "ghost";
  size?: "sm" | "md" | "lg";
  children: React.ReactNode;
  onClick?: () => void;
  disabled?: boolean;
}
```

### Error Handling
```typescript
// API Calls immer mit try/catch
try {
  const response = await api.questions.getAll();
  setQuestions(response.data);
} catch (error) {
  toast.error("Fehler beim Laden der Fragen");
  console.error(error);
}
```

## 🚀 SETUP & BUILD

### Package.json Scripts
```json
{
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "type-check": "tsc --noEmit"
  }
}
```

### Wichtige Dependencies
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "@types/react": "^18.2.0",
    "typescript": "^5.0.0",
    "tailwindcss": "^3.3.0",
    "@radix-ui/react-*": "latest",
    "react-router-dom": "^6.8.0",
    "zustand": "^4.4.0",
    "@tanstack/react-query": "^4.29.0"
  }
}
```

## 🎨 MICROCOPY (Deutsch)

### Standard Texte
```typescript
const copy = {
  buttons: {
    login: "Anmelden",
    register: "Registrieren", 
    submit: "Abgeben",
    continue: "Weiter",
    back: "Zurück"
  },
  feedback: {
    correct: "Sehr gut! Das war richtig.",
    incorrect: "Fast! Schau dir die Erklärung an.",
    examComplete: "Prüfung erfolgreich abgegeben!"
  },
  navigation: {
    dashboard: "Dashboard",
    learning: "Lernen", 
    exam: "Prüfung",
    progress: "Fortschritt"
  }
}
```

## ⚠️ WICHTIGE REGELN

### DO's ✅
- **Design System strikt befolgen** (Farben, Spacing, Typography)
- **Mobile First** entwickeln
- **TypeScript** für alle Komponenten
- **Accessibility** (ARIA, Keyboard Navigation)
- **Loading States** für alle API Calls
- **Error Boundaries** für Fehlerbehandlung

### DON'Ts ❌
- **Keine inline Styles** - nur TailwindCSS Klassen
- **Keine Magic Numbers** - nur Design Token verwenden
- **Keine ungeprüften API Calls** - immer Error Handling
- **Keine festen Breakpoints** - nur TailwindCSS responsive
- **Keine unbetonten Farben** - nur aus Design System

## 🎯 NÄCHSTE SCHRITTE

1. **Vite React Setup** mit TypeScript
2. **TailwindCSS konfigurieren** mit Design System Farben
3. **shadcn/ui installieren** für Basis-Komponenten
4. **API Service** für Backend-Kommunikation
5. **Login Component** als erstes Feature

## 📝 COMMIT NACHRICHTEN
```
feat: Add login component with validation
fix: Resolve navigation state issue  
style: Update button variants to match design system
refactor: Extract API service utilities
test: Add unit tests for question component
```

---

**Diese Regeln sind bindend für alle Frontend-Entwicklung!**

*Erstellt von Hans Hahn - Frontend Development Guidelines*