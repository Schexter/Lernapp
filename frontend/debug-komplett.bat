@echo off
REM Kompletter Netzwerk-Debug für Frontend
REM Erstellt von Hans Hahn - Alle Rechte vorbehalten

title Frontend Netzwerk Debug

echo ========================================
echo   KOMPLETTER NETZWERK-DEBUG
echo ========================================
echo.

cd "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java\frontend"

REM 1. NODE.JS STATUS
echo [1/8] Node.js Status...
node --version 2>nul
if %errorlevel% equ 0 (
    echo   ✅ Node.js: 
    node --version
    npm --version
) else (
    echo   ❌ Node.js nicht gefunden!
    goto :error
)
echo.

REM 2. AKTUELLE PROZESSE
echo [2/8] Aktuelle Node-Prozesse...
tasklist | findstr node.exe
echo.

REM 3. PORT-STATUS
echo [3/8] Port 5173 Status...
netstat -ano | findstr :5173
if %errorlevel% neq 0 (
    echo   ❌ Port 5173 ist NICHT belegt - Frontend läuft nicht!
)
echo.

REM 4. FIREWALL-REGELN
echo [4/8] Firewall-Regeln...
powershell -Command "Get-NetFirewallRule -DisplayName '*5173*' | Select-Object DisplayName, Enabled, Direction, Action"
echo.

REM 5. NETZWERK-PROFIL
echo [5/8] Netzwerk-Profil...
powershell -Command "Get-NetConnectionProfile | Select-Object Name, NetworkCategory"
echo.

REM 6. IP-ADRESSE
echo [6/8] Aktuelle IP-Adresse...
powershell -Command "Get-NetIPAddress -AddressFamily IPv4 | Where-Object {$_.IPAddress -like '10.42.1.*'} | Select-Object IPAddress, InterfaceAlias"
echo.

REM 7. FRONTEND DEPENDENCIES
echo [7/8] Frontend Dependencies...
if exist "node_modules" (
    echo   ✅ node_modules vorhanden
) else (
    echo   ❌ node_modules fehlt - npm install erforderlich!
    npm install
)
echo.

REM 8. VITE CONFIG
echo [8/8] Vite Konfiguration...
if exist "vite.config.ts" (
    echo   ✅ vite.config.ts gefunden
    type vite.config.ts | findstr "host"
) else (
    echo   ❌ vite.config.ts nicht gefunden!
)
echo.

echo ========================================
echo   FRONTEND NEUSTART
echo ========================================
echo.

REM Alte Node-Prozesse beenden
echo [CLEANUP] Beende alte Node-Prozesse...
taskkill /f /im node.exe >nul 2>&1
timeout /t 2 /nobreak >nul

REM Dependencies prüfen
echo [DEPS] Prüfe Dependencies...
npm list --depth=0 2>nul | findstr vite
if %errorlevel% neq 0 (
    echo   Installing dependencies...
    npm install
)

REM Frontend starten
echo [START] Starte Frontend...
echo.
echo   🎯 Frontend wird verfügbar sein unter:
echo      🏠 Lokal:     http://localhost:5173
for /f "tokens=1" %%i in ('powershell -Command "Get-NetIPAddress -AddressFamily IPv4 | Where-Object {$_.IPAddress -like '10.42.1.*'} | Select-Object -ExpandProperty IPAddress"') do (
    echo      🌐 Netzwerk:  http://%%i:5173
    set "NETWORK_IP=%%i"
)
echo.
echo   📱 MOBILE TEST:
echo      Smartphone/Tablet im gleichen WLAN verwenden!
echo.

start "Frontend Debug" cmd /k "npm run dev"

echo [WAIT] Warte 15 Sekunden auf Frontend-Start...
timeout /t 15 /nobreak >nul

echo.
echo ========================================
echo   VERBINDUNGSTESTS
echo ========================================
echo.

REM Localhost Test
echo [TEST 1] Localhost Test...
curl -s -I http://localhost:5173 2>nul | findstr "HTTP"
if %errorlevel% equ 0 (
    echo   ✅ Localhost funktioniert!
) else (
    echo   ❌ Localhost nicht erreichbar - Frontend-Problem!
)

REM Netzwerk Test
echo [TEST 2] Netzwerk Test...
for /f "tokens=1" %%i in ('powershell -Command "Get-NetIPAddress -AddressFamily IPv4 | Where-Object {$_.IPAddress -like '10.42.1.*'} | Select-Object -ExpandProperty IPAddress"') do (
    echo   Testing: http://%%i:5173
    curl -s -I http://%%i:5173 2>nul | findstr "HTTP"
    if !errorlevel! equ 0 (
        echo   ✅ Netzwerk-IP %%i funktioniert!
    ) else (
        echo   ❌ Netzwerk-IP %%i nicht erreichbar!
    )
)

REM Port Check
echo [TEST 3] Port-Binding Check...
netstat -ano | findstr :5173
if %errorlevel% equ 0 (
    echo   ✅ Port 5173 ist aktiv!
) else (
    echo   ❌ Port 5173 nicht aktiv - Frontend gestartet?
)

echo.
echo ========================================
echo   EXTERNE GERÄTE TESTS
echo ========================================
echo.

echo 📱 VON ANDEREM GERÄT TESTEN:
for /f "tokens=1" %%i in ('powershell -Command "Get-NetIPAddress -AddressFamily IPv4 | Where-Object {$_.IPAddress -like '10.42.1.*'} | Select-Object -ExpandProperty IPAddress"') do (
    echo   URL: http://%%i:5173
    echo   Terminal-Test: curl -I http://%%i:5173
    echo   Browser-Test: http://%%i:5173
)
echo.

echo 🔧 FALLS IMMER NOCH PROBLEME:
echo   1. Router-Firewall kann Geräte isolieren
echo   2. Schulungs-Netzwerk kann Client-Isolation haben
echo   3. Antivirus-Software kann blocken
echo   4. Andere Firewall-Software aktiv
echo.

echo ⚠️  CLIENT ISOLATION CHECK:
echo   Manche Netzwerke isolieren Geräte voneinander!
echo   Test: Können andere Geräte sich gegenseitig anpingen?
echo.

pause

:error
echo.
echo ❌ KRITISCHER FEHLER beim Setup!
pause