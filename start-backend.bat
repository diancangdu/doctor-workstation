@echo off
cd /d %~dp0springboot

echo ========================================
echo   Start Backend (Spring Boot)
echo ========================================
start "Backend" java -jar target\springboot-0.0.1-SNAPSHOT.jar
echo Backend started at http://localhost:9090
pause
