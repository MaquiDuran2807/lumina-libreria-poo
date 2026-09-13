@echo off
REM Compila todo el proyecto (Windows + JDK 17+)
if not exist out mkdir out
dir /s /b src\*.java > fuentes.txt
javac -encoding UTF8 -d out @fuentes.txt
if %errorlevel%==0 (
  echo [OK] Compilado en .\out — ejecuta run.bat
) else (
  echo [ERROR] Fallo la compilacion
)
