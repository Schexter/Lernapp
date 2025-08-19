@echo off
echo Starting Fachinformatiker Lernapp...
cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java
echo.
echo Cleaning build directories...
rmdir /s /q lernapp-core\build 2>nul
rmdir /s /q lernapp-web\build 2>nul
rmdir /s /q lernapp-security\build 2>nul
echo.
echo Compiling modules...
call gradlew :lernapp-core:compileJava
if %errorlevel% neq 0 (
    echo ERROR: Failed to compile lernapp-core
    pause
    exit /b %errorlevel%
)
call gradlew :lernapp-security:compileJava
if %errorlevel% neq 0 (
    echo ERROR: Failed to compile lernapp-security
    pause
    exit /b %errorlevel%
)
call gradlew :lernapp-web:compileJava
if %errorlevel% neq 0 (
    echo ERROR: Failed to compile lernapp-web
    pause
    exit /b %errorlevel%
)
echo.
echo Starting application...
call gradlew :lernapp-web:bootRun --debug
pause
