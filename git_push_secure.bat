@echo off
echo ===============================================
echo Sichere Git Push Methode
echo ===============================================
echo.
echo Diese Methode nutzt den Windows Credential Manager
echo.

REM Lösche alte Credentials (falls vorhanden)
echo Loesche alte GitHub-Credentials...
cmdkey /delete:git:https://github.com

REM Konfiguriere Git Credential Manager
git config --global credential.helper manager

echo.
echo Beim naechsten Push werden Sie nach Ihrem GitHub-Benutzernamen
echo und Personal Access Token gefragt.
echo.
echo WICHTIG: Verwenden Sie NICHT Ihr GitHub-Passwort!
echo Verwenden Sie einen Personal Access Token von:
echo https://github.com/settings/tokens
echo.

REM Versuche zu pushen (wird nach Credentials fragen)
echo Starte Push (Sie werden nach Credentials gefragt)...
echo.
git push origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Push erfolgreich! Ihre Credentials wurden gespeichert.
) else (
    echo.
    echo ❌ Push fehlgeschlagen. Bitte ueberpruefen Sie Ihre Credentials.
)

pause