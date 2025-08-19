@echo off
echo ========================================
echo Fixing Git Commit Issues
echo ========================================
echo.

cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

echo Step 1: Checking Git Status...
git status
echo.

echo Step 2: Adding all files...
git add -A
echo.

echo Step 3: Committing changes...
git commit -m "Add complete data initializer with 150 AP1 questions and fix compilation issues"
echo.

echo Step 4: Pushing to remote...
git push origin main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Push failed. Trying with force...
    git push -f origin main
)

echo.
echo ========================================
echo Git operations completed!
echo ========================================
pause
