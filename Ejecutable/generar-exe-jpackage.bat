@echo off
setlocal

echo ==========================================
echo Generador INSTALADOR EXE (Java 21 + WiX)
echo ==========================================
echo.

REM =========================================================
REM POSICIONAMIENTO: Garantiza que el script se ejecute desde
REM la ruta donde se encuentra el archivo .bat
REM =========================================================
cd /d "%~dp0"

echo Carpeta de trabajo:
echo %CD%
echo.

REM =========================================================
REM VALIDACIONES INICIALES: Verifica existencia de artefactos
REM necesarios para el empaquetado
REM =========================================================

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

REM =========================================================
REM CONFIGURACION DEL JDK: Define la ruta del JDK requerido
REM para herramientas jlink y jpackage
REM =========================================================
set "JDK_PATH=C:\Program Files\Java\jdk-21.0.10\bin"

if not exist "%JDK_PATH%\jlink.exe" (
    echo ERROR: No se encontro jlink.exe
    goto :error
)

if not exist "%JDK_PATH%\jpackage.exe" (
    echo ERROR: No se encontro jpackage.exe
    goto :error
)

REM =========================================================
REM LIMPIEZA PREVIA: Elimina artefactos de ejecuciones previas
REM =========================================================
if exist "build" (
    echo Eliminando build anterior...
    rmdir /s /q "build"
)

if exist "runtime" (
    echo Eliminando runtime anterior...
    rmdir /s /q "runtime"
)

if exist "VerificadorPrecios-1.0.0.msi" (
    echo Eliminando instalador anterior...
    del /f /q "VerificadorPrecios-1.0.0.msi"
)

REM =========================================================
REM PREPARACION DEL DIRECTORIO BUILD:
REM Se crea la estructura base y se copian los recursos
REM necesarios para el empaquetado
REM =========================================================
echo Creando estructura build...
mkdir build

echo Copiando archivo principal...
copy /y "VerificadorPrecios.jar" "build\"

echo Copiando librerias...
xcopy "lib" "build\lib\" /e /i /y || goto :error

echo Copiando reportes...
xcopy "reportes" "build\reportes\" /e /i /y || goto :error

echo Build creado correctamente
echo.

REM =========================================================
REM GENERACION DEL RUNTIME:
REM Se construye un runtime reducido usando jlink
REM =========================================================
echo Creando runtime...

"%JDK_PATH%\jlink.exe" ^
 --add-modules ALL-MODULE-PATH ^
 --output "runtime"

if errorlevel 1 (
    echo ERROR al crear runtime
    goto :error
)

echo Runtime creado correctamente
echo.

REM =========================================================
REM GENERACION DEL INSTALADOR:
REM Se empaqueta la aplicacion en formato MSI utilizando jpackage
REM =========================================================
echo Generando instalador...

"%JDK_PATH%\jpackage.exe" ^
 --input "build" ^
 --dest "." ^
 --name "VerificadorPrecios" ^
 --main-jar "VerificadorPrecios.jar" ^
 --main-class "App.Main" ^
 --type msi ^
 --runtime-image "runtime" ^
 --install-dir "VerificadorPrecios" ^
 --vendor "edelangel" ^
 --app-version "1.0.0" ^
 --win-upgrade-uuid "12345678-1234-1234-1234-123456789012" ^
 --win-shortcut ^
 --win-menu ^
 --win-menu-group "VerificadorPrecios" ^
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
echo %CD%\VerificadorPrecios-1.0.0.msi
echo.

REM =========================================================
REM LIMPIEZA FINAL:
REM Eliminacion de archivos temporales generados durante el proceso
REM =========================================================
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

REM =========================================================
REM MANEJO DE ERRORES:
REM Limpieza basica y notificacion en caso de fallo
REM =========================================================
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