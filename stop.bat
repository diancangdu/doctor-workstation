@echo off
cd /d %~dp0

echo ========================================
echo   Stop All
echo ========================================

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":9090" ^| findstr "LISTENING"') do (
    echo Stopping backend PID: %%a
    taskkill /F /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo Stopping frontend PID: %%a
    taskkill /F /PID %%a >nul 2>&1
)

echo Stopped
pause
