@echo off
echo ========================================
echo   Fachinformatiker Lernapp - Clean Start
echo   Minimale funktionierende Version
echo ========================================
echo.

echo [1] Cleaning old builds...
if exist build rmdir /s /q build
if exist .gradle rmdir /s /q .gradle
call gradlew clean

echo.
echo [2] Building application...
call gradlew build -x test

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build fehlgeschlagen! Pruefe die Fehler oben.
    pause
    exit /b 1
)

echo.
echo [3] Starting application...
echo.
echo ====================================
echo App wird gestartet auf:
echo - Startseite: http://localhost:8080
echo - API: http://localhost:8080/api/questions  
echo - H2 Console: http://localhost:8080/h2-console
echo   (User: sa, Password: leer)
echo ====================================
echo.

call gradlew bootRun

pause
