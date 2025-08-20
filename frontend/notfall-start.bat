@echo off
REM NOTFALL-FRONTEND-START
REM Erstellt von Hans Hahn - Alle Rechte vorbehalten

title NOTFALL - Frontend Reparatur

echo ========================================
echo   🚨 NOTFALL-FRONTEND-REPARATUR
echo ========================================
echo.

cd "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java\frontend"

echo [SCHRITT 1] Alle Node-Prozesse beenden...
taskkill /f /im node.exe >nul 2>&1
taskkill /f /im npm.exe >nul 2>&1
timeout /t 2 /nobreak >nul
echo   ✅ Prozesse beendet

echo.
echo [SCHRITT 2] Node.js PATH prüfen...
where node >nul 2>&1
if %errorlevel% equ 0 (
    echo   ✅ Node.js gefunden:
    node --version
) else (
    echo   ❌ Node.js nicht im PATH!
    echo   🔧 Versuche manuellen PATH...
    set "PATH=%PATH%;C:\Program Files\nodejs"
    set "PATH=%PATH%;C:\Program Files (x86)\nodejs"
    set "PATH=%PATH%;%LOCALAPPDATA%\Programs\nodejs"
    
    where node >nul 2>&1
    if !errorlevel! equ 0 (
        echo   ✅ Node.js nach PATH-Fix gefunden!
    ) else (
        echo   ❌ Node.js immer noch nicht gefunden!
        echo   💡 Lösung: Node.js neu installieren von https://nodejs.org
        pause
        exit /b 1
    )
)

echo.
echo [SCHRITT 3] Dependencies prüfen...
if exist "package.json" (
    echo   ✅ package.json vorhanden
) else (
    echo   ❌ package.json fehlt - falscher Ordner!
    pause
    exit /b 1
)

if exist "node_modules" (
    echo   ✅ node_modules vorhanden
) else (
    echo   ⏳ node_modules fehlt - installiere...
    npm install
)

echo.
echo [SCHRITT 4] Vite Config prüfen...
if exist "vite.config.ts" (
    echo   ✅ vite.config.ts vorhanden
    findstr "host" vite.config.ts
) else (
    echo   ❌ vite.config.ts fehlt!
)

echo.
echo [SCHRITT 5] Frontend starten...
echo   🎯 Localhost sollte wieder funktionieren: http://localhost:5173
echo   📱 Netzwerk: http://10.42.1.177:5173
echo.

npm run dev

if %errorlevel% neq 0 (
    echo.
    echo ❌ Frontend-Start fehlgeschlagen!
    echo.
    echo 🔧 ALTERNATIVE STARTMETHODEN:
    echo.
    echo [ALT 1] Direkt mit npx:
    echo   npx vite
    echo.
    echo [ALT 2] Manual mit Node:
    echo   node node_modules\vite\bin\vite.js
    echo.
    echo [ALT 3] Global Vite installieren:
    echo   npm install -g vite
    echo   vite
    echo.
    
    echo Versuche Alternative 1...
    npx vite
    
    if !errorlevel! neq 0 (
        echo Versuche Alternative 2...
        node node_modules\vite\bin\vite.js
    )
)

pause