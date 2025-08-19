@echo off
echo =====================================================
echo   FACHINFORMATIKER LERNAPP - DIREKTE KOMPILIERUNG
echo =====================================================
echo.

cd /d C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

echo [1] Stoppe alle Java-Prozesse...
taskkill /F /IM java.exe 2>nul

echo.
echo [2] Loesche alte Build-Dateien...
rmdir /s /q build 2>nul
rmdir /s /q lernapp-core\build 2>nul
rmdir /s /q lernapp-security\build 2>nul
rmdir /s /q lernapp-web\build 2>nul

echo.
echo [3] Kompiliere nur die Module (nicht src)...
echo.

REM Kompiliere nur die Module
call gradlew.bat :lernapp-core:compileJava :lernapp-security:compileJava :lernapp-web:compileJava

if errorlevel 1 (
    echo.
    echo === KOMPILIERUNG FEHLGESCHLAGEN ===
    echo Versuche Build ohne Tests...
    call gradlew.bat :lernapp-core:build :lernapp-security:build :lernapp-web:build -x test
)

echo.
echo [4] Starte die Web-Anwendung...
call gradlew.bat :lernapp-web:bootRun

pause
