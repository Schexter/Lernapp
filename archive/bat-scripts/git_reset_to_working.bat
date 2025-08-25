@echo off
cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java
echo.
echo === Git Status vor Reset ===
git status
echo.
echo === Sichere aktuelle Änderungen ===
git add -A
git stash save "Backup vor Reset - Tag Problem"
echo.
echo === Git Log - Letzte Commits ===
git log --oneline -10
echo.
echo === Reset auf funktionierenden Stand ===
echo Wähle den Commit zum Zurücksetzen:
echo 1) 4e21b60 - Phase 1.3 abgeschlossen. App startet
echo 2) 9b2026b - Phase 1.1 abgeschlossen. App startet
echo 3) Abbrechen
echo.
set /p choice="Deine Wahl (1-3): "
if "%choice%"=="1" (
    git reset --hard 4e21b60
    echo Reset auf Phase 1.3 durchgeführt!
) else if "%choice%"=="2" (
    git reset --hard 9b2026b
    echo Reset auf Phase 1.1 durchgeführt!
) else (
    echo Abgebrochen.
)
pause
