@echo off
chcp 65001 >nul
echo ========================================
echo  关闭项目
echo ========================================

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":9090" ^| findstr "LISTENING"') do (
    echo 关闭后端 PID: %%a
    taskkill /F /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo 关闭前端 PID: %%a
    taskkill /F /PID %%a >nul 2>&1
)

echo 项目已关闭
pause
