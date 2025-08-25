@echo off
echo ===============================================
echo GitHub Personal Access Token (PAT) Setup
echo ===============================================
echo.
echo Sie muessen einen Personal Access Token von GitHub erstellen:
echo.
echo 1. Gehen Sie zu: https://github.com/settings/tokens
echo 2. Klicken Sie auf "Generate new token (classic)"
echo 3. Geben Sie dem Token einen Namen (z.B. "Lernapp-Push")
echo 4. Waehlen Sie mindestens diese Berechtigungen:
echo    - repo (alle Unteroptionen)
echo 5. Klicken Sie auf "Generate token"
echo 6. WICHTIG: Kopieren Sie den Token SOFORT (er wird nur einmal angezeigt!)
echo.
echo ===============================================
set /p token="Fuegen Sie Ihren GitHub Personal Access Token hier ein: "
echo.

REM Konfiguriere Git Credential Manager
git config --global credential.helper manager-core

REM Setze die Remote URL mit Token
git remote set-url origin https://%token%@github.com/Schexter/Lernapp.git

echo.
echo ✅ GitHub-Authentifizierung konfiguriert!
echo.
echo Teste die Verbindung...
git push --dry-run origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Verbindung erfolgreich! Sie koennen jetzt pushen.
    echo.
    echo Moechten Sie jetzt pushen? (J/N)
    set /p push_now=""
    if /i "%push_now%"=="J" (
        git push origin main
        echo ✅ Push erfolgreich!
    )
) else (
    echo.
    echo ❌ Verbindung fehlgeschlagen. Bitte ueberpruefen Sie Ihren Token.
)

pause