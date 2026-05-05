# Tecnologias y Dependencias

## Stack Base

- Lenguaje: Java
- UI: Swing/AWT
- IDE/Build: NetBeans + Ant
- Reporteria: JasperReports
- Base de datos: Firebird
- Driver JDBC: Jaybird
- Impresion: Java Print Service + Jasper print exporter

## Configuracion de Build

Datos obtenidos de `nbproject/project.properties` y `build.xml`:

- `main.class`: `Ventanas.VentanaInicio`
- `javac.source`: `1.8`
- `javac.target`: `1.8`
- `platform.active`: `JDK_21`
- empaquetado de salida: `dist/Verificador.jar`

El `build.xml` copia recursos de `fonts/` despues de compilar para que la fuente y configuracion de Jasper queden disponibles en runtime.

## Librerias Detectadas

### Base de datos

- `jaybird-full-2.2.4.jar`

### Reportes / PDF / graficos

- `jasperreports-6.3.0.jar`
- `com.lowagie.text-2.1.7.jar`
- `jcommon-1.0.23.jar`
- `jfreechart-1.0.19.jar`

### Utilerias Apache

- `commons-beanutils-1.8.3.jar`
- `commons-codec-1.10.jar`
- `commons-digester-2.1.jar`
- `commons-javaflow.jar`
- `commons-logging-1.1.jar`
- `commons.collections-3.2.1.jar`

### UI / extras

- `jcalendar-1.4.jar`
- `swing-worker-1.1.jar`
- `timingframework-classic-1.1.jar`
- `JCarrierPigeon-1.3.jar`

### Otras

- `code128.jar`
- `joda-time-2.10.10.jar`
- `poi-3.12-20150511.jar`
- `poi-3.17.jar`

## Recursos del Proyecto

### `configuracion.properties`

Define:

- `ambiente`
- `clave`
- `formatoPrecio`
- `impresora`
- `informacion`
- `ipEmpresa`
- `rutaEmpresa`

### `fonts/`

Incluye:

- `code128.ttf`
- `fonts.xml`
- `jasperreports_extension.properties`

Se usa para renderizar e integrar codigos de barras en etiquetas.

### `reportes/`

Incluye plantillas Jasper:

- `EtiquetaPrecio.jrxml`
- variantes historicas o auxiliares del reporte

## Observaciones Tecnicas

- El proyecto combina Java 8 a nivel de compilacion con una plataforma JDK 21 en NetBeans; esto puede funcionar, pero conviene validar compatibilidad de toolchain.
- Hay dos versiones de Apache POI declaradas en el classpath, lo que puede introducir conflictos si realmente se usan.
- No se detectaron pruebas automatizadas activas ni framework de testing configurado en `test/`.
