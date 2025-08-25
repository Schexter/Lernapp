@echo off
REM Fachinformatiker Lernapp - Frontend Netzwerk-Fix
REM Erstellt von Hans Hahn - Alle Rechte vorbehalten

title Lernapp Frontend Netzwerk-Fix

echo ========================================
echo   FRONTEND NETZWERK-REPARATUR
echo   React App auf Port 5173
echo ========================================
echo.

cd "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java\frontend"

REM 1. AKTUELLE IP-ADRESSE ERMITTELN
echo [1/5] Ermittle Netzwerk-Informationen...
echo.
echo IHRE NETZWERK-INTERFACES:
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /C:"IPv4"') do (
    set "ip=%%a"
    setlocal EnableDelayedExpansion
    set "ip=!ip: =!"
    if not "!ip!"=="127.0.0.1" (
        echo   🌐 NETZWERK-IP: !ip!
        set "MAIN_IP=!ip!"
    ) else (
        echo   🏠 LOCALHOST: !ip!
    )
    endlocal
)
echo.

REM 2. ALTE NODE-PROZESSE BEENDEN
echo [2/5] Beende alte Node-Prozesse auf Port 5173...
for /f "tokens=5" %%p in ('netstat -ano 2^>nul ^| findstr :5173') do (
    echo   Beende Prozess %%p...
    taskkill /PID %%p /F >nul 2>&1
)
echo   ✅ Port 5173 ist jetzt frei!
echo.

REM 3. FIREWALL-REGEL FÜR FRONTEND ERSTELLEN
echo [3/5] Erstelle Firewall-Regel für Frontend (Port 5173)...
netsh advfirewall firewall add rule name="Lernapp Frontend Port 5173" dir=in action=allow protocol=TCP localport=5173 >nul 2>&1
if %errorlevel% equ 0 (
    echo   ✅ Firewall-Regel für Frontend erfolgreich erstellt!
) else (
    echo   ⚠️  Firewall-Regel konnte nicht automatisch erstellt werden
    echo      Lösung: Script als Administrator ausführen
)
echo.

REM 4. VITE-KONFIGURATION PRÜFEN
echo [4/5] Prüfe Vite-Konfiguration...
findstr "host: '0.0.0.0'" vite.config.ts >nul
if %errorlevel% equ 0 (
    echo   ✅ Vite lauscht auf allen Interfaces (0.0.0.0)
) else (
    echo   ❌ Vite-Konfiguration fehlerhaft!
    echo   💡 'host: 0.0.0.0' fehlt in vite.config.ts
)
echo.

REM 5. FRONTEND STARTEN
echo [5/5] Starte React Frontend...
echo   ⏳ Das kann 10-30 Sekunden dauern...
echo   📱 Frontend startet auf Port 5173...
echo.

start "Lernapp Frontend" cmd /k "echo Starte React Frontend... && npm run dev"

REM Warten auf Frontend-Start
timeout /t 3 /nobreak >nul
echo   ⏳ Warte auf Frontend-Start (noch 15 Sekunden)...
timeout /t 15 /nobreak >nul

echo.
echo ========================================
echo   🎯 FRONTEND URLS TESTEN
echo ========================================
echo.

REM Localhost Test
echo 🏠 LOCALHOST-TEST:
curl -s -o nul -w "Status: %%{http_code}" http://localhost:5173 --connect-timeout 3
if %errorlevel% equ 0 (
    echo   ✅ http://localhost:5173 - ERREICHBAR
) else (
    echo   ❌ http://localhost:5173 - NICHT ERREICHBAR
    echo      Frontend ist noch nicht gestartet oder hat Fehler
)
echo.

REM Netzwerk-IP Tests
echo 🌐 NETZWERK-TESTS:
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /C:"IPv4"') do (
    set "ip=%%a"
    setlocal EnableDelayedExpansion
    set "ip=!ip: =!"
    if not "!ip!"=="127.0.0.1" (
        echo   Testing: http://!ip!:5173
        curl -s -o nul -w "   Status: %%{http_code}" http://!ip!:5173 --connect-timeout 3
        if !errorlevel! equ 0 (
            echo   ✅ ERREICHBAR - Frontend funktioniert im Netzwerk!
            echo.
            echo   🎯 ERFOLG! Zugriff von anderen Geräten möglich:
            echo      http://!ip!:5173
        ) else (
            echo   ❌ NICHT ERREICHBAR
            echo      Mögliche Ursachen:
            echo      - Frontend noch nicht vollständig gestartet
            echo      - Firewall blockiert Verbindung
            echo      - Router-/Netzwerk-Einstellungen
        )
    )
    endlocal
)
echo.

echo ========================================
echo   📱 MOBILE ACCESS SETUP
echo ========================================
echo.

echo 💡 FÜR ZUGRIFF VON SMARTPHONE/TABLET:
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /C:"IPv4"') do (
    set "ip=%%a"
    setlocal EnableDelayedExpansion
    set "ip=!ip: =!"
    if not "!ip!"=="127.0.0.1" (
        echo   📱 Smartphone/Tablet URL: http://!ip!:5173
        echo   📶 Stelle sicher, dass das Gerät im gleichen WLAN ist!
    )
    endlocal
)
echo.

echo 🔧 FALLS IMMER NOCH PROBLEME:
echo   1. Antivirus-Software prüfen
echo   2. Router-Firewall prüfen  
echo   3. Script als Administrator ausführen
echo   4. Node.js neu installieren
echo.

echo 📋 SYSTEM-ÜBERSICHT:
echo   🖥️  BACKEND:  http://localhost:8080 (nur lokal)
echo   🌐 FRONTEND: http://[IHRE-IP]:5173 (Netzwerk)
echo   🛠️  H2-DB:    http://localhost:8080/h2-console
echo.

echo ⚠️  WICHTIGER HINWEIS:
echo   Das FRONTEND (Port 5173) muss über das Netzwerk erreichbar sein!
echo   Das BACKEND (Port 8080) läuft nur lokal und verbindet sich per API.
echo.

pause