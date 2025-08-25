@echo off
REM ========================================
REM FIX BUILD & START
REM Autor: Hans Hahn
REM ========================================

cls
echo ========================================
echo    BUILD FIX & APP START
echo ========================================
echo.

echo [1] Cleaning old build...
call gradlew clean

echo.
echo [2] Building application...
call gradlew build

if %errorlevel% neq 0 (
    echo.
    echo ========================================
    echo    BUILD FEHLGESCHLAGEN!
    echo ========================================
    echo Pruefe die Fehlermeldungen oben.
    pause
    exit /b 1
)

echo.
echo ========================================
echo    BUILD ERFOLGREICH!
echo ========================================
echo.
echo [3] Starte Spring Boot Server...
call gradlew bootRun

REM Erstellt von Hans Hahn - Alle Rechte vorbehalten