@echo off
echo ========================================
echo   LERNAPP - PERSISTENTE DATENBANK
echo ========================================
echo.

cd "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java"

echo [INFO] Stoppe alte Java-Prozesse...
for /f "tokens=5" %%p in ('netstat -ano ^| findstr :8080') do taskkill /PID %%p /F 2>nul

echo [INFO] Erstelle Datenbank-Verzeichnis...
if not exist "data" mkdir data

echo [INFO] Starte App mit persistenter Datenbank...
echo [WICHTIG] Ihre Accounts bleiben jetzt erhalten!
echo.

gradlew.bat clean bootRun

echo.
echo ========================================
echo   DATENBANKDATEIEN:
echo ========================================
if exist "data\lernappdb.mv.db" (
    echo ✅ lernappdb.mv.db gefunden - Daten sind persistent!
    dir data\*.db /b
) else (
    echo ❌ Keine Datenbankdatei gefunden
)
echo.
pause