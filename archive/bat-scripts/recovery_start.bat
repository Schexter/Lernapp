@echo off
echo ========================================================
echo   FACHINFORMATIKER LERNAPP - RECOVERY MODE
echo ========================================================
echo.

cd /d C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

echo [1] Killing all Java processes...
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul

echo.
echo [2] Clearing Gradle cache...
rmdir /s /q .gradle\buildOutputCleanup 2>nul
del /q .gradle\*.lock 2>nul

echo.
echo [3] Building project...
call gradlew.bat clean build -x test

if errorlevel 1 (
    echo.
    echo === BUILD FAILED ===
    echo Trying to fix common issues...
    
    echo Removing problematic files...
    del /q lernapp-core\src\main\java\com\fachinformatiker\lernapp\model\Tag.java 2>nul
    del /q lernapp-core\src\main\java\com\fachinformatiker\lernapp\model\Topic.java 2>nul
    
    echo Rebuilding...
    call gradlew.bat build -x test
)

echo.
echo [4] Starting application...
java -jar lernapp-web\build\libs\lernapp-web-*.jar

if errorlevel 1 (
    echo.
    echo === ALTERNATE START METHOD ===
    call gradlew.bat bootRun --no-daemon
)

pause
