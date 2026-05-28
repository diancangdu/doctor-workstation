@echo off
chcp 65001 >nul
cd /d %~dp0

echo ========================================
echo  启动项目（后端 + 前端）
echo ========================================

:: 启动后端
echo [1/2] 启动后端...
start "Backend-9090" cmd /c "cd /d %~dp0springboot && java -jar target\springboot-0.0.1-SNAPSHOT.jar"

:: 等待后端启动
echo 等待后端就绪...
timeout /t 8 /nobreak >nul

:: 启动前端
echo [2/2] 启动前端...
start "Frontend-8080" cmd /c "cd /d %~dp0 && npm run serve"

echo ========================================
echo  后端: http://localhost:9090
echo  前端: http://localhost:8080
echo  关闭: 双击 stop.bat
echo ========================================
pause
