@echo off
setlocal

echo ==========================================
echo Generador INSTALADOR EXE (Java 21 + WiX)
echo ==========================================
echo.

REM ==========================================
REM MOVERSE A LA CARPETA DEL SCRIPT
REM ==========================================
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

if not exist "fonts" (
    echo ERROR: No se encontro fonts
    goto :error
)

REM ==========================================
REM CONFIGURACION DEL JDK
REM ==========================================
set "JDK_PATH=C:\Program Files\Java\jdk-21.0.10\bin"

if not exist "%JDK_PATH%\jlink.exe" (
    echo ERROR: No se encontro jlink.exe
    goto :error
)

if not exist "%JDK_PATH%\jpackage.exe" (
    echo ERROR: No se encontro jpackage.exe
    goto :error
)

REM ==========================================
REM LIMPIAR BUILD ANTERIOR
REM ==========================================
if exist "build" (
    echo Eliminando build anterior...
    rmdir /s /q "build"
)

REM ==========================================
REM LIMPIAR RUNTIME ANTERIOR
REM ==========================================
if exist "runtime" (
    echo Eliminando runtime anterior...
    rmdir /s /q "runtime"
)

REM ==========================================
REM LIMPIAR INSTALADOR ANTERIOR
REM ==========================================
if exist "VerificadorPrecios.exe" (
    echo Eliminando instalador anterior...
    del /f /q "VerificadorPrecios.exe"
)

REM ==========================================
REM CREAR BUILD LIMPIO
REM ==========================================
echo Creando estructura build...
mkdir build

copy /y "VerificadorPrecios.jar" "build\"
copy /y "configuracion.properties" "build\"

xcopy "lib" "build\lib\" /e /i /y || goto :error
xcopy "reportes" "build\reportes\" /e /i /y || goto :error
xcopy "fonts" "build\fonts\" /e /i /y || goto :error

echo Build creado correctamente
echo.

REM ==========================================
REM CREAR RUNTIME EMBEBIDO
REM ==========================================
echo Creando runtime...

"%JDK_PATH%\jlink.exe" ^
 --add-modules java.base,java.desktop,java.logging,java.sql,java.xml,java.naming,java.management,java.datatransfer,java.prefs ^
 --output "runtime"

if errorlevel 1 (
    echo ERROR al crear runtime
    goto :error
)

echo Runtime creado correctamente
echo.

REM ==========================================
REM GENERAR INSTALADOR EXE
REM ==========================================
echo Generando instalador...

"%JDK_PATH%\jpackage.exe" ^
 --input "build" ^
 --dest "." ^
 --name "VerificadorPrecios" ^
 --main-jar "VerificadorPrecios.jar" ^
 --main-class "Ventanas.VentanaInicio" ^
 --type exe ^
 --runtime-image "runtime" ^
 --install-dir "VerificadorPrecios" ^
 --vendor "edelangel" ^
 --app-version "1.0.0" ^
 --win-upgrade-uuid "12345678-1234-1234-1234-123456789012" ^
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

echo Instalador generado:
echo %CD%\VerificadorPrecios.exe
echo.

REM ==========================================
REM LIMPIEZA TEMPORAL
REM ==========================================
if exist "build" (
    echo Eliminando build temporal...
    rmdir /s /q "build"
)

if exist "runtime" (
    echo Eliminando runtime temporal...
    rmdir /s /q "runtime"
)

echo.
echo Proceso finalizado correctamente.
echo.

pause
exit /b 0

:error
echo.
echo ==========================================
echo ERROR EN EL PROCESO
echo ==========================================
echo.

if exist "build" rmdir /s /q "build"
if exist "runtime" rmdir /s /q "runtime"

pause
exit /b 1