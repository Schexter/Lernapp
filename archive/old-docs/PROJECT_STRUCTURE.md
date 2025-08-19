# 🗂️ PROJEKT-STRUKTUR ÜBERSICHT
## Fachinformatiker Lernapp - Stand: Januar 2025

---

## 📁 CONTROLLER STATUS

### ✅ AKTIVE Controller:
1. **UltraSimpleAuthController** (/api/auth/*) - AKTIV - Hauptauth für Development
2. **WebController** - AKTIV - Routing für alle Web-Seiten
3. **QuestionApiController** - AKTIV - Fragen-API
4. **LearningController** - AKTIV - Lernbereich
5. **UserController** - AKTIV - User-Management

### ❌ DEAKTIVIERTE Controller (nicht löschen, für später):
1. **AuthController** - DEAKTIVIERT - Benötigt JWT Dependencies
2. **SimpleAuthController** - DEAKTIVIERT - Ersetzt durch UltraSimple
3. **WorkingAuthController** - DEAKTIVIERT - Hat AuthManager Probleme

---

## 📄 SEITEN-STRUKTUR

### 🔐 LOGIN-SEITEN (zu viele!):
1. **/login** → auth/login.html - PROBLEMATISCH (Auto-Redirect Loop)
2. **/simple-login** → auth/simple-login.html - Alt
3. **/quick-login** → quick-login.html - FUNKTIONIERT ✅
4. **/api-test** → api-test.html - Debug Tool ✅

### 📊 DASHBOARD-SEITEN:
1. **/dashboard** → dashboard/index.html - Haupt-Dashboard ✅
2. **/dashboard-main** → dashboard-main.html - Alternative
3. **/static-dashboard** → static-dashboard.html - IMMER FUNKTIONIERT ✅
4. **/dashboard-spa** → dashboard/index.html - Gleiche wie /dashboard

### 📚 LERN-BEREICHE:
1. **/learn** → learning/index.html
2. **/practice** → practice.html UND practice/index.html (DUPLIKAT!)
3. **/exams** oder **/exam** → exam.html UND exams/index.html (DUPLIKAT!)
4. **/progress** → progress/index.html
5. **/profile** → profile/index.html
6. **/settings** → settings/index.html

---

## 🔧 SECURITY CONFIGS:
1. **SimplifiedSecurityConfig** - NEU, AKTIV ✅
2. **ProperSecurityConfig** - DEAKTIVIERT
3. **DevSecurityConfig** - DEAKTIVIERT
4. **ApiSecurityConfig** - Unklar

---

## 🎯 EMPFOHLENE BEREINIGUNG:

### Behalten:
- **/quick-login** - Funktioniert gut
- **/dashboard** - Haupt-Dashboard
- **/static-dashboard** - Fallback ohne Auth
- **/api-test** - Nützlich für Debugging
- **UltraSimpleAuthController** - Simple & funktioniert

### Entfernen/Konsolidieren:
- auth/simple-login.html - Redundant
- dashboard-main.html - Redundant
- Doppelte practice/exam Seiten
- Alte deaktivierte Controller (später)

### Fixen:
- auth/login.html - Auto-Redirect Problem beheben
- Klare Trennung zwischen Templates
