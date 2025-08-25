@echo off
echo ========================================
echo   FRONTEND SCHNELLSTART + NETZWERK
echo ========================================
echo.

cd "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java\frontend"

echo [INFO] Ihre IP-Adressen:
echo   🏠 Localhost: 127.0.0.1
echo   🌐 Netzwerk: 169.254.170.142 (Ethernet 3)
echo   🌐 Netzwerk: 169.254.113.86 (Ethernet 2)
echo.

echo [INFO] Beende alte Node-Prozesse...
for /f "tokens=5" %%p in ('netstat -ano 2^>nul ^| findstr :5173') do taskkill /PID %%p /F >nul 2>&1

echo [INFO] Starte React Frontend mit Netzwerk-Zugriff...
echo   Frontend wird verfügbar sein unter:
echo   🏠 http://localhost:5173
echo   🌐 http://169.254.170.142:5173
echo   🌐 http://169.254.113.86:5173
echo.

npm run dev

pause