# TODO - Fachinformatiker Lernapp

## 🔥 KRITISCH - SOFORT FÜR CLAUDE CODE:

### WINDOWS-START PROBLEME BEHEBEN:
1. **NICHT** `./gradlew bootRun` verwenden! 
2. **RICHTIG für Windows:**
   ```cmd
   gradlew bootRun
   # ODER
   gradlew.bat bootRun
   # ODER
   start.bat
   ```

### APP STARTEN UND CSV IMPORT:
1. **Terminal 1:** App starten
   ```cmd
   cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java
   gradlew bootRun
   ```
   WARTEN bis "Started LernappApplication" erscheint!

2. **Terminal 2:** Import ausführen (NEUES Fenster!)
   ```cmd
   cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java
   test_import.bat
   ```

### ODER EINFACH:
```cmd
cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java
quickstart.bat
```

---

## 🚨 PHASE 1: Authentication System (Diese Woche)
- [ ] **User Entity** mit JPA Annotations erstellen
- [ ] **UserRepository** (Spring Data JPA) implementieren
- [ ] **Role & Permission** Entities definieren
- [ ] **AuthService** mit JWT Token Generation
- [ ] **AuthController** mit Login/Register Endpoints
- [ ] **Password Encryption** mit BCrypt
- [ ] **Frontend Forms** mit Backend verbinden
- [ ] **Integration Tests** für Authentication

## ⚠️ PHASE 2: Lernfortschritt-Tracking (Nächste Woche)
- [ ] **UserProgress Entity** erstellen
- [ ] **LearningPath Entity** definieren
- [ ] **ProgressService** implementieren
- [ ] **Spaced Repetition Algorithm** entwickeln
- [ ] **Dashboard** mit Fortschrittsanzeige
- [ ] **Progress API Endpoints** erstellen
- [ ] **Charts/Visualisierungen** einbauen
- [ ] **Tests** für Progress-Tracking

## 💡 PHASE 3: Prüfungsmodus (Übernächste Woche)
- [ ] **Examination Entity** erstellen
- [ ] **ExamSession Management** implementieren
- [ ] **Timer Functionality** einbauen
- [ ] **Question Randomization** Logic
- [ ] **Result Calculation** Service
- [ ] **Certificate Generation** (PDF)
- [ ] **Exam History** speichern
- [ ] **Performance Analytics**

## ✅ PHASE 4: Datenimport & Verwaltung
- [x] **CSV Import Service** implementiert
- [x] **Import Controller** mit REST API
- [x] **Batch Processing** für große Datenmengen
- [x] **Import Validation** & Error Handling
- [x] **Duplikat-Erkennung** implementiert
- [ ] **Admin Interface** für Frageverwaltung
- [ ] **Excel Import** Support
- [ ] **Kategorien-Management**

## 🎨 PHASE 5: Frontend-Optimierung
- [ ] **JavaScript** für dynamische Inhalte
- [ ] **AJAX/Fetch** für API-Calls
- [ ] **Form Validation** (Client-side)
- [ ] **Loading States** & Spinner
- [ ] **Error Handling** & Toasts
- [ ] **Responsive Design** optimieren
- [ ] **Dark Mode** Toggle
- [ ] **Accessibility** (ARIA)

## ✅ ERLEDIGT
- [x] Spring Boot Application Setup
- [x] Gradle Build Configuration
- [x] H2 Database Integration
- [x] Question Entity mit JPA
- [x] QuestionRepository
- [x] QuestionService
- [x] QuestionController (REST API)
- [x] Basic Security Config
- [x] HTML Templates erstellt
- [x] Test-Daten eingefügt
- [x] Projekt bereinigt und strukturiert
- [x] CSV Import implementiert
- [x] Windows-Startscripts erstellt

## 📊 FORTSCHRITT
```
Gesamtfortschritt: ████████████░░░░░░░░ 60%

✅ Infrastruktur:     ████████████████████ 100%
✅ Basis-Backend:      ████████████████████ 100%
✅ Import-System:      ████████████████████ 100%
🟡 Frontend:          ████████░░░░░░░░░░░░  40%
🔴 Authentication:    ░░░░░░░░░░░░░░░░░░░░   0%
🔴 Lernfortschritt:   ░░░░░░░░░░░░░░░░░░░░   0%
🔴 Prüfungsmodus:     ░░░░░░░░░░░░░░░░░░░░   0%
```

## 🎯 NÄCHSTER SCHRITT
**CSV-Import testen mit `quickstart.bat`** und dann User Authentication implementieren!

## 🐛 BEKANNTE PROBLEME
- Windows: `./gradlew` funktioniert nicht → verwende `gradlew` oder `gradlew.bat`
- curl nicht gefunden → PowerShell verwenden oder curl installieren
- Port 8080 belegt → anderen Java-Prozess beenden

---
*Erstellt von Hans Hahn - Alle Rechte vorbehalten*
*Letzte Aktualisierung: 2025-08-19*