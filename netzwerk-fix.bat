@echo off
REM Fachinformatiker Lernapp - Netzwerk Fix Script
REM Erstellt von Hans Hahn - Alle Rechte vorbehalten

echo ========================================
echo   LERNAPP NETZWERK-REPARATUR
echo ========================================
echo.

REM 1. Firewall-Regel für Port 8080 erstellen
echo [1/4] Erstelle Firewall-Regel für Port 8080...
netsh advfirewall firewall add rule name="Fachinformatiker Lernapp Port 8080" dir=in action=allow protocol=TCP localport=8080
if %errorlevel% neq 0 (
    echo   ⚠️  Firewall-Regel konnte nicht erstellt werden. Manuell hinzufügen!
) else (
    echo   ✅ Firewall-Regel erfolgreich erstellt!
)
echo.

REM 2. App starten
echo [2/4] Starte Spring Boot App...
start "Lernapp Backend" cmd /k "gradlew.bat bootRun"
echo   ⏳ App startet im Hintergrund...
echo.

REM 3. Warten auf App-Start
echo [3/4] Warte auf App-Start (15 Sekunden)...
timeout /t 15 /nobreak >nul
echo.

REM 4. Test der Erreichbarkeit
echo [4/4] Teste Erreichbarkeit...
echo.

REM Localhost Test
echo 📱 LOCALHOST-TEST:
curl -s http://localhost:8080 >nul 2>&1
if %errorlevel% neq 0 (
    echo   ❌ http://localhost:8080 - NICHT ERREICHBAR
) else (
    echo   ✅ http://localhost:8080 - ERREICHBAR
)

REM Netzwerk-IP ermitteln und testen
echo.
echo 🌐 NETZWERK-TESTS:
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /i "IPv4"') do (
    set "ip=%%a"
    setlocal EnableDelayedExpansion
    set "ip=!ip: =!"
    if not "!ip!"=="127.0.0.1" (
        echo   Test: http://!ip!:8080
        curl -s http://!ip!:8080 >nul 2>&1
        if !errorlevel! neq 0 (
            echo   ❌ NICHT ERREICHBAR - Firewall oder App-Problem
        ) else (
            echo   ✅ ERREICHBAR - App funktioniert im Netzwerk!
        )
    )
    endlocal
)

echo.
echo ========================================
echo   NETZWERK-SETUP ABGESCHLOSSEN
echo ========================================
echo.
echo 💡 WICHTIGE URLS:
echo    http://localhost:8080 - Lokaler Zugriff
echo    http://[IHRE-IP]:8080 - Netzwerk-Zugriff
echo.
echo 🔧 BEI PROBLEMEN:
echo    1. Firewall in Windows-Einstellungen prüfen
echo    2. Antivirus-Software prüfen
echo    3. Router-Firewall prüfen (bei externem Zugriff)
echo.
echo ⚠️  BLUETOOTH/USB FEHLER IGNORIEREN:
echo    Die Browser-Fehlermeldungen sind harmlos!
echo    Sie betreffen nur Web-APIs, nicht die App.
echo.
pause
