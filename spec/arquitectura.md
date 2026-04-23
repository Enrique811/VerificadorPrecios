# Arquitectura y Modulos

## Estilo General

La arquitectura es una aplicacion monolitica de escritorio con acoplamiento alto entre UI, estado estatico y acceso a datos.

No hay capas estrictamente separadas, pero el codigo se organiza en cuatro grupos funcionales:

- interfaz (`Ventanas`)
- acceso a datos (`SQL`)
- conexion/licencia (`Conexion`)
- soporte/configuracion/reportes (`Metodos`)

## Modulos Principales

### 1. UI Principal

**Clase:** `Ventanas.VentanaInicio`

Responsabilidades:

- construir la interfaz principal;
- capturar codigo de barras e informacion adicional;
- lanzar consulta del articulo;
- mostrar estados visuales de exito/error;
- abrir dialogos de busqueda y configuracion;
- generar e imprimir la etiqueta.

Dependencias relevantes:

- `Configuracion`
- `Conexion`
- `SQLArticulo`
- `SQLFechaHora`
- `PrecioFormatter`
- `DatosReporte`
- JasperReports

## 2. Busqueda de Articulos

**Clases:** `Ventanas.BusquedaDialog`, `Ventanas.VentanaBuscarArticulo`

Responsabilidades:

- buscar por descripcion;
- renderizar tabla de resultados;
- retornar el codigo seleccionado a `VentanaInicio`.

Notas:

- `VentanaBuscarArticulo` actua como compatibilidad/herencia ligera sobre `BusquedaDialog`.
- La tabla usa render personalizado (`FormatoTablaBArt`) y modelo estatico.

## 3. Configuracion

**Clases:** `Ventanas.ConfiguracionWindow`, `Metodos.ConfigManager`, `Metodos.Configuracion`

Responsabilidades:

- leer y persistir `configuracion.properties`;
- editar ambiente, licencia, impresora, informacion e IP;
- propagar cambios a la ventana principal.

Diferencia interna:

- `Configuracion` mezcla lectura de propiedades con utilidades criptograficas para licencia.
- `ConfigManager` introduce una API mas limpia para lectura/escritura de configuracion.

## 4. Conexion y Licencia

**Clase:** `Conexion.Conexion`

Responsabilidades:

- abrir conexion JDBC a Firebird;
- reutilizar una conexion unica estatica;
- administrar `PreparedStatement` y `ResultSet`;
- validar vigencia de licencia con AES y fechas.

Detalles:

- usuario Firebird fijo: `SYSDBA`
- password fijo: `masterkey`
- charset configurado: `ISO8859_1`
- la licencia depende de una clave cifrada almacenada en propiedades.

## 5. Acceso a Datos

**Clases:** `SQL.SQLArticulo`, `SQL.SQLFechaHora`

Responsabilidades:

- consultar producto por codigo;
- buscar articulos por descripcion;
- consultar fecha actual del servidor;
- consultar precios especiales.

Observaciones:

- `SQLArticulo` carga resultados en campos estaticos globales.
- La UI consume esos campos directamente despues de cada consulta.
- El parametro `tarifa` existe en varias firmas, pero en el flujo actual no siempre afecta la consulta principal.

## 6. Impresion y Reportes

**Elementos:** `VentanaInicio.imprimirEtiqueta`, `Metodos.DatosReporte`, `reportes/EtiquetaPrecio.jrxml`

Responsabilidades:

- construir imagen de codigo de barras usando fuente `Code 128`;
- mapear datos a `JRBeanCollectionDataSource`;
- compilar el reporte `.jrxml`;
- mostrar vista previa o enviar a impresora segun ambiente.

Ambientes:

- `a`: vista previa Jasper.
- `b`: impresion directa a la impresora configurada.

## Dependencias Entre Modulos

```text
VentanaInicio
  -> Configuracion
  -> Conexion
  -> SQLArticulo
  -> SQLFechaHora
  -> PrecioFormatter
  -> DatosReporte
  -> JasperReports

BusquedaDialog
  -> SQLArticulo
  -> PrecioFormatter
  -> FormatoTablaBArt
  -> VentanaInicio

ConfiguracionWindow
  -> ConfigManager
  -> Configuracion
  -> PrinterUtils
  -> VentanaInicio

SQLArticulo / SQLFechaHora
  -> Conexion
```

## Riesgos Arquitectonicos Iniciales

- Uso extensivo de estado estatico compartido entre UI y SQL.
- Conexion global unica sin abstraccion ni aislamiento por operacion.
- Credenciales de base codificadas en fuente.
- Mezcla de responsabilidades en `Configuracion` y `Conexion`.
- Dependencia fuerte de archivo local y rutas relativas en tiempo de ejecucion.
