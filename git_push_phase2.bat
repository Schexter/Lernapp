@echo off
echo ===============================================
echo GIT PUSH - Phase 2.0
echo ===============================================
echo.
echo Bitte geben Sie Ihre GitHub-Credentials ein:
echo.
set /p username="GitHub Username: "
set /p token="Personal Access Token: "

echo.
echo Pushe zu GitHub...
git push https://%username%:%token%@github.com/Schexter/Lernapp.git main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Push erfolgreich!
    echo.
    echo Phase 2.0 ist jetzt auf GitHub gesichert:
    echo - Backend Authentication mit JWT
    echo - 171 AP1 Fragen
    echo - React Frontend mit TailwindCSS
) else (
    echo.
    echo ❌ Push fehlgeschlagen.
    echo.
    echo Alternativ können Sie versuchen:
    echo git push origin main
    echo.
    echo Oder erstellen Sie einen Token hier:
    echo https://github.com/settings/tokens
)

pause