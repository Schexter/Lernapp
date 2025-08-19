@echo off
echo ===========================================
echo  Security Fix - Clean Restart
echo ===========================================

cd /d "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java"

echo [1] Stoppe laufende Java-Prozesse...
taskkill /f /im java.exe >nul 2>&1

echo [2] Clean Build...
call gradlew.bat clean --no-daemon --quiet

echo [3] Kompiliere mit Security-Fixes...
call gradlew.bat :lernapp-security:compileJava --no-daemon --quiet
call gradlew.bat :lernapp-web:compileJava --no-daemon --quiet

echo [4] Starte Anwendung (DEV-Profile)...
echo.
echo =========================================
echo  Anwendung läuft auf: http://localhost:8080
echo  Profile: dev (DevSecurityConfig aktiv)
echo  API Test: http://localhost:8080/api/questions
echo =========================================
echo.

start /min cmd /c "gradlew.bat :lernapp-web:bootRun --no-daemon"

timeout /t 10 /nobreak >nul

echo [5] Teste API-Endpunkt...
curl -s http://localhost:8080/api/questions > test_result.txt
if exist test_result.txt (
    echo API-Test erfolgreich!
    type test_result.txt
    del test_result.txt
) else (
    echo API-Test fehlgeschlagen - prüfe Logs
)

pause
