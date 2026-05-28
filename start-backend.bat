@echo off
chcp 65001 >nul
cd /d %~dp0springboot

:: 从 project-config.json 读取后端端口
for /f "tokens=2 delims=:, " %%a in ('findstr "backendPort" ..\project-config.json') do set BEPORT=%%a
if "%BEPORT%"=="" set BEPORT=9090

echo ========================================
echo  启动后端 (Spring Boot :%BEPORT%)
echo ========================================
start "Backend-%BEPORT%" java -jar target\springboot-0.0.1-SNAPSHOT.jar
echo 后端已启动，访问 http://localhost:%BEPORT%
pause
