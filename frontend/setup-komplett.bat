@echo off
REM Fachinformatiker Lernapp - Node.js Check & Frontend Setup
REM Erstellt von Hans Hahn - Alle Rechte vorbehalten

title Node.js Setup Check

echo ========================================
echo   NODE.JS INSTALLATION CHECK
echo ========================================
echo.

REM 1. NODE.JS PRÜFUNG
echo [1/5] Prüfe Node.js Installation...
node --version >nul 2>&1
if %errorlevel% equ 0 (
    echo   ✅ Node.js ist installiert:
    node --version
    npm --version
) else (
    echo   ❌ Node.js ist NICHT installiert!
    echo.
    echo   💡 LÖSUNG:
    echo      1. https://nodejs.org besuchen
    echo      2. LTS Version herunterladen
    echo      3. Installer ausführen (als Administrator)
    echo      4. Computer neu starten
    echo      5. Dieses Script erneut ausführen
    echo.
    echo   🔗 DIRECT DOWNLOAD:
    echo      https://nodejs.org/dist/v20.10.0/node-v20.10.0-x64.msi
    echo.
    pause
    exit /b 1
)
echo.

REM 2. FRONTEND ORDNER PRÜFUNG
echo [2/5] Prüfe Frontend-Ordner...
cd "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java\frontend"
if exist "package.json" (
    echo   ✅ package.json gefunden
) else (
    echo   ❌ package.json nicht gefunden!
    echo      Frontend-Projekt fehlt oder ist beschädigt
    pause
    exit /b 1
)
echo.

REM 3. DEPENDENCIES INSTALLATION
echo [3/5] Installiere/Überprüfe Dependencies...
if exist "node_modules" (
    echo   ✅ node_modules vorhanden - prüfe Installation...
) else (
    echo   ⏳ node_modules fehlt - installiere Dependencies...
)

npm install
if %errorlevel% equ 0 (
    echo   ✅ Dependencies erfolgreich installiert!
) else (
    echo   ❌ npm install fehlgeschlagen!
    echo      Netzwerk-/Proxy-Problem oder beschädigte package.json
    pause
    exit /b 1
)
echo.

REM 4. FIREWALL-REGEL
echo [4/5] Erstelle Firewall-Regel für Port 5173...
netsh advfirewall firewall delete rule name="Lernapp Frontend 5173" >nul 2>&1
netsh advfirewall firewall add rule name="Lernapp Frontend 5173" dir=in action=allow protocol=TCP localport=5173 >nul 2>&1
if %errorlevel% equ 0 (
    echo   ✅ Firewall-Regel erfolgreich erstellt!
) else (
    echo   ⚠️  Firewall-Regel fehlgeschlagen
    echo      LÖSUNG: Script als Administrator ausführen
    echo      ODER manuell in Windows Defender Firewall hinzufügen
)
echo.

REM 5. FRONTEND START
echo [5/5] Starte Frontend für Netzwerk-Zugriff...
echo.
echo   🎯 FRONTEND WIRD VERFÜGBAR SEIN UNTER:
echo      🏠 Lokal:     http://localhost:5173
echo      🌐 Netzwerk:  http://10.42.1.117:5173
echo.
echo   📱 MOBILE ZUGRIFF:
echo      Smartphone/Tablet im gleichen WLAN:
echo      http://10.42.1.117:5173
echo.
echo   ⏳ Frontend startet jetzt...
echo      (Das kann 10-30 Sekunden dauern)
echo.

npm run dev

if %errorlevel% neq 0 (
    echo.
    echo ❌ Frontend-Start fehlgeschlagen!
    echo    Mögliche Ursachen:
    echo    - Dependencies nicht vollständig installiert
    echo    - Port 5173 bereits belegt
    echo    - Vite-Konfiguration fehlerhaft
    echo.
    echo 🔧 DEBUGGING:
    echo    npm run build  (Test ob Build funktioniert)
    echo    npx vite       (Direkt Vite starten)
    echo.
    pause
)

echo.
echo ========================================
echo   SETUP ABGESCHLOSSEN
echo ========================================
echo.
echo Testen Sie jetzt:
echo   🏠 http://localhost:5173
echo   🌐 http://10.42.1.117:5173
echo.
pause