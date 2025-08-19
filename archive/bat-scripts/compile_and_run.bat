@echo off
echo =====================================================
echo   FACHINFORMATIKER LERNAPP - KOMPILIERUNG & START
echo =====================================================
echo.

cd /d C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

echo [1] Beende alle laufenden Java-Prozesse...
taskkill /F /IM java.exe 2>nul
echo.

echo [2] Loesche alte Build-Dateien...
call gradlew.bat clean
echo.

echo [3] Kompiliere alle Module...
echo.
echo === Kompiliere Core-Modul ===
call gradlew.bat :lernapp-core:compileJava

if errorlevel 1 (
    echo FEHLER beim Kompilieren von Core!
    pause
    exit /b 1
)

echo.
echo === Kompiliere Security-Modul ===
call gradlew.bat :lernapp-security:compileJava

if errorlevel 1 (
    echo FEHLER beim Kompilieren von Security!
    pause
    exit /b 1
)

echo.
echo === Kompiliere Web-Modul ===
call gradlew.bat :lernapp-web:compileJava

if errorlevel 1 (
    echo FEHLER beim Kompilieren von Web!
    pause
    exit /b 1
)

echo.
echo =====================================================
echo   KOMPILIERUNG ERFOLGREICH!
echo =====================================================
echo.

echo [4] Baue JAR-Dateien...
call gradlew.bat build -x test

echo.
echo [5] Starte die Anwendung...
echo.
call gradlew.bat :lernapp-web:bootRun

pause
