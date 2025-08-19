@echo off
echo =====================================================
echo   LERNAPP - MINIMAL KOMPILIERUNG
echo =====================================================
echo.

cd /d C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

echo [1] Beende Java-Prozesse...
taskkill /F /IM java.exe 2>nul

echo.
echo [2] Clean Build...
call gradlew.bat clean

echo.
echo [3] Kompiliere nur Core...
call gradlew.bat :lernapp-core:compileJava

if errorlevel 1 (
    echo Core Kompilierung fehlgeschlagen!
    pause
    exit /b 1
)

echo.
echo [4] Kompiliere Security...
call gradlew.bat :lernapp-security:compileJava

if errorlevel 1 (
    echo Security Kompilierung fehlgeschlagen!
    pause
    exit /b 1
)

echo.
echo [5] Kompiliere Web...
call gradlew.bat :lernapp-web:compileJava

if errorlevel 1 (
    echo Web Kompilierung fehlgeschlagen!
    pause
    exit /b 1
)

echo.
echo =====================================================
echo   KOMPILIERUNG ERFOLGREICH - STARTE APP
echo =====================================================
echo.

call gradlew.bat :lernapp-web:bootRun

pause
