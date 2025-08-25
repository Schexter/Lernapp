@echo off
echo ========================================
echo PUSH ZU GITHUB - Einfach und Sicher
echo ========================================
echo.

REM Sicherstellen dass wir auf main sind
git checkout main 2>nul
if errorlevel 1 (
    echo FEHLER: Kann nicht auf main wechseln!
    echo Führe erst fix_git_and_push.bat aus!
    pause
    exit /b 1
)

echo Status der Änderungen:
git status --short
echo.

REM Alle Änderungen stagen (außer ignorierte)
set /p "COMMIT_MSG=Commit Nachricht eingeben: "
echo.

echo Füge Änderungen hinzu...
git add .
echo.

echo Committe mit Nachricht: %COMMIT_MSG%
git commit -m "%COMMIT_MSG%"
echo.

echo Synchronisiere mit Server...
git pull origin main --rebase
echo.

echo PUSHE zu GitHub...
git push origin main

if errorlevel 1 (
    echo.
    echo FEHLER beim Push!
    echo Mögliche Gründe:
    echo - Keine Berechtigung
    echo - Konflikte mit Server
    echo - Netzwerkproblem
) else (
    echo.
    echo ========================================
    echo ERFOLGREICH GEPUSHT!
    echo ========================================
    echo.
    echo Nächste Schritte:
    echo 1. SSH auf Server: ssh root@138.199.223.167
    echo 2. Führe aus: ./manual_update.sh
)

pause
