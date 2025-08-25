@echo off
cd /d C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java
echo Checking Git status...
git status
echo.
echo Restoring deleted files...
git checkout -- .
echo.
echo Resetting all changes...
git reset --hard HEAD
echo.
echo Current status:
git status
echo.
echo Done!
pause