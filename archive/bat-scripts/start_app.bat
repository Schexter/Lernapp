@echo off
echo ========================================
echo   FACHINFORMATIKER LERNAPP STARTEN
echo ========================================
echo.
cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java
echo Aktueller Ordner: %CD%
echo.
echo [1] Build wird vorbereitet...
call gradlew clean
echo.
echo [2] Kompiliere Module...
call gradlew :lernapp-core:compileJava
call gradlew :lernapp-security:compileJava
call gradlew :lernapp-web:compileJava
echo.
echo [3] Starte Anwendung...
echo.
echo ========================================
echo   App startet auf http://localhost:8080
echo   Swagger UI: http://localhost:8080/swagger-ui.html
echo   H2 Console: http://localhost:8080/h2-console
echo ========================================
echo.
call gradlew :lernapp-web:bootRun
pause
