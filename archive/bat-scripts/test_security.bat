@echo off
echo Testing Security Configuration...
cd /d "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java"

echo.
echo [1] Kompiliere nur die Änderungen...
gradlew.bat :lernapp-security:classes --no-daemon --quiet
if %ERRORLEVEL% NEQ 0 (
    echo FEHLER: Kompilierung fehlgeschlagen
    pause
    exit /b 1
)

gradlew.bat :lernapp-web:classes --no-daemon --quiet
if %ERRORLEVEL% NEQ 0 (
    echo FEHLER: Web-Kompilierung fehlgeschlagen
    pause
    exit /b 1
)

echo [OK] Kompilierung erfolgreich!
echo.

echo [2] Starte Anwendung...
echo Öffne Browser: http://localhost:8080
echo.

gradlew.bat :lernapp-web:bootRun --no-daemon --quiet
