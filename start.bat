@echo off
echo ========================================
echo   Fachinformatiker Lernapp - Starter
echo ========================================
echo.
echo [INFO] Starte Spring Boot Application...
echo.

REM Prüfe ob Java installiert ist
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java ist nicht installiert oder nicht im PATH!
    echo         Bitte Java 17+ installieren.
    pause
    exit /b 1
)

REM Starte die Anwendung
echo [INFO] Kompiliere und starte App...
echo.
call gradlew.bat bootRun

REM Falls Gradle nicht funktioniert
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Gradle Build fehlgeschlagen!
    echo         Versuche: gradlew.bat clean build
    pause
    exit /b 1
)

pause