# Flujo Principal del Sistema

## 1. Arranque

Punto de entrada:

- `Ventanas.VentanaInicio.main`

Secuencia:

1. Se crea `VentanaInicio`.
2. `initComponents()` construye la UI.
3. Se registra la fuente de codigo de barras.
4. `Configuracion.leerArchivoDePropiedades()` carga parametros.
5. Se precarga `informacion` en pantalla.
6. Se configuran acciones de teclado, botones y cierre.
7. `Conexion.ConectarBDEmpresa()` abre conexion Firebird.
8. `Conexion.tieneLicenciavalida()` valida rango de licencia.

## 2. Consulta por Codigo de Barras

Disparadores:

- `Enter` en `codigoBarras`
- `Enter` en `informacion`

Secuencia:

1. `consultarArticulo()` toma el codigo capturado.
2. Si esta vacio, limpia la UI y muestra estado neutral.
3. Si tiene valor, llama a `SQL.SQLArticulo.buscarArticuloPorCodigoBarra(codigo, "8")`.
4. `SQLArticulo` ejecuta query sobre `PRODUCTOS` e `INVENTARIO_BALANCES`.
5. Si encuentra resultado, carga campos estaticos del articulo.
6. `VentanaInicio.actualizarDesdeArticuloActual()` transforma el precio y actualiza la vista.
7. Si no encuentra resultado, limpia datos y muestra error visual + toast.

Salida visible:

- descripcion
- presentacion
- existencia/stock
- precio formateado

## 3. Busqueda por Descripcion

Disparador:

- boton "Buscar productos"
- `Ctrl + F7`

Secuencia:

1. Se abre `BusquedaDialog`.
2. El usuario escribe descripcion y presiona `Enter`.
3. `SQL.SQLArticulo.buscarArticuloPorDescripcion(...)` devuelve una lista.
4. La lista se carga en `JTable`.
5. El usuario selecciona una fila con doble clic o `Enter`.
6. El dialogo llama `ownerFrame.cargarArticuloDesdeBusqueda(codigo)`.
7. `VentanaInicio` reutiliza el flujo normal de consulta por codigo.

## 4. Configuracion

Disparador:

- boton "Configuracion"
- `Ctrl + F9`

Secuencia:

1. Se abre `ConfiguracionWindow`.
2. `ConfigManager.loadProperties()` lee el archivo actual.
3. Se muestran ambiente, formato, clave, impresora, informacion e IP.
4. Al guardar, `ConfigManager.saveConfiguration(...)` persiste cambios.
5. `Configuracion.leerArchivoDePropiedades()` recarga estado estatico.
6. `VentanaInicio.aplicarConfiguracionActual()` refresca informacion en la vista.

## 5. Impresion de Etiqueta

Disparador:

- boton "Imprimir etiqueta"
- `Ctrl + F8`

Precondicion:

- debe existir un articulo cargado con codigo de barras.

Secuencia:

1. `imprimirEtiqueta()` valida que exista `SQL.SQLArticulo.codigo_barras`.
2. Procesa el codigo para Code128/EAN.
3. Genera una imagen del codigo de barras usando la fuente `code128.ttf`.
4. Construye un `DatosReporte` con descripcion, precio, fecha e informacion.
5. Compila `reportes/EtiquetaPrecio.jrxml`.
6. Llena el reporte con `JRBeanCollectionDataSource`.
7. Segun `Configuracion.ambiente`:
   - `a`: abre vista previa Jasper.
   - `b`: imprime directo en la impresora configurada.

## 6. Cierre

Disparadores:

- tecla `Esc`
- cierre de ventana principal

Secuencia:

1. Se pide confirmacion con `JOptionPane`.
2. Si el usuario acepta, la aplicacion termina con `System.exit(0)`.

## Flujo Resumido

```text
Inicio
  -> leer configuracion
  -> conectar a Firebird
  -> validar licencia
  -> consultar articulo
      -> mostrar resultado
      -> buscar por descripcion (opcional)
      -> imprimir etiqueta (opcional)
      -> abrir configuracion (opcional)
  -> salir
```
