# 🚨 WINDOWS-ANLEITUNG: APP STARTEN UND CSV IMPORTIEREN

## ✅ KORREKTE WINDOWS-BEFEHLE:

### 1️⃣ APP STARTEN (Windows)
```cmd
cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

REM Build und Start - RICHTIG für Windows:
gradlew.bat clean build
gradlew.bat bootRun

REM ODER einfach:
gradlew bootRun

REM ODER mit dem vorhandenen Script:
start.bat
```

### 2️⃣ WARTE BIS APP GESTARTET IST
Warte auf diese Meldung:
```
Started LernappApplication in X.XXX seconds
Tomcat started on port 8080
```

### 3️⃣ NEUES CMD-FENSTER ÖFFNEN (App läuft weiter im ersten!)
Drücke `Windows + R`, tippe `cmd`, Enter

### 4️⃣ CSV-IMPORT TESTEN
```cmd
cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

REM Test-Script ausführen:
test_import.bat
```

### 5️⃣ ALTERNATIVE: DIREKTE CURL-BEFEHLE
Falls test_import.bat nicht funktioniert:

```cmd
REM Prüfe ob Server läuft:
curl http://localhost:8080/api/questions

REM Import ALLE AP1 Fragen:
curl -X POST http://localhost:8080/api/import/ap1/all

REM Statistiken anzeigen:
curl http://localhost:8080/api/import/statistics
```

## 🔧 FALLS CURL NICHT FUNKTIONIERT:

### Option A: PowerShell verwenden
```powershell
# In PowerShell (als Administrator):
Invoke-WebRequest -Uri "http://localhost:8080/api/import/ap1/all" -Method POST
```

### Option B: Browser verwenden
1. Installiere Browser-Extension: "REST Client" oder "Postman"
2. Oder erstelle eine HTML-Seite zum Testen

## 📝 DEBUG-CHECKLISTE:

### Server läuft? Prüfe:
- [ ] Konsole zeigt "Started LernappApplication"
- [ ] http://localhost:8080 im Browser öffnen
- [ ] Keine Fehler in der Konsole

### Import vorbereitet? Prüfe:
- [ ] CSV-Dateien existieren in `data/ap1_questions/`
- [ ] ImportController.java ist kompiliert
- [ ] CsvImportService.java ist kompiliert

## 🚀 QUICK-START KOMPLETTBEFEHL:
```cmd
REM Alles in einem:
cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java
start gradlew bootRun
timeout /t 20
start test_import.bat
```

## ⚠️ HÄUFIGE WINDOWS-FEHLER:

1. **"./gradlew" funktioniert nicht**
   → Verwende `gradlew` oder `gradlew.bat`

2. **"curl" nicht gefunden**
   → Windows 10/11 hat curl eingebaut, sonst:
   → Verwende PowerShell Invoke-WebRequest
   → Oder installiere curl: `winget install curl`

3. **Port 8080 belegt**
   → `netstat -ano | findstr :8080`
   → Task beenden oder Port in application.properties ändern

4. **Gradle-Wrapper fehlt**
   → `gradle wrapper` ausführen (wenn Gradle installiert)
   → Oder gradlew.bat aus Backup kopieren

## 📋 STATUS-CHECK BEFEHLE:
```cmd
REM Java Version prüfen:
java -version

REM Gradle Version prüfen:
gradlew -v

REM Prozesse auf Port 8080:
netstat -ano | findstr :8080

REM Alle Java-Prozesse:
tasklist | findstr java
```

---
ERSTELLT VON HANS HAHN - ALLE RECHTE VORBEHALTEN