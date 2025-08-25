@echo off
echo ========================================
echo   LERNAPP SCHNELL-START TEST
echo ========================================
echo.

cd "C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java"

echo [INFO] Prüfe Java Version...
java -version
echo.

echo [INFO] Bereinige Projekt...
call gradlew.bat clean --quiet
echo.

echo [INFO] Starte App (das kann 1-2 Minuten dauern)...
echo [HINWEIS] App läuft wenn "Started LernappApplication" erscheint
echo.
call gradlew.bat bootRun