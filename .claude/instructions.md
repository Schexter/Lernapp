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
**PHASE 1: Authentication System**
1. User Entity mit JPA erstellen
2. UserRepository implementieren
3. AuthService mit JWT
4. Login/Register Endpoints
5. Frontend Forms verbinden

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
├── templates/       # HTML Templates
├── static/          # CSS, JS, Images
├── data.sql         # Test-Daten
└── application.yml  # Konfiguration
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
3. Code schreiben (max. 100 Zeilen)
4. Mit start.bat testen
5. Bei Erfolg: CHANGELOG updaten
6. Bei Fehler: error.log updaten

---
*Erstellt von Hans Hahn - Alle Rechte vorbehalten*