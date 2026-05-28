@echo off
chcp 65001 >nul
cd /d %~dp0springboot
echo ========================================
echo  启动后端 (Spring Boot :9090)
echo ========================================
start "Backend-9090" java -jar target\springboot-0.0.1-SNAPSHOT.jar
echo 后端已启动，访问 http://localhost:9090
pause
