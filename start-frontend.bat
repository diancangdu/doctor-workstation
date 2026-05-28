@echo off
chcp 65001 >nul
cd /d %~dp0
echo ========================================
echo  启动前端 (Vue CLI :8080)
echo ========================================
npm run serve
echo 前端已启动，访问 http://localhost:8080
pause
