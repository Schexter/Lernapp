@echo off
cd /d C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java
git status --short > git_status.txt 2>&1
type git_status.txt
