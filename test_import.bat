@echo off
REM ========================================
REM CSV Import Test Script
REM Autor: Hans Hahn
REM ========================================

cls
echo ========================================
echo    LERNAPP CSV IMPORT TEST
echo ========================================
echo.

REM Prüfe ob Server läuft
echo Teste Server-Verbindung...
curl -s -o NUL -w "Server Status: %%{http_code}\n" http://localhost:8080/api/questions
echo.

:menu
echo Wähle eine Option:
echo.
echo [1] Import ALLE AP1 Fragen (600 Fragen)
echo [2] Import einzelne Kategorie
echo [3] Zeige Import-Statistiken
echo [4] Lösche alle Fragen (VORSICHT!)
echo [5] Test API - Hole 10 zufällige Fragen
echo [Q] Beenden
echo.

set /p choice="Deine Wahl: "

if /i "%choice%"=="1" goto import_all
if /i "%choice%"=="2" goto import_single
if /i "%choice%"=="3" goto show_stats
if /i "%choice%"=="4" goto delete_all
if /i "%choice%"=="5" goto test_api
if /i "%choice%"=="q" goto end

echo Ungültige Eingabe!
pause
cls
goto menu

:import_all
echo.
echo ========================================
echo    IMPORTIERE ALLE AP1 FRAGEN
echo ========================================
echo.
echo Starte Import von 600 Fragen...
echo Dies kann einige Minuten dauern...
echo.

curl -X POST http://localhost:8080/api/import/ap1/all

echo.
echo ========================================
echo    Import abgeschlossen!
echo ========================================
pause
cls
goto menu

:import_single
echo.
echo Verfügbare Kategorien:
echo [1] Geschäftsprozesse
echo [2] IT-Systeme
echo [3] Vernetzte Systeme
echo [4] Datenbanken
echo [5] Datenschutz und Sicherheit
echo [6] Wirtschafts- und Sozialkunde
echo.
set /p cat="Wähle Kategorie (1-6): "

if "%cat%"=="1" set file=ap1_questions/ap1_geschaeftsprozesse.csv
if "%cat%"=="2" set file=ap1_questions/ap1_it_systeme.csv
if "%cat%"=="3" set file=ap1_questions/ap1_vernetzte_systeme.csv
if "%cat%"=="4" set file=ap1_questions/ap1_datenbanken.csv
if "%cat%"=="5" set file=ap1_questions/ap1_datenschutz_sicherheit.csv
if "%cat%"=="6" set file=ap1_questions/ap1_wirtschaft_sozialkunde.csv

echo.
echo Importiere %file%...
curl -X POST "http://localhost:8080/api/import/local?path=%file%"
echo.
pause
cls
goto menu

:show_stats
echo.
echo ========================================
echo    IMPORT STATISTIKEN
echo ========================================
echo.

curl -X GET http://localhost:8080/api/import/statistics

echo.
echo.
pause
cls
goto menu

:delete_all
echo.
echo ========================================
echo    WARNUNG: ALLE FRAGEN LÖSCHEN
echo ========================================
echo.
echo Bist du SICHER dass du ALLE Fragen löschen willst?
echo.
set /p confirm="Tippe 'JA' zum Bestätigen: "

if /i "%confirm%"=="JA" (
    echo.
    echo Lösche alle Fragen...
    curl -X DELETE "http://localhost:8080/api/import/questions/all?confirm=true"
    echo.
    echo Alle Fragen wurden gelöscht!
) else (
    echo.
    echo Löschvorgang abgebrochen.
)

pause
cls
goto menu

:test_api
echo.
echo ========================================
echo    TESTE API - 10 ZUFÄLLIGE FRAGEN
echo ========================================
echo.

curl -X GET http://localhost:8080/api/questions?limit=10

echo.
echo.
pause
cls
goto menu

:end
echo.
echo Auf Wiedersehen!
timeout /t 2 >nul
exit

REM Erstellt von Hans Hahn - Alle Rechte vorbehalten