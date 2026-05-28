@echo off
chcp 65001 >nul
cd /d %~dp0

:: 从 project-config.json 读取端口
for /f "tokens=2 delims=:, " %%a in ('findstr "backendPort" project-config.json') do set BEPORT=%%a
for /f "tokens=2 delims=:, " %%a in ('findstr "frontendPort" project-config.json') do set FEPORT=%%a
if "%BEPORT%"=="" set BEPORT=9090
if "%FEPORT%"=="" set FEPORT=8080

echo ========================================
echo  关闭项目
echo ========================================

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%BEPORT%" ^| findstr "LISTENING"') do (
    echo 关闭后端 PID: %%a
    taskkill /F /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%FEPORT%" ^| findstr "LISTENING"') do (
    echo 关闭前端 PID: %%a
    taskkill /F /PID %%a >nul 2>&1
)

echo 项目已关闭
pause
