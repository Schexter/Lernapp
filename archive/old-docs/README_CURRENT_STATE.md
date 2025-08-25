# 🎓 Fachinformatiker Lernapp - Quick Start Guide

## 🚀 Schnellstart

### 1. Anwendung starten:
```bash
cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java
gradlew.bat :lernapp-web:bootRun
```

### 2. Browser öffnen und navigieren zu:
- **Login:** http://localhost:8080/login
- **Dashboard:** http://localhost:8080/dashboard
- **Static Dashboard (immer zugänglich):** http://localhost:8080/static-dashboard

---

## 📁 AKTUELLE STRUKTUR (Januar 2025)

### ✅ FUNKTIONIERENDE SEITEN:

| Route | Beschreibung | Status |
|-------|--------------|--------|
| `/login` | Neue saubere Login-Seite | ✅ FUNKTIONIERT |
| `/dashboard` | Haupt-Dashboard | ✅ FUNKTIONIERT |
| `/static-dashboard` | Dashboard ohne Auth | ✅ IMMER OK |
| `/api-test` | API Test Console | ✅ DEBUG TOOL |
| `/quick-login` | Quick Login | ✅ FUNKTIONIERT |
| `/learn` | Lernbereich | ✅ OK |
| `/practice` | Übungen | ✅ OK |
| `/exams` | Prüfungen | ✅ OK |
| `/progress` | Fortschritt | ✅ OK |
| `/profile` | Profil | ✅ OK |
| `/settings` | Einstellungen | ✅ OK |

### 🔐 TEST-BENUTZER:

| Username | Passwort | Rolle |
|----------|----------|-------|
| demo | demo123 | Student |
| student | student123 | Student |
| teacher | teacher123 | Lehrer |
| admin | admin123 | Admin |

---

## 🛠️ AKTIVE KOMPONENTEN:

### Controller:
- **UltraSimpleAuthController** - Haupt-Auth API (`/api/auth/*`)
- **WebController** - Alle Web-Routen (sauber organisiert)
- **QuestionApiController** - Fragen API
- **LearningController** - Lern-API
- **UserController** - User Management

### Security:
- **SimplifiedSecurityConfig** - Einfache Security für Development
- Alle Seiten sind ohne Auth zugänglich (Development-Modus)
- API Endpoints optional geschützt

---

## 🔧 FEHLERBEHEBUNG:

### Problem: Login funktioniert nicht
**Lösung:** 
1. Browser Cache leeren (F12 → Application → Clear site data)
2. Verwende http://localhost:8080/api-test zum Testen
3. Oder nutze http://localhost:8080/static-dashboard direkt

### Problem: Redirect-Loop
**Lösung:** 
- Verwende `/login` statt `/login-old`
- Oder gehe direkt zu `/static-dashboard`

### Problem: 500 Fehler bei API
**Lösung:**
- Anwendung neu starten
- Schaue in Konsole nach genauer Fehlermeldung

---

## 📝 ENTWICKLUNGS-NOTIZEN:

### Deaktivierte/Alte Komponenten:
- `AuthController` - Benötigt JWT Dependencies
- `SimpleAuthController` - Ersetzt durch UltraSimple
- `WorkingAuthController` - Hat AuthManager Probleme
- `auth/login.html` - Alte Login-Seite mit Redirect-Loop
- `dashboard-main.html` - Redundant

### Geplante Verbesserungen:
1. JWT Token richtig implementieren
2. Doppelte Templates konsolidieren
3. Alte Controller entfernen
4. Security für Production härten

---

## 📞 SUPPORT:

Bei Problemen:
1. Konsole-Output prüfen
2. Browser DevTools öffnen (F12)
3. `/api-test` Seite verwenden
4. Projekt neu starten mit `gradlew clean bootRun`

---

**Entwickler:** Hans Hahn  
**Stand:** Januar 2025  
**Status:** Development Mode - Alle Auth-Checks vereinfacht
