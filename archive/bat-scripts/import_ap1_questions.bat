@echo off
echo ========================================
echo   AP1 Fragen Import
echo   Importiere alle AP1-Prüfungsfragen
echo ========================================
echo.

echo Nutze die H2-Console zum Import:
echo.
echo 1. Öffne http://localhost:8080/h2-console
echo 2. Verbinde mit:
echo    - JDBC URL: jdbc:h2:mem:lernappdb
echo    - User: sa
echo    - Password: (leer)
echo.
echo 3. Führe das SQL-Script aus: import_ap1_questions.sql
echo.
pause
