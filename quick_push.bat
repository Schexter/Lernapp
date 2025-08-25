@echo off
echo ===============================================
echo QUICK PUSH TO GITHUB
echo ===============================================
echo.
echo Bitte geben Sie Ihre GitHub-Credentials ein:
echo.
set /p username="GitHub Username: "
set /p token="Personal Access Token (NICHT Ihr Passwort!): "

echo.
echo Pushe zu GitHub...
git push https://%username%:%token%@github.com/Schexter/Lernapp.git main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Push erfolgreich!
    echo.
    echo Ihre Arbeit ist jetzt auf GitHub gesichert!
) else (
    echo.
    echo ❌ Push fehlgeschlagen. Bitte pruefen Sie Ihre Credentials.
    echo.
    echo Erstellen Sie einen Personal Access Token hier:
    echo https://github.com/settings/tokens
)

pause