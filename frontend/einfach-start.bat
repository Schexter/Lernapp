@echo off
echo ========================================
echo   SUPER EINFACHER FRONTEND START
echo ========================================
echo.

cd "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java\frontend"

echo [1] Teste Node.js...
node --version
if %errorlevel% neq 0 (
    echo ❌ Node.js fehlt! Installiere von: https://nodejs.org
    pause
    exit
)

echo [2] Installiere Dependencies...
npm install

echo [3] Starte Frontend...
echo   Localhost: http://localhost:5173
echo.

npm run dev

pause