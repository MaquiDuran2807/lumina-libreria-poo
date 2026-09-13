@echo off
REM Lumina Libreria - ejecuta la app (compila solo si hace falta)
setlocal
cd /d "%~dp0"
chcp 65001 >nul

if not exist "out\com\libreriasmart\Main.class" (
  echo [INFO] Aun no esta compilado. Compilando primero...
  call "%~dp0compilar.bat"
  if %errorlevel% neq 0 exit /b 1
)

REM Localizar java: primero PATH, luego instalaciones comunes
where java >nul 2>nul
if %errorlevel%==0 goto :ejecutar
set "JDK_DIR="
for /D %%D in ("C:\Program Files\Eclipse Adoptium\jdk*" "C:\Program Files\Java\jdk*" "C:\Program Files\Microsoft\jdk*") do (
  if exist "%%~D\bin\java.exe" set "JDK_DIR=%%~D"
)
if defined JDK_DIR (
  echo [INFO] Usando JDK: %JDK_DIR%
  set "PATH=%JDK_DIR%\bin;%PATH%"
) else (
  echo [ERROR] No se encontro 'java'.
  echo Instala JDK 17+ desde https://adoptium.net y vuelve a intentarlo.
  pause
  exit /b 1
)

:ejecutar
java -cp out com.libreriasmart.Main
pause
