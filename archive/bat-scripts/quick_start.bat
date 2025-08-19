@echo off
echo ========================================
echo   Fachinformatiker Lernapp - Quick Start
echo   Minimale Version
echo ========================================
echo.

echo [1] Cleaning...
call gradlew clean

echo.
echo [2] Compiling...
call gradlew compileJava

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo FEHLER beim Kompilieren!
    echo Versuche trotzdem zu starten...
)

echo.
echo [3] Starting...
echo.
echo App wird gestartet auf: http://localhost:8080
echo H2 Console: http://localhost:8080/h2-console
echo API: http://localhost:8080/api/questions
echo.

call gradlew bootRun

pause
