@echo off
echo === Fachinformatiker Lernapp - Wiederherstellung und Start ===
echo.

cd /d C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

echo [1] Beende alle Java-Prozesse...
taskkill /F /IM java.exe 2>nul

echo [2] Git-Repository wiederherstellen...
git reset --hard HEAD 2>nul
git checkout -- . 2>nul
git clean -fd 2>nul

echo [3] Clean Build...
call gradlew.bat clean

echo [4] Starte die App...
call gradlew.bat :lernapp-web:bootRun

pause
