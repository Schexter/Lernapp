@echo off
REM Fachinformatiker Lernapp - Frontend für 10.42.1.117:5173
REM Erstellt von Hans Hahn - Alle Rechte vorbehalten

title Frontend Netzwerk 10.42.1.117:5173

echo ========================================
echo   FRONTEND NETZWERK-SETUP
echo   IP: 10.42.1.117:5173
echo ========================================
echo.

cd "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java\frontend"

REM 1. PORT FREIGEBEN
echo [1/4] Beende alte Prozesse auf Port 5173...
for /f "tokens=5" %%p in ('netstat -ano 2^>nul ^| findstr :5173') do (
    echo   Beende Prozess %%p...
    taskkill /PID %%p /F >nul 2>&1
)
echo   ✅ Port 5173 ist frei!
echo.

REM 2. FIREWALL-REGEL FÜR PORT 5173
echo [2/4] Erstelle Firewall-Regel für Port 5173...
netsh advfirewall firewall delete rule name="Lernapp Frontend Port 5173" >nul 2>&1
netsh advfirewall firewall add rule name="Lernapp Frontend Port 5173" dir=in action=allow protocol=TCP localport=5173 >nul 2>&1
if %errorlevel% equ 0 (
    echo   ✅ Firewall-Regel erfolgreich erstellt!
) else (
    echo   ⚠️  Firewall-Regel fehlgeschlagen - als Administrator starten!
)
echo.

REM 3. VITE-KONFIGURATION PRÜFEN
echo [3/4] Prüfe Vite-Konfiguration...
findstr "host: '0.0.0.0'" vite.config.ts >nul
if %errorlevel% equ 0 (
    echo   ✅ Vite ist für Netzwerk-Zugriff konfiguriert
) else (
    echo   ❌ Vite-Konfiguration muss angepasst werden!
)
echo.

REM 4. FRONTEND STARTEN
echo [4/4] Starte Frontend für Netzwerk-Zugriff...
echo.
echo   🎯 FRONTEND WIRD ERREICHBAR SEIN UNTER:
echo      🏠 Lokal:     http://localhost:5173
echo      🌐 Netzwerk:  http://10.42.1.117:5173
echo.
echo   📱 FÜR MOBILE GERÄTE:
echo      Gleichen WLAN verwenden und http://10.42.1.117:5173 öffnen
echo.
echo   ⏳ Frontend startet jetzt...
echo.

npm run dev

echo.
echo ========================================
echo   TEST IHRE VERBINDUNG
echo ========================================
echo.
echo Öffnen Sie in einem Browser:
echo   🏠 http://localhost:5173
echo   🌐 http://10.42.1.117:5173
echo.
echo Falls es nicht funktioniert:
echo   1. Windows Defender Firewall prüfen
echo   2. Antivirus-Software prüfen  
echo   3. Router-Einstellungen prüfen
echo   4. Als Administrator starten
echo.
pause