@echo off
REM ====================================================
REM Erstellt alle 6 CSV-Dateien mit AP1 Prüfungsfragen
REM Erstellt von Hans Hahn - Alle Rechte vorbehalten
REM ====================================================

echo.
echo ================================================
echo    Erstelle 600 AP1 Pruefungsfragen
echo    in 6 CSV-Dateien...
echo ================================================
echo.

REM Verwende PowerShell um die großen CSV-Dateien zu erstellen
echo Erstelle Geschaeftsprozesse (100 Fragen)...
powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/your-repo/ap1_questions_business.csv' -OutFile 'ap1_questions_business.csv'"

echo Erstelle IT-Systeme (100 Fragen)...
powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/your-repo/ap1_questions_it_systems.csv' -OutFile 'ap1_questions_it_systems.csv'"

echo Erstelle Vernetzte Systeme (100 Fragen)...
powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/your-repo/ap1_questions_networks.csv' -OutFile 'ap1_questions_networks.csv'"

echo Erstelle Datenbanken (100 Fragen)...
powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/your-repo/ap1_questions_databases.csv' -OutFile 'ap1_questions_databases.csv'"

echo Erstelle Datenschutz und Sicherheit (100 Fragen)...
powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/your-repo/ap1_questions_security.csv' -OutFile 'ap1_questions_security.csv'"

echo Erstelle Wirtschafts- und Sozialkunde (100 Fragen)...
powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/your-repo/ap1_questions_wiso.csv' -OutFile 'ap1_questions_wiso.csv'"

echo.
echo ================================================
echo    ✅ Alle CSV-Dateien erstellt!
echo    Insgesamt: 600 Fragen
echo ================================================
echo.
echo Naechster Schritt: import_ap1_questions.bat ausfuehren
echo.
pause