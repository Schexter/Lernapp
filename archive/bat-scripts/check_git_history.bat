@echo off
cd /d C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java
echo === Git Log (letzte 5 Commits) ===
git log --oneline -5
echo.
echo === Geloeschte Dateien ===
git ls-files --deleted
echo.
echo === Modifizierte Dateien ===
git diff --name-only
pause
