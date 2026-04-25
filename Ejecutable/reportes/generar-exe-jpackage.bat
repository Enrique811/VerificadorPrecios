@echo off
setlocal

echo ==========================================
echo Generador EXE + Runtime (Java 21)
echo ==========================================
echo.

REM Ir siempre a la carpeta del script
cd /d %~dp0

echo Carpeta de trabajo:
echo %CD%
echo.

REM =========================
REM VALIDACIONES
REM =========================

if not exist "app.jar" (
    echo ERROR: No se encontro "app.jar"
    goto :error
)

if not exist "lib" (
    echo ADVERTENCIA: No se encontro carpeta "lib"
    echo (si usas librerias externas, esto puede fallar)
)

REM =========================
REM VERIFICAR JDK
REM =========================

set JDK_PATH=C:\Program Files\Java\jdk-21.0.10\bin

if not exist "%JDK_PATH%\jlink.exe" (
    echo ERROR: No se encontro jlink en:
    echo %JDK_PATH%
    goto :error
)

if not exist "%JDK_PATH%\jpackage.exe" (
    echo ERROR: No se encontro jpackage en:
    echo %JDK_PATH%
    goto :error
)

REM =========================
REM CREAR RUNTIME SI NO EXISTE
REM =========================

if not exist "runtime" (
    echo.
    echo Creando runtime...
    
    "%JDK_PATH%\jlink.exe" ^
     --add-modules java.base,java.desktop,java.logging,java.sql,java.xml ^
     --output runtime

    if errorlevel 1 (
        echo ERROR al crear runtime
        goto :error
    )

    echo Runtime creado correctamente
) else (
    echo Runtime ya existe, se reutiliza
)

REM =========================
REM GENERAR EXE
REM =========================

echo.
echo Generando EXE...

"%JDK_PATH%\jpackage.exe" ^
 --input . ^
 --dest . ^
 --name VerificadorPrecios ^
 --main-jar app.jar ^
 --main-class Ventanas.VentanaInicio ^
 --type exe ^
 --runtime-image runtime ^
 --win-shortcut ^
 --win-menu

if errorlevel 1 (
    echo.
    echo ERROR al generar el EXE
    goto :error
)

echo.
echo ==========================================
echo EXE GENERADO CORRECTAMENTE
echo ==========================================
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