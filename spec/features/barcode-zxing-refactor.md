# Feature Spec: barcode-zxing-refactor

## Metadata

- id: `barcode-zxing-refactor`
- fecha: `2026-04-22`
- estado: `implemented`
- owner: `codex`

## Contexto

La implementacion anterior generaba codigos de barras en `VentanaInicio` usando una fuente `Code 128`, validaciones manuales y renderizado de texto sobre imagen.

Hallazgos del analisis inicial:

- generacion antigua: [src/Ventanas/VentanaInicio.java](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/src/Ventanas/VentanaInicio.java:613)
- consumo en reporte: [src/Metodos/DatosReporte.java](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/src/Metodos/DatosReporte.java:14)
- campo Jasper: [reportes/EtiquetaPrecio.jrxml](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/reportes/EtiquetaPrecio.jrxml:7)

## Objetivo

Reemplazar la generacion manual por ZXing con soporte para:

- EAN-8
- EAN-13
- UPC-A
- fallback a CODE128

## Alcance

- agregar ZXing al proyecto Ant;
- crear un modulo aislado para deteccion y generacion;
- integrar la nueva fachada en el flujo de impresion;
- mantener compatibilidad con Jasper al seguir entregando `java.awt.Image`.

## No Alcance

- rediseñar reportes Jasper;
- cambiar `DatosReporte`;
- modificar consultas SQL.

## Puntos de Integracion Identificados

### Clases involucradas

- `Ventanas.VentanaInicio`
- `Metodos.DatosReporte`
- `reportes/EtiquetaPrecio.jrxml`

### Metodos del codigo viejo encontrados

- `procesarCodigoBarras(String)`
- `validarEAN13(String)`
- `validarEAN8(String)`
- `encodeCode128B(String)`
- `crearImagenCodigoBarras(String, int, int)`
- `registrarFuenteCodigoBarras()`

### Invocaciones / uso

- `VentanaInicio.imprimirEtiqueta()` era el punto unico de generacion e invocacion real.
- `DatosReporte` y Jasper solo consumen la imagen final.

### Punto reemplazado

- `VentanaInicio.imprimirEtiqueta()` ahora usa `BarcodeFacade`.

## Cambios Implementados

- nuevo paquete: `src/com/project/barcode/newimpl`
- nueva fachada: [BarcodeFacade.java](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/src/com/project/barcode/newimpl/BarcodeFacade.java:8)
- servicio de deteccion/validacion: [BarcodeService.java](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/src/com/project/barcode/newimpl/BarcodeService.java:6)
- generador ZXing: [BarcodeGenerator.java](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/src/com/project/barcode/newimpl/BarcodeGenerator.java:12)
- integracion controlada: [VentanaInicio.java](/C:/Users/gaming/Documents/NetBeansProjects/Proyectos/VerificadorPrecios/src/Ventanas/VentanaInicio.java:599)

## Uso de BarcodeFacade

```java
BarcodeFacade facade = new BarcodeFacade();
byte[] png = facade.generateBarcode("5901234123457");
```

Flujo interno:

1. detecta tipo;
2. valida checksum si aplica;
3. si falla, usa `CODE128`;
4. genera PNG y retorna `byte[]`.

## Criterios de Aceptacion Cubiertos

1. El proyecto contiene un modulo aislado de codigos de barras con ZXing.
2. Se soportan EAN-8, EAN-13, UPC-A y fallback a CODE128.
3. El flujo de impresion existente usa la nueva fachada sin cambiar el reporte Jasper.
4. Se genero una prueba aislada con PNGs de muestra.

## Referencias

- decision: [ADR-001-zxing-barcode-generation.md](../decisions/ADR-001-zxing-barcode-generation.md)
- tareas: [barcode-zxing-migration.md](../tasks/barcode-zxing-migration.md)
