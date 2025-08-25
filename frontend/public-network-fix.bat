@echo off
REM Fachinformatiker Lernapp - Public Network Fix
REM Erstellt von Hans Hahn - Alle Rechte vorbehalten

title Public Network Firewall Fix

echo ========================================
echo   PUBLIC NETWORK PROBLEM FIX
echo   Netzwerk: GFN-Schulung 2
echo ========================================
echo.

echo [INFO] Problem identifiziert:
echo   Ihr Netzwerk ist als "PUBLIC" klassifiziert
echo   Windows blockiert eingehende Verbindungen von anderen Geräten
echo.

echo [LÖSUNG 1] Netzwerk auf PRIVAT umstellen (EMPFOHLEN)
echo   1. Windows-Einstellungen öffnen
echo   2. Netzwerk ^& Internet ^> WLAN
echo   3. "GFN-Schulung 2" anklicken
echo   4. Netzwerkprofil auf "PRIVAT" umstellen
echo.

echo [LÖSUNG 2] Firewall-Regel für PUBLIC-Netzwerk erstellen
echo   Als Administrator ausführen:
echo   netsh advfirewall firewall add rule name="Lernapp Public" dir=in action=allow protocol=TCP localport=5173 profile=public
echo.

echo [LÖSUNG 3] PowerShell als Administrator:
echo   Set-NetConnectionProfile -InterfaceAlias "WLAN" -NetworkCategory Private
echo   New-NetFirewallRule -DisplayName "Lernapp Public" -Direction Inbound -Protocol TCP -LocalPort 5173 -Action Allow -Profile Public
echo.

echo ========================================
echo   AUTOMATISCHER FIX-VERSUCH
echo ========================================
echo.

echo [1/3] Versuche Netzwerk auf Private umzustellen...
powershell -Command "Set-NetConnectionProfile -InterfaceAlias 'WLAN' -NetworkCategory Private" 2>nul
if %errorlevel% equ 0 (
    echo   ✅ Netzwerk erfolgreich auf PRIVATE umgestellt!
) else (
    echo   ❌ Fehlgeschlagen - Administrator-Rechte erforderlich
)
echo.

echo [2/3] Erstelle Public-Firewall-Regel...
netsh advfirewall firewall add rule name="Lernapp Frontend Public" dir=in action=allow protocol=TCP localport=5173 profile=public 2>nul
if %errorlevel% equ 0 (
    echo   ✅ Public-Firewall-Regel erstellt!
) else (
    echo   ❌ Fehlgeschlagen - Administrator-Rechte erforderlich
)
echo.

echo [3/3] Erstelle erweiterte Firewall-Regel...
powershell -Command "New-NetFirewallRule -DisplayName 'Lernapp Public Extended' -Direction Inbound -Protocol TCP -LocalPort 5173 -Action Allow -Profile Public,Private,Domain" 2>nul
if %errorlevel% equ 0 (
    echo   ✅ Erweiterte Firewall-Regel erstellt!
) else (
    echo   ❌ Fehlgeschlagen - Administrator-Rechte erforderlich
)
echo.

echo ========================================
echo   STATUS CHECK
echo ========================================
echo.

echo [TEST] Prüfe aktuelle Netzwerkkategorie...
powershell -Command "Get-NetConnectionProfile | Select-Object Name, NetworkCategory"
echo.

echo [TEST] Prüfe Port 5173...
netstat -an | findstr :5173
echo.

echo [TEST] Prüfe Firewall-Regeln...
powershell -Command "Get-NetFirewallRule -DisplayName '*5173*' | Select-Object DisplayName, Enabled"
echo.

echo ========================================
echo   MANUELLER FIX (falls automatisch fehlschlägt)
echo ========================================
echo.

echo 🔧 ALS ADMINISTRATOR AUSFÜHREN:
echo    1. CMD als Administrator öffnen
echo    2. Folgende Befehle eingeben:
echo.
echo    netsh advfirewall firewall add rule name="Lernapp Public Fix" dir=in action=allow protocol=TCP localport=5173 profile=public
echo    powershell Set-NetConnectionProfile -InterfaceAlias "WLAN" -NetworkCategory Private
echo.

echo 🔧 WINDOWS-EINSTELLUNGEN:
echo    1. Windows-Einstellungen ^> Netzwerk ^& Internet ^> WLAN
echo    2. "GFN-Schulung 2" anklicken
echo    3. Netzwerkprofil auf "PRIVAT" umstellen
echo.

echo 💡 IHRE FRONTEND-URL:
echo    http://10.42.1.177:5173
echo.

echo ⚠️  SICHERHEITSHINWEIS:
echo    In Schulungs-/Firmennetzwerken vorher IT-Administrator fragen!
echo.

pause