@echo off
REM Quick Test Script für Fachinformatiker Lernapp (Windows)
REM Erstellt von Hans Hahn - Alle Rechte vorbehalten

echo ============================================
echo  Fachinformatiker Lernapp - Quick Test
echo ============================================
echo.

cd /d "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java"

echo [1] Build Projekt
echo ------------------------
call gradlew.bat clean build --no-daemon
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Build fehlgeschlagen!
    pause
    exit /b 1
)
echo [OK] Build erfolgreich!
echo.

echo [2] Tests ausfuehren
echo ------------------------
call gradlew.bat test --no-daemon
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [WARNUNG] Einige Tests fehlgeschlagen
)
echo.

echo [3] Anwendung starten
echo ------------------------
echo Die Anwendung wird gestartet auf http://localhost:8080
echo H2 Console verfuegbar unter: http://localhost:8080/h2-console
echo.
echo Druecke Ctrl+C zum Beenden
echo.

call gradlew.bat :lernapp-web:bootRun --no-daemon
