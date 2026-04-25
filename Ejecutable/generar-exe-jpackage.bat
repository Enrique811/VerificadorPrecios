@echo off
setlocal

echo ==========================================
echo Generador INSTALADOR EXE (Java 21 + WiX)
echo ==========================================
echo.

REM Moverse a la carpeta del script
cd /d "%~dp0"

echo Carpeta de trabajo:
echo %CD%
echo.

REM ==========================================
REM VALIDACIONES OBLIGATORIAS
REM ==========================================

if not exist "VerificadorPrecios.jar" (
    echo ERROR: No se encontro VerificadorPrecios.jar
    goto :error
)

if not exist "lib" (
    echo ERROR: No se encontro la carpeta lib
    goto :error
)

if not exist "reportes" (
    echo ERROR: No se encontro la carpeta reportes
    goto :error
)

if not exist "configuracion.properties" (
    echo ERROR: No se encontro configuracion.properties
    goto :error
)

REM Validar icono
if not exist "logo_icon.png" (
    echo ERROR: No se encontro logo.ico
    goto :error
)

REM ==========================================
REM CONFIGURACION JDK
REM ==========================================
set "JDK_PATH=C:\Program Files\Java\jdk-21.0.10\bin"

if not exist "%JDK_PATH%\jlink.exe" (
    echo ERROR: No se encontro jlink
    goto :error
)

if not exist "%JDK_PATH%\jpackage.exe" (
    echo ERROR: No se encontro jpackage
    goto :error
)

REM ==========================================
REM LIMPIAR RUNTIME
REM ==========================================
if exist "runtime" (
    echo Eliminando runtime anterior...
    rmdir /s /q "runtime"
)

REM ==========================================
REM LIMPIAR INSTALADOR ANTERIOR
REM ==========================================
if exist "VerificadorPrecios.exe" (
    del /f /q "VerificadorPrecios.exe"
)

REM ==========================================
REM CREAR RUNTIME
REM ==========================================
echo Creando runtime...

"%JDK_PATH%\jlink.exe" ^
 --add-modules java.base,java.desktop,java.logging,java.sql,java.xml ^
 --output "runtime"

if errorlevel 1 (
    echo ERROR al crear runtime
    goto :error
)

echo Runtime creado correctamente
echo.

REM ==========================================
REM GENERAR INSTALADOR CON ICONO
REM ==========================================
echo Generando instalador...

"%JDK_PATH%\jpackage.exe" ^
 --input "." ^
 --dest "." ^
 --name "VerificadorPrecios" ^
 --main-jar "VerificadorPrecios.jar" ^
 --main-class "Ventanas.VentanaInicio" ^
 --type exe ^
 --runtime-image "runtime" ^
 --icon "logo_icon.png" ^
 --install-dir "VerificadorPrecios" ^
 --win-dir-chooser ^
 --win-menu ^
 --win-shortcut ^
 --win-shortcut-prompt ^
 --vendor "TuEmpresa" ^
 --app-version "1.0" ^
 --java-options "-Dfile.encoding=UTF-8"

if errorlevel 1 (
    echo.
    echo ERROR al generar el instalador
    goto :error
)

echo.
echo ==========================================
echo INSTALADOR GENERADO CORRECTAMENTE
echo ==========================================
echo.

echo Icono aplicado correctamente
echo Instalador generado:
echo %CD%\VerificadorPrecios.exe
echo.

pause
exit /b 0

:error
echo.
echo ==========================================
echo ERROR EN EL PROCESO
echo ==========================================
echo.
pause
exit /b 1