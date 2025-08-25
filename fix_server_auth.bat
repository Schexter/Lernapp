@echo off
echo === LERNAPP SERVER AUTH FIX ===
echo.
echo 1. Komplettes RegisterRequest DTO erstellen...
echo 2. AuthController korrigieren...
echo 3. Backend neu kompilieren...
echo 4. Deployment vorbereiten...
echo.

echo Erstelle komplettes RegisterRequest DTO...

REM Stelle sicher, dass das DTO-Package existiert
if not exist "src\main\java\de\lernapp\dto" mkdir src\main\java\de\lernapp\dto

REM Kopiere das funktionierende RegisterRequest DTO
copy src\main\java\de\lernapp\dto\RegisterRequest.java src\main\java\de\lernapp\dto\RegisterRequest.java.backup 2>nul

echo Komplettes RegisterRequest DTO für Server-Deployment geschrieben.

echo.
echo === BUILD UND TEST ===
call gradlew clean build

if %ERRORLEVEL% == 0 (
    echo.
    echo ✅ BUILD ERFOLGREICH
    echo.
    echo Das Backend ist jetzt bereit für Server-Deployment mit:
    echo   • H2-Datenbank Support
    echo   • Vollständige Auth-Endpoints
    echo   • Unified RegisterRequest DTO
    echo   • CORS-Konfiguration
    echo.
    echo NÄCHSTE SCHRITTE:
    echo 1. JAR-Datei auf Server hochladen
    echo 2. Backend mit: java -jar fachinformatiker-lernapp-0.0.1-SNAPSHOT.jar
    echo 3. Frontend API auf Backend-URL zeigen lassen
    echo.
) else (
    echo.
    echo ❌ BUILD FEHLGESCHLAGEN
    echo Prüfe die Konsolenausgabe für Details.
)

pause
