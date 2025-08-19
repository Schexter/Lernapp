@echo off
echo ===============================================
echo GitHub SSH Setup
echo ===============================================
echo.

REM Prüfe ob SSH-Key existiert
if exist %USERPROFILE%\.ssh\id_rsa.pub (
    echo SSH-Key gefunden!
    echo.
    type %USERPROFILE%\.ssh\id_rsa.pub
    echo.
    echo ===============================================
    echo Kopieren Sie den obigen Key und fuegen Sie ihn
    echo in GitHub ein: https://github.com/settings/keys
    echo ===============================================
) else (
    echo Kein SSH-Key gefunden. Erstelle neuen Key...
    echo.
    ssh-keygen -t rsa -b 4096 -C "hans@example.com" -f %USERPROFILE%\.ssh\id_rsa -N ""
    echo.
    echo SSH-Key erstellt! Hier ist Ihr Public Key:
    echo.
    type %USERPROFILE%\.ssh\id_rsa.pub
    echo.
    echo ===============================================
    echo Kopieren Sie den obigen Key und fuegen Sie ihn
    echo in GitHub ein: https://github.com/settings/keys
    echo ===============================================
)

echo.
echo Nach dem Hinzufuegen des Keys zu GitHub, druecken Sie Enter...
pause

REM Ändere Remote URL zu SSH
git remote set-url origin git@github.com:Schexter/Lernapp.git

echo.
echo Remote URL geaendert zu SSH.
echo.

REM Teste SSH-Verbindung
echo Teste SSH-Verbindung zu GitHub...
ssh -T git@github.com

echo.
echo Versuche zu pushen...
git push origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Push erfolgreich via SSH!
) else (
    echo.
    echo ❌ Push fehlgeschlagen. Bitte ueberpruefen Sie Ihre SSH-Konfiguration.
)

pause