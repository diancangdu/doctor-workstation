@echo off
cd /d %~dp0

echo ========================================
echo   Start All (Backend + Frontend)
echo ========================================

echo [1/2] Starting backend ...
start "Backend" cmd /c "cd /d %~dp0springboot && java -jar target\springboot-0.0.1-SNAPSHOT.jar"

echo Waiting for backend ...
timeout /t 8 /nobreak >nul

echo [2/2] Starting frontend ...
start "Frontend" cmd /c "cd /d %~dp0 && npm run serve"

echo ========================================
echo   Backend : http://localhost:9090
echo   Frontend: http://localhost:8080
echo   Stop   : stop.bat
echo ========================================
pause
