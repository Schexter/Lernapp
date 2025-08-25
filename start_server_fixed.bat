@echo off
echo ========================================
echo    FACHINFORMATIKER LERNAPP SERVER
echo    Security Fix Applied
echo ========================================
echo.

REM Lösche alte Session/Security-Caches
echo Cleaning cache...
if exist .\.gradle\cache rd /s /q .\.gradle\cache
if exist .\build\tmp rd /s /q .\build\tmp

REM Stoppe alle laufenden Java-Prozesse (optional)
echo Stopping any running instances...
taskkill /F /IM java.exe 2>nul

REM Warte kurz
timeout /t 2 /nobreak >nul

REM Starte Server mit expliziten Parametern
echo Starting server on port 8080...
echo.
echo Server wird gestartet mit:
echo - JWT-basierte Authentifizierung
echo - Keine Mock-User mehr
echo - Korrekte Security-Konfiguration
echo.
echo Login mit echten Registrierungen unter:
echo http://localhost:8080/api/auth/register
echo.

gradlew.bat bootRun --args="--server.port=8080 --spring.profiles.active=default"

pause