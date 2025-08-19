@echo off
echo =====================================================
echo   Fachinformatiker Lernapp - Quick Start
echo =====================================================
echo.

cd /d C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

echo [1] Stoppe alle Java-Prozesse...
taskkill /F /IM java.exe 2>nul

echo.
echo [2] Kompiliere und starte die Anwendung...
call gradlew.bat clean :lernapp-web:bootRun

pause
