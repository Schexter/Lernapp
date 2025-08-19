@echo off
echo ===============================================
echo   Fixing Authentication Issues
echo ===============================================

REM Kill all Java processes
echo Stopping all Java processes...
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul

REM Clear temporary files
echo Clearing temporary files...
del /Q "%TEMP%\*.tmp" 2>nul
rmdir /S /Q .gradle\caches\journal-1 2>nul

REM Set development profile
echo Setting development profile...
set SPRING_PROFILES_ACTIVE=dev

REM Start with reduced security for testing
echo Starting application with demo mode...
gradlew bootRun --args="--spring.profiles.active=dev --security.enable-csrf=false --server.port=8080"
