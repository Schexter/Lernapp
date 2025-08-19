# Claude Code Instructions - Fachinformatiker Lernapp

## 📌 Projekt-Übersicht
- **Projekt**: Fachinformatiker Lernapp
- **Technologie**: Java 17+ mit Spring Boot 3.x
- **Database**: H2 (Development)
- **Status**: Basis läuft, Authentication fehlt
- **Pfad**: C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

## ✅ Was bereits funktioniert
- Spring Boot Application startet auf Port 8080
- H2 Database mit Test-Daten
- Question Entity, Repository, Service, Controller
- REST API unter `/api/questions`
- HTML Templates vorhanden
- Basic Security Config

## 🎯 Aktueller Fokus
**PHASE 1: React Frontend Setup (PRIORITÄT)**
1. React + TypeScript + TailwindCSS Setup in /frontend
2. Design System implementieren (siehe /frontend/DESIGN_SYSTEM.md)
3. Login/Register Komponenten erstellen
4. API Integration mit Spring Boot Backend
5. Dashboard und Lernmodus UI

**PARALLEL: Authentication System (Backend)**
1. User Entity mit JPA erstellen
2. UserRepository implementieren
3. AuthService mit JWT
4. Login/Register Endpoints

## 💻 Arbeitsweise
1. **Immer zuerst** `TODO.md` und `CHANGELOG.md` lesen
2. **Kleine Schritte** - max. 100 Zeilen Code pro Session
3. **Nach jeder Änderung** mit `start.bat` testen
4. **Bei Fehlern** sofort in `error.log` dokumentieren
5. **Git Commits** mit aussagekräftigen Messages

## 🔧 Wichtige Befehle
```bash
# App starten
start.bat

# Build & Test
./gradlew build
./gradlew test

# Datenbank-Console
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (leer)
```

## 📁 Projekt-Struktur
```
src/main/java/de/lernapp/
├── config/          # Konfigurationen
├── controller/      # REST & Web Controller
├── model/           # JPA Entities
├── repository/      # Data Access Layer
├── service/         # Business Logic
└── LernappApplication.java

src/main/resources/
├── templates/       # HTML Templates (Legacy)
├── static/          # CSS, JS, Images
├── data.sql         # Test-Daten
└── application.yml  # Konfiguration

frontend/            # NEUES REACT FRONTEND
├── src/
│   ├── components/  # UI-Komponenten
│   ├── pages/       # Seiten
│   ├── services/    # API Services
│   └── types/       # TypeScript Types
├── DESIGN_SYSTEM.md # Design-Regeln (ChatGPT basiert)
├── CLAUDE_CODE_INSTRUCTIONS.md
└── package.json
```

## 📝 Code-Standards
- **Style Guide**: Google Java Style
- **Kommentare**: Deutsch für Business Logic
- **Klassen/Methoden**: Englisch
- **Dependency Injection**: Constructor Injection
- **Tests**: Minimum 70% Coverage
- **Keine Spaghetti**: Modulare Klassenstruktur

## ⚠️ Wichtige Hinweise
- **NIEMALS** mehr als 200 Zeilen ohne Test
- **IMMER** error.log bei Problemen aktualisieren
- **CHANGELOG.md** nach jeder Session updaten
- **Alte Module** sind archiviert in `/archive`

## 🚀 Session-Ablauf
1. TODO.md checken - Was ist zu tun?
2. CHANGELOG.md lesen - Was war zuletzt?
3. **Frontend:** DESIGN_SYSTEM.md und CLAUDE_CODE_INSTRUCTIONS.md beachten
4. Code schreiben (max. 100 Zeilen)
5. Mit start.bat (Backend) + npm run dev (Frontend) testen
6. Bei Erfolg: CHANGELOG updaten
7. Bei Fehler: error.log updaten

## 🎨 Frontend Development
- **Design System**: Basiert auf ChatGPT Blueprint - siehe /frontend/DESIGN_SYSTEM.md
- **Tech Stack**: React 18 + TypeScript + TailwindCSS + shadcn/ui
- **API Integration**: REST Calls zu Spring Boot Backend (:8080)
- **Responsive**: Mobile First, TailwindCSS Breakpoints
- **Colors**: Primär #2F6FED, Accent #10B981, strikt aus Design System

---
*Erstellt von Hans Hahn - Alle Rechte vorbehalten*