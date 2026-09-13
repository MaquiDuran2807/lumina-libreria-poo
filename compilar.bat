@echo off
REM Lumina Libreria - compila el proyecto (Windows + JDK 17+)
setlocal
cd /d "%~dp0"
chcp 65001 >nul

REM 1. Localizar javac: primero PATH, luego instalaciones comunes
where javac >nul 2>nul
if %errorlevel%==0 goto :compilar
set "JDK_DIR="
for /D %%D in ("C:\Program Files\Eclipse Adoptium\jdk*" "C:\Program Files\Java\jdk*" "C:\Program Files\Microsoft\jdk*") do (
  if exist "%%~D\bin\javac.exe" set "JDK_DIR=%%~D"
)
if defined JDK_DIR (
  echo [INFO] Usando JDK: %JDK_DIR%
  set "PATH=%JDK_DIR%\bin;%PATH%"
) else (
  echo [ERROR] No se encontro 'javac'.
  echo Instala JDK 17+ desde https://adoptium.net, ejecita el instalador
  echo y vuelve a correr compilar.bat.
  pause
  exit /b 1
)

:compilar
if not exist out mkdir out
REM OJO: en archivos @argumentos javac trata \ como escape, por eso se
REM escriben las rutas con / (javac las acepta en Windows).
setlocal EnableDelayedExpansion
(for /R src %%f in (*.java) do (
  set "P=%%f"
  echo "!P:\=/!"
)) > fuentes.txt
endlocal
javac -encoding UTF8 -d out @fuentes.txt
if %errorlevel% neq 0 (
  echo [ERROR] Fallo la compilacion. Revisa los mensajes de arriba.
  pause
  exit /b 1
)
echo [OK] Compilado en .\out - ahora ejecuta run.bat
