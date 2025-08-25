@echo off
echo === Git Hard Reset to Last Commit ===
cd /d C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

echo Current status:
git status --short

echo.
echo Resetting to last commit...
git reset --hard HEAD

echo.
echo Restoring all deleted files...
git checkout HEAD -- .

echo.
echo Status after reset:
git status --short

echo.
echo Deleted files that were restored:
git diff --name-status HEAD~1 HEAD 2>nul

pause
