@echo off
echo ========================================
echo   LERNAPP START - PORT 8081
echo ========================================
echo.

cd "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java"

echo [INFO] Stoppe alle Java-Prozesse auf Port 8080...
for /f "tokens=5" %%p in ('netstat -ano ^| findstr :8080') do taskkill /PID %%p /F 2>nul

echo [INFO] Starte App auf Port 8081...
echo [WARTEZEIT] ~30 Sekunden bis "Started LernappApplication"
echo.

gradlew.bat clean bootRun

pause