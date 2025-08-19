@echo off
echo =====================================================
echo   Fachinformatiker Lernapp - Start
echo =====================================================
echo.

cd /d C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

echo [1] Stoppe alle Java-Prozesse...
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul

echo.
echo [2] Loesche temporaere Dateien...
if exist .gradle\buildOutputCleanup rmdir /s /q .gradle\buildOutputCleanup 2>nul

echo.
echo [3] Starte die Anwendung...
echo.
call gradlew.bat --no-daemon :lernapp-web:bootRun

if errorlevel 1 (
    echo.
    echo === FEHLER BEIM START ===
    echo Pruefe error.log fuer Details
    pause
) else (
    echo.
    echo === APP LAEUFT AUF http://localhost:8080 ===
)
