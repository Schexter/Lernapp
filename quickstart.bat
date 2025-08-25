@echo off
REM ========================================
REM QUICK START - Lernapp + Import
REM Autor: Hans Hahn
REM ========================================

cls
echo ========================================
echo    LERNAPP QUICK START
echo ========================================
echo.

echo [1] Starte Spring Boot Server...
start cmd /k "gradlew bootRun"

echo.
echo Warte 15 Sekunden bis Server gestartet ist...
timeout /t 15 /nobreak

echo.
echo [2] Teste Server-Verbindung...
curl -s -o NUL -w "Server Status: %%{http_code}\n" http://localhost:8080/api/questions

if %errorlevel% neq 0 (
    echo.
    echo FEHLER: Server nicht erreichbar!
    echo Prüfe das andere CMD-Fenster für Fehler.
    pause
    exit /b 1
)

echo.
echo [3] Server läuft! Starte Import-Tool...
echo.
timeout /t 2

REM Starte Import-Menü
call test_import.bat

REM Erstellt von Hans Hahn - Alle Rechte vorbehalten