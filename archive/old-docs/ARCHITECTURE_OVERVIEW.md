# 🏗️ Fachinformatiker Lernapp - Komplette Architektur & Aufbau

*Erstellt von Hans Hahn - Alle Rechte vorbehalten*

## 📊 System-Architektur Übersicht

```
┌──────────────────────────────────────────────────────────────┐
│                        BROWSER (Client)                       │
│  ┌──────────────────────────────────────────────────────┐   │
│  │   Thymeleaf Templates + HTMX + Alpine.js + Tailwind │   │
│  └──────────────────────────────────────────────────────┘   │
└────────────────────▲─────────────▼──────────────────────────┘
                      │     HTTP     │
┌─────────────────────┴─────────────┴──────────────────────────┐
│                    SPRING BOOT APPLICATION                    │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │            PRESENTATION LAYER (lernapp-web)         │    │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────┐ │    │
│  │  │ Controllers  │  │ REST APIs    │  │   DTOs   │ │    │
│  │  │ @Controller  │  │ @RestController  │ Data Transfer│ │    │
│  │  └──────────────┘  └──────────────┘  └──────────┘ │    │
│  └─────────────────────────────────────────────────────┘    │
│                              ▼                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │           SECURITY LAYER (lernapp-security)         │    │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────┐ │    │
│  │  │ Spring Security │  │ JWT Handler  │  │  CORS    │ │    │
│  │  │ Filter Chain │  │ Auth Manager │  │ Config   │ │    │
│  │  └──────────────┘  └──────────────┘  └──────────┘ │    │
│  └─────────────────────────────────────────────────────┘    │
│                              ▼                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │          BUSINESS LAYER (lernapp-core)              │    │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────┐ │    │
│  │  │  Services    │  │ Business     │  │ Learning │ │    │
│  │  │ @Service     │  │ Logic        │  │Algorithm │ │    │
│  │  └──────────────┘  └──────────────┘  └──────────┘ │    │
│  └─────────────────────────────────────────────────────┘    │
│                              ▼                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │         DATA ACCESS LAYER (lernapp-core)            │    │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────┐ │    │
│  │  │ Repositories │  │   Entities   │  │  Cache   │ │    │
│  │  │ Spring Data  │  │ @Entity      │  │  Redis   │ │    │
│  │  └──────────────┘  └──────────────┘  └──────────┘ │    │
│  └─────────────────────────────────────────────────────┘    │
└───────────────────────────▼──────────────────────────────────┘
                            │
┌───────────────────────────▼──────────────────────────────────┐
│                         DATABASE                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │   H2 (Development)  /  PostgreSQL (Production)       │   │
│  └──────────────────────────────────────────────────────┘   │
└───────────────────────────────────────────────────────────────┘
```

## 🎨 Frontend UI/UX Design

### 1. Hauptnavigation & Layout

```
┌─────────────────────────────────────────────────────────────┐
│  🎓 Fachinformatiker Lernapp     [Dashboard][Lernen][Profil]│
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────┐  ┌──────────────────────────────────────┐   │
│  │ Sidebar  │  │         Main Content Area             │   │
│  │          │  │                                        │   │
│  │ • Topics │  │  [Dynamischer Inhalt via HTMX]        │   │
│  │ • Stats  │  │                                        │   │
│  │ • Exams  │  │                                        │   │
│  │ • Admin  │  │                                        │   │
│  └──────────┘  └──────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 2. User Journey / Seitenstruktur

```
START
  │
  ▼
[Landing Page]
  │
  ├─► [Login] ──────► [Dashboard]
  │                          │
  └─► [Register]             ▼
         │            ┌──────────────┐
         │            │ Hauptbereich │
         └──────────► │              │
                      ├── Lernen ────┼──► Thema wählen
                      │              │     └─► Fragen beantworten
                      │              │         └─► Fortschritt
                      │              │
                      ├── Prüfung ───┼──► Prüfung starten
                      │              │     └─► Timer läuft
                      │              │         └─► Ergebnis
                      │              │
                      ├── Statistik ─┼──► Lernfortschritt
                      │              │     └─► Schwächen
                      │              │         └─► Empfehlungen
                      │              │
                      └── Profil ────┼──► Einstellungen
                                          └─► Lernziele
```

## 🗂️ Modul-Struktur im Detail

### lernapp-core (Herzstück)
```java
lernapp-core/
├── domain/
│   ├── model/          // Entities
│   │   ├── User.java
│   │   ├── Question.java
│   │   ├── Answer.java
│   │   ├── Topic.java
│   │   ├── Progress.java
│   │   └── Examination.java
│   │
│   ├── repository/     // Data Access
│   │   ├── UserRepository.java
│   │   ├── QuestionRepository.java
│   │   └── ProgressRepository.java
│   │
│   └── service/        // Business Logic
│       ├── UserService.java
│       ├── LearningService.java
│       ├── SpacedRepetitionService.java
│       └── ExaminationService.java
```

### lernapp-web (Presentation)
```java
lernapp-web/
├── controller/
│   ├── HomeController.java      // @Controller für Views
│   ├── LearningController.java  // HTMX Endpoints
│   ├── DashboardController.java
│   └── api/
│       └── QuestionApiController.java // @RestController für JSON
│
├── dto/                         // Data Transfer Objects
│   ├── UserDTO.java
│   ├── QuestionDTO.java
│   └── ProgressDTO.java
│
└── resources/
    ├── templates/               // Thymeleaf HTML
    │   ├── layout/
    │   │   └── main.html       // Basis-Layout
    │   ├── pages/
    │   │   ├── login.html
    │   │   ├── dashboard.html
    │   │   └── learning.html
    │   └── fragments/          // Wiederverwendbare Teile
    │       ├── header.html
    │       ├── sidebar.html
    │       └── question-card.html
    │
    └── static/
        ├── css/
        │   └── app.css         // Tailwind CSS
        ├── js/
        │   └── app.js          // Alpine.js Components
        └── images/
```

### lernapp-security
```java
lernapp-security/
├── config/
│   ├── SecurityConfig.java     // Spring Security Setup
│   ├── JwtConfig.java
│   └── CorsConfig.java
├── jwt/
│   ├── JwtTokenProvider.java
│   └── JwtAuthenticationFilter.java
└── service/
    └── AuthenticationService.java
```

## 🎮 Kern-Features der App

### 1. Adaptives Lernsystem
```java
// Spaced Repetition Algorithm
class SpacedRepetitionService {
    
    // Berechnet wann eine Frage wieder gezeigt wird
    LocalDateTime calculateNextReview(Progress progress) {
        if (progress.getCorrectStreak() == 0) {
            return now().plusMinutes(10);    // Falsch: 10 Min
        } else if (progress.getCorrectStreak() == 1) {
            return now().plusHours(1);       // 1x richtig: 1 Stunde
        } else if (progress.getCorrectStreak() == 2) {
            return now().plusDays(1);        // 2x richtig: 1 Tag
        } else if (progress.getCorrectStreak() == 3) {
            return now().plusDays(3);        // 3x richtig: 3 Tage
        } else {
            return now().plusWeeks(1);       // 4x+ richtig: 1 Woche
        }
    }
}
```

### 2. Lernmodus-Ablauf
```
User wählt Topic
       ▼
System holt Fragen basierend auf:
- Noch nie gesehen (Priorität 1)
- Falsch beantwortet (Priorität 2)  
- Fällig zur Wiederholung (Priorität 3)
       ▼
Frage anzeigen (HTMX partial update)
       ▼
User antwortet
       ▼
Sofortiges Feedback (grün/rot)
       ▼
Progress tracken & nächste Frage
```

### 3. Dashboard-Komponenten
```
┌─────────────────────────────────────────────┐
│            DASHBOARD                         │
├─────────────────────────────────────────────┤
│                                             │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐   │
│  │  Streak  │ │Progress  │ │Next Goal │   │
│  │   🔥15   │ │   68%    │ │  3 Days  │   │
│  └──────────┘ └──────────┘ └──────────┘   │
│                                             │
│  ┌────────────────────────────────────┐    │
│  │     Lernfortschritt diese Woche    │    │
│  │     ████████████░░░░░ 68%         │    │
│  └────────────────────────────────────┘    │
│                                             │
│  ┌────────────────────────────────────┐    │
│  │      Schwache Themen               │    │
│  │  • Netzwerktechnik (42%)          │    │
│  │  • Datenbanken (51%)              │    │
│  │  • Linux (58%)                    │    │
│  └────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

## 🔄 Request-Flow Beispiel

```
1. User klickt "Nächste Frage" Button
              ▼
2. HTMX sendet Request:
   GET /learning/next-question?topic=5
              ▼
3. LearningController empfängt Request
              ▼
4. LearningService.getNextQuestion(userId, topicId)
   - Prüft Progress des Users
   - Wählt optimale Frage aus
              ▼
5. QuestionRepository.findById(questionId)
              ▼  
6. Controller rendert Thymeleaf Fragment
   "fragments/question-card"
              ▼
7. HTMX ersetzt nur den Question-Bereich
   (Kein Full-Page Reload!)
              ▼
8. User sieht neue Frage sofort
```

## 💾 Datenbank-Schema Beziehungen

```sql
users (1) ──────< (N) progress
  │                      │
  │                      ▼
  │                   questions (1) ────< (N) answers
  │                      ▲
  │                      │
  └──< exam_sessions >───┘
           │
           ▼
      examinations

topics (self-referencing hierarchy)
  │
  └──< questions (N:1)
```

## 🚀 Deployment-Architektur

```
Production:
┌─────────────────────────────────┐
│      Docker Container           │
│  ┌───────────────────────────┐ │
│  │  Spring Boot JAR          │ │
│  │  (Embedded Tomcat)        │ │
│  └───────────────────────────┘ │
└─────────────▼───────────────────┘
              │
┌─────────────▼───────────────────┐
│     PostgreSQL Database         │
│     (Docker Volume)              │
└──────────────────────────────────┘

Optional später:
- Nginx Reverse Proxy
- Redis Cache
- Kubernetes Orchestration
```

## 📅 Zeitplan & Meilensteine

### Phase 1: Foundation Setup (Woche 1-2)
- ✅ Sprint 1.1: Projekt-Setup (FERTIG)
- 🔄 Sprint 1.2: Domain Models (AKTUELL - Claude Code)

### Phase 2: Authentication & Security (Woche 3)
- JWT Implementation
- Login/Logout Endpoints  
- Security Configuration

### Phase 3: Web Frontend (Woche 4-5) ⭐
- Woche 4: Thymeleaf + HTMX + Tailwind CSS Setup
- Woche 5: Login/Register Pages, Dashboard, Navigation

### Phase 4: Learning Engine (Woche 6-7)
- Question Management UI
- Learning Algorithm
- Progress Tracking

### Phase 5: Examination System (Woche 8)
- Prüfungsmodus
- Result Analysis

### Phase 6: Performance & Deployment (Woche 9-10)
- Optimization
- Docker/Kubernetes
- Go-Live!

## 🎯 Frontend-Strategie

### Was wir NICHT machen:
❌ Kein separates React/Angular Frontend  
❌ Kein kompliziertes SPA Setup
❌ Keine Node.js Build-Pipeline

### Was wir STATTDESSEN machen:
✅ **Server-Side Rendering mit Thymeleaf** (Spring Boot integriert)  
✅ **HTMX für Interaktivität** (ohne JavaScript schreiben zu müssen)  
✅ **Tailwind CSS** für modernes Design  
✅ **Alpine.js** für kleine Interaktionen

### Warum dieser Ansatz?
1. **Einfacher** - Alles in einem Spring Boot Projekt
2. **Schneller** - Kein separates Frontend-Backend
3. **Wartbarer** - Ein Tech-Stack, eine Sprache (Java)
4. **Modern** - HTMX ist der neue Trend für Server-Side Apps

## 📊 Realistischer Fortschritt

```
Woche 1-2: Foundation ████████░░ 80% (fast fertig)
Woche 3:   Security   ░░░░░░░░░░ 0%
Woche 4-5: Frontend   ░░░░░░░░░░ 0%  
Woche 6-7: Learning   ░░░░░░░░░░ 0%
Woche 8:   Exams      ░░░░░░░░░░ 0%
Woche 9-10: Deploy    ░░░░░░░░░░ 0%
```

## 🏁 Wann haben wir was?

### In 3 Wochen (Ende Woche 3):
- ✅ Datenbank-Modelle fertig
- ✅ User können sich registrieren/einloggen
- ✅ Basic Security

### In 5 Wochen (Ende Woche 5): ⭐
- ✅ **ERSTE SICHTBARE APP!**
- ✅ Login-Seite
- ✅ Dashboard
- ✅ Navigation
- ✅ Erste Fragen anzeigen

### In 7 Wochen (Ende Woche 7):
- ✅ Fragen beantworten
- ✅ Lernfortschritt tracken
- ✅ Adaptive Learning

### In 10 Wochen (Ende Woche 10):
- ✅ **FERTIGE APP!**
- ✅ Deployed und online
- ✅ Voll funktionsfähig

## 💡 Realistische Einschätzung

**MVP-Strategie für schnellen Erfolg:**
1. **Phase 1-3 durchziehen** (5 Wochen) = Basis-App mit Login und ersten Seiten
2. **Dann MVP launchen** = Etwas Sichtbares haben!
3. **Phase 4-6 iterativ** = Schritt für Schritt Features hinzufügen

**So könntest du schon in 4-5 Wochen eine erste funktionierende App haben!**

---

*Erstellt von Hans Hahn - Alle Rechte vorbehalten*
*Datum: 2025-08-13*
