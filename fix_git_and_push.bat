@echo off
echo ========================================
echo GIT FIX und PUSH Script
echo ========================================
echo.

REM Aktuellen Status anzeigen
echo Aktueller Git Status:
git status --short
echo.

REM Auf main Branch wechseln
echo Wechsle auf main Branch...
git checkout main
if errorlevel 1 (
    echo Main Branch existiert nicht, erstelle ihn...
    git checkout -b main
)
echo.

REM Änderungen vom detached HEAD holen
echo Hole Änderungen...
git cherry-pick 8aed31c
echo.

REM Mit Remote synchronisieren
echo Synchronisiere mit GitHub...
git fetch origin
git pull origin main --rebase
echo.

REM Status prüfen
echo Finaler Status:
git status
echo.

echo ========================================
echo Bereit zum Push!
echo Führe aus: git push origin main
echo ========================================
pause
