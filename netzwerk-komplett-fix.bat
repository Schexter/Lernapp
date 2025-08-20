@echo off
REM Fachinformatiker Lernapp - Komplettes Netzwerk-Fix
REM Erstellt von Hans Hahn - Alle Rechte vorbehalten

title Lernapp Netzwerk-Fix

echo ========================================
echo   LERNAPP NETZWERK-REPARATUR
echo ========================================
echo.

cd "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java"

REM 1. AKTUELLE IP-ADRESSE ERMITTELN
echo [1/6] Ermittle Netzwerk-Informationen...
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

REM 2. ALTE JAVA-PROZESSE BEENDEN
echo [2/6] Beende alte Java-Prozesse auf Port 8080...
for /f "tokens=5" %%p in ('netstat -ano 2^>nul ^| findstr :8080') do (
    echo   Beende Prozess %%p...
    taskkill /PID %%p /F >nul 2>&1
)
echo   ✅ Port 8080 ist jetzt frei!
echo.

REM 3. FIREWALL-REGEL ERSTELLEN (ohne Admin-Rechte versuchen)
echo [3/6] Erstelle Firewall-Regel...
netsh advfirewall firewall add rule name="Lernapp Port 8080" dir=in action=allow protocol=TCP localport=8080 >nul 2>&1
if %errorlevel% equ 0 (
    echo   ✅ Firewall-Regel erfolgreich erstellt!
) else (
    echo   ⚠️  Firewall-Regel konnte nicht automatisch erstellt werden
    echo      Lösung: Script als Administrator ausführen oder manuell hinzufügen
)
echo.

REM 4. SERVER-KONFIGURATION PRÜFEN
echo [4/6] Prüfe Server-Konfiguration...
findstr "server.address=0.0.0.0" src\main\resources\application.properties >nul
if %errorlevel% equ 0 (
    echo   ✅ Server lauscht auf allen Interfaces (0.0.0.0)
) else (
    echo   ❌ Server-Konfiguration fehlerhaft!
    echo   💡 Füge 'server.address=0.0.0.0' zur application.properties hinzu
)
echo.

REM 5. APP STARTEN
echo [5/6] Starte Lernapp...
echo   ⏳ Das kann 30-60 Sekunden dauern...
echo   📱 Watching for startup completion...
echo.

start "Lernapp Backend" cmd /k "echo Starte Lernapp Backend... && gradlew.bat clean bootRun"

REM Warten auf App-Start
timeout /t 5 /nobreak >nul
echo   ⏳ Warte auf App-Start (noch 30 Sekunden)...
timeout /t 30 /nobreak >nul

REM 6. VERBINDUNGSTEST
echo [6/6] Teste Verbindungen...
echo.

REM Localhost Test
echo 🏠 LOCALHOST-TEST:
curl -s -o nul -w "Status: %%{http_code}" http://localhost:8080 --connect-timeout 3
if %errorlevel% equ 0 (
    echo   ✅ http://localhost:8080 - ERREICHBAR
) else (
    echo   ❌ http://localhost:8080 - NICHT ERREICHBAR
    echo      App ist noch nicht gestartet oder hat Fehler
)
echo.

REM Netzwerk-IP Tests
echo 🌐 NETZWERK-TESTS:
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /C:"IPv4"') do (
    set "ip=%%a"
    setlocal EnableDelayedExpansion
    set "ip=!ip: =!"
    if not "!ip!"=="127.0.0.1" (
        echo   Testing: http://!ip!:8080
        curl -s -o nul -w "   Status: %%{http_code}" http://!ip!:8080 --connect-timeout 3
        if !errorlevel! equ 0 (
            echo   ✅ ERREICHBAR - App funktioniert im Netzwerk!
            echo.
            echo   🎯 ERFOLG! Zugriff von anderen Geräten möglich:
            echo      http://!ip!:8080
        ) else (
            echo   ❌ NICHT ERREICHBAR
            echo      Mögliche Ursachen:
            echo      - App noch nicht vollständig gestartet
            echo      - Firewall blockiert Verbindung
            echo      - Router-/Netzwerk-Einstellungen
        )
    )
    endlocal
)
echo.

echo ========================================
echo   DIAGNOSE ABGESCHLOSSEN
echo ========================================
echo.

echo 📋 ZUSAMMENFASSUNG:
echo   • App läuft auf Port 8080
echo   • Server-Adresse: 0.0.0.0 (alle Interfaces)
echo   • Firewall-Regel: Lernapp Port 8080
echo.

echo 🔧 FALLS IMMER NOCH PROBLEME:
echo   1. Antivirus-Software prüfen
echo   2. Router-Firewall prüfen
echo   3. Script als Administrator ausführen
echo   4. App-Logs prüfen (andere CMD-Fenster)
echo.

echo 💡 WICHTIGE URLs:
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /C:"IPv4"') do (
    set "ip=%%a"
    setlocal EnableDelayedExpansion
    set "ip=!ip: =!"
    if not "!ip!"=="127.0.0.1" (
        echo   🌐 NETZWERK: http://!ip!:8080
    ) else (
        echo   🏠 LOKAL: http://!ip!:8080
    )
    endlocal
)
echo   🛠️  H2-CONSOLE: http://localhost:8080/h2-console
echo.

echo ⚠️  HINWEIS zu Bluetooth/USB-Fehlern:
echo   Diese Browser-Warnungen sind HARMLOS und haben nichts
echo   mit der Netzwerk-Erreichbarkeit zu tun!
echo.

pause