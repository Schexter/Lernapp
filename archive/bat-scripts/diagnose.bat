@echo off
echo =====================================================
echo   LERNAPP DIAGNOSTIC
echo =====================================================
echo.

cd /d C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

echo [1] Checking Java...
java -version
echo.

echo [2] Checking Gradle...
call gradlew.bat --version
echo.

echo [3] Listing modules...
dir /b lernapp-* 2>nul
echo.

echo [4] Building core module...
call gradlew.bat :lernapp-core:build -x test

if errorlevel 1 (
    echo.
    echo === CORE BUILD FAILED ===
    echo Checking for compilation errors...
    call gradlew.bat :lernapp-core:compileJava
) else (
    echo.
    echo [5] Building security module...
    call gradlew.bat :lernapp-security:build -x test
    
    if errorlevel 1 (
        echo.
        echo === SECURITY BUILD FAILED ===
    ) else (
        echo.
        echo [6] Building web module...
        call gradlew.bat :lernapp-web:build -x test
        
        if errorlevel 1 (
            echo.
            echo === WEB BUILD FAILED ===
        ) else (
            echo.
            echo === ALL MODULES BUILT SUCCESSFULLY ===
            echo.
            echo [7] Starting application...
            call gradlew.bat :lernapp-web:bootRun
        )
    )
)

pause
