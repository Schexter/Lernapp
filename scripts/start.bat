@echo off
echo ====================================
echo   Fachinformatiker Lernapp - Start
echo ====================================
echo.

REM Projekt kompilieren
echo [1/2] Kompiliere Projekt...
call gradlew clean build -x test

REM Anwendung starten
echo.
echo [2/2] Starte Spring Boot Anwendung...
call gradlew bootRun

pause
