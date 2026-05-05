# Task Plan: barcode-zxing-migration

## Metadata

- fecha: `2026-04-22`
- owner: `codex`
- feature: `barcode-zxing-refactor`

## Objetivo

Migrar la generacion de codigos de barras a ZXing sin romper el flujo actual de impresion de etiquetas.

## Archivos / Modulos Afectados

- [nbproject/project.properties](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/nbproject/project.properties:1)
- [build.xml](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/build.xml:1)
- [src/com/project/barcode/newimpl/BarcodeType.java](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/src/com/project/barcode/newimpl/BarcodeType.java:1)
- [src/com/project/barcode/newimpl/BarcodeService.java](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/src/com/project/barcode/newimpl/BarcodeService.java:1)
- [src/com/project/barcode/newimpl/BarcodeGenerator.java](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/src/com/project/barcode/newimpl/BarcodeGenerator.java:1)
- [src/com/project/barcode/newimpl/BarcodeFacade.java](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/src/com/project/barcode/newimpl/BarcodeFacade.java:1)
- [src/com/project/barcode/newimpl/BarcodeSampleApp.java](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/src/com/project/barcode/newimpl/BarcodeSampleApp.java:1)
- [src/Ventanas/VentanaInicio.java](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/src/Ventanas/VentanaInicio.java:599)

## Plan Ejecutado

1. Se localizaron clases, metodos e invocaciones del codigo viejo.
2. Se descargaron `core-3.5.3.jar` y `javase-3.5.3.jar` a `lib/`.
3. Se actualizo el classpath Ant en `nbproject/project.properties`.
4. Se elimino la referencia a `code128.jar` y la copia de `code128.ttf` en `build.xml`.
5. Se implemento el modulo nuevo con deteccion, validacion y generacion PNG.
6. Se integro `BarcodeFacade` en `VentanaInicio.imprimirEtiqueta()`.
7. Se genero una prueba aislada con PNGs en `build/barcode-samples/`.

## Verificacion

- compilacion por `javac` sobre todo `src/` con classpath completo de `lib/`;
- generacion de:
  - `build/barcode-samples/ean8.png`
  - `build/barcode-samples/ean13.png`
  - `build/barcode-samples/upca.png`
  - `build/barcode-samples/code128.png`

## Riesgos / Bloqueos

- `ant` no esta disponible en PATH del entorno actual;
- `javac` de JDK 21 compila, pero emite una excepcion interna al cerrar `jasperreports-6.3.0.jar`; los `.class` si se generan correctamente.
