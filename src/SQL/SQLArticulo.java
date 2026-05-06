/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQL;

import Conexion.Conexion;
import Metodos.Articulos;
import Metodos.CalculadoraImpuestos;
import Metodos.Configuracion;
import Metodos.DetalleImpuesto;
import Metodos.ImpuestoInfo;
import Metodos.ResultadoCalculoImpuestos;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrador
 */
public class SQLArticulo {

    public static String codigo;
    public static String codigo_barras;
    public static String identificacion;
    public static String descripcion;
    public static String presentacion;
    public static String formato;
    public static BigDecimal precio_venta_base;
    public static BigDecimal precio_venta_final;
    public static String impuestos;
    public static String desglose_impuestos;
    public static String familia;
    public static boolean controlConsulta;

    public static String precioESpecial;

    private static final String SELECT_PRODUCTO_BASE
            = "SELECT"
            + " A.ID AS CODIGO,"
            + " A.ID AS IDENTIFICACION,"
            + " A.CODIGO AS CODIGO_BARRAS,"
            + " A.DESCRIPCION AS DESCRIPCION,"
            + " A.TVENTA AS FORMATO,"
            + " A.TVENTA AS PRESENTACION,"
            + " A.PVENTA AS PRECIO_BASE,"
            + " A.IMPUESTOS AS IMPUESTOS,"
            + " COALESCE(B.CANTIDAD_ACTUAL, 0) AS STOCK"
            + " FROM PRODUCTOS A"
            + " LEFT JOIN INVENTARIO_BALANCES B ON A.ID = B.PRODUCTO_ID";

    private static String stockSeguro(String stock) {
        return (stock == null || stock.trim().isEmpty()) ? "Sin registro" : stock;
    }

    private static void cargarArticuloDesdeResultado() throws SQLException {
        codigo = Conexion.resultado.getString("CODIGO");
        codigo_barras = Conexion.resultado.getString("CODIGO_BARRAS");
        identificacion = Conexion.resultado.getString("IDENTIFICACION");
        descripcion = Conexion.resultado.getString("DESCRIPCION");
        formato = stockSeguro(Conexion.resultado.getString("STOCK"));
        presentacion = Conexion.resultado.getString("PRESENTACION");
        precio_venta_base = obtenerPrecioBase(Conexion.resultado.getBigDecimal("PRECIO_BASE"));
        impuestos = Conexion.resultado.getString("IMPUESTOS");
        ResultadoCalculoImpuestos resultado = calcularPrecioConImpuestos(precio_venta_base, impuestos);
        precio_venta_final = resultado.getPrecioFinal();
        desglose_impuestos = construirDesgloseVisible(resultado);
    }

    public static void buscarArticuloPorCodigoBarra(String cod_barras, String tarifa) {
        controlConsulta = false;
        try {
            Conexion.ConectarBDEmpresa();
            Conexion.cerrarRecursosConsulta();
            Conexion.consulta = SELECT_PRODUCTO_BASE + " WHERE A.CODIGO = ?";
            System.out.println("" + Conexion.consulta);
            Conexion.preparacion = Conexion.conexion.prepareStatement(Conexion.consulta);
            Conexion.preparacion.setString(1, cod_barras);
            Conexion.resultado = Conexion.preparacion.executeQuery();

            if (Conexion.resultado.next()) {
                cargarArticuloDesdeResultado();
                controlConsulta = true;
            } else {
                controlConsulta = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(Conexion.PanelMensaje,
                    "PROBLEMAS....",
                    "SQL, ARTICULO",
                    JOptionPane.INFORMATION_MESSAGE);
        } finally {
            Conexion.cerrarRecursosConsulta();
        }
    }

    public static void buscarArticuloPorCodigoBarraAuxiliar(String cod_barras, String tarifa) {
        controlConsulta = false;
        try {

            Conexion.ConectarBDEmpresa();
            Conexion.cerrarRecursosConsulta();
            Conexion.consulta = "SELECT"
                    + " A.CODIGO,A.IDENTIFICACION,A.CODIGO_BARRAS,A.DESCRIPCION,A.FAMILIA,A.SUBFAMILIA,"
                    + " A.MARCA,A.FORMATO,A.LIBRE1 AS PRESENTACION,A.STOCK,A.TIPO_IVA,A.TIPO_IEPS,A.PRECIO_COSTE,A.PRECIO_COSTE2,"
                    + " A.FECHA_ALTA,A.PIEZASCAJA,A.GRUPO_COMISION,IVA.IVA,IEPS.IEPS,LIN.PRECIO_VENTA,LIN.PRECIO_IVA,LIN.CODIGO_TARIFA AS TARIFA"
                    + " FROM LINTARIF LIN"
                    + " INNER JOIN ARTICULO A ON A.CODIGO=LIN.CODIGO_ARTICULO"
                    + " INNER JOIN TIPOSIVA IVA ON IVA.CODIGO=A.TIPO_IVA"
                    + " INNER JOIN TIPOSIEPS IEPS ON IEPS.CODIGO=A.TIPO_IEPS"
                    + " INNER JOIN COD_BARRAS AUX ON AUX.ARTICULO=A.CODIGO"
                    + " WHERE AUX.CODIGO_BARRAS = ? AND LIN.CODIGO_TARIFA = ?";
            System.out.println("" + Conexion.consulta);
            Conexion.preparacion = Conexion.conexion.prepareStatement(Conexion.consulta);
            Conexion.preparacion.setString(1, cod_barras);
            Conexion.preparacion.setString(2, tarifa);
            Conexion.resultado = Conexion.preparacion.executeQuery();

            if (Conexion.resultado.next()) {
                codigo = Conexion.resultado.getString("CODIGO");
                codigo_barras = Conexion.resultado.getString("CODIGO_BARRAS");
                identificacion = Conexion.resultado.getString("IDENTIFICACION");
                descripcion = Conexion.resultado.getString("DESCRIPCION");
                formato = stockSeguro(Conexion.resultado.getString("STOCK"));
                presentacion = Conexion.resultado.getString("PRESENTACION");
                precio_venta_base = obtenerPrecioBase(Conexion.resultado.getBigDecimal("PRECIO_IVA"));
                impuestos = null;
                precio_venta_final = precio_venta_base;
                desglose_impuestos = "";
                controlConsulta = true;
            } else {
                controlConsulta = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(Conexion.PanelMensaje,
                    "PROBLEMAS....",
                    "SQL, ARTICULO",
                    JOptionPane.INFORMATION_MESSAGE);
        } finally {
            Conexion.cerrarRecursosConsulta();
        }
    }

    public static ArrayList<Articulos> buscarArticuloPorDescripcion(String descrip) {
        ArrayList<Articulos> articulo = new ArrayList<>();

        controlConsulta = false;
        try {
            Conexion.ConectarBDEmpresa();
            Conexion.cerrarRecursosConsulta();
            Conexion.consulta = SELECT_PRODUCTO_BASE
                    + " WHERE UPPER(A.DESCRIPCION) LIKE UPPER(?)"
                    + " ORDER BY A.DESCRIPCION";

            System.out.println("" + Conexion.consulta);
            Conexion.preparacion = Conexion.conexion.prepareStatement(Conexion.consulta);
            Conexion.preparacion.setString(1, descrip + "%");
            Conexion.resultado = Conexion.preparacion.executeQuery();

            ArrayList<FilaArticuloBusqueda> filas = new ArrayList<FilaArticuloBusqueda>();
            Set<String> idsImpuestos = new HashSet<String>();

            while (Conexion.resultado.next()) {
                BigDecimal precioBase = obtenerPrecioBase(Conexion.resultado.getBigDecimal("PRECIO_BASE"));
                String impuestosArticulo = Conexion.resultado.getString("IMPUESTOS");
                List<String> idsFila = parsearIdsImpuestos(impuestosArticulo);

                filas.add(new FilaArticuloBusqueda(
                        Conexion.resultado.getString("CODIGO"),
                        Conexion.resultado.getString("CODIGO_BARRAS"),
                        Conexion.resultado.getString("IDENTIFICACION"),
                        Conexion.resultado.getString("DESCRIPCION"),
                        stockSeguro(Conexion.resultado.getString("STOCK")),
                        Conexion.resultado.getString("PRESENTACION"),
                        precioBase,
                        idsFila));
                idsImpuestos.addAll(idsFila);
            }

            Conexion.cerrarRecursosConsulta();
            Map<String, ImpuestoInfo> impuestosDisponibles = SQLImpuesto.consultarImpuestosPorIds(
                    new ArrayList<String>(idsImpuestos));

            for (int i = 0; i < filas.size(); i++) {
                FilaArticuloBusqueda fila = filas.get(i);
                ResultadoCalculoImpuestos resultadoCalculo = CalculadoraImpuestos.calcular(
                        fila.precioBase, fila.idsImpuestos, impuestosDisponibles);
                articulo.add(new Articulos(
                        fila.codigo,
                        fila.codigoBarras,
                        fila.identificacion,
                        fila.descripcion,
                        fila.stock,
                        fila.presentacion,
                        resultadoCalculo.getPrecioFinal(),
                        construirDesgloseVisible(resultadoCalculo)));
                controlConsulta = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(Conexion.PanelMensaje,
                    "PROBLEMAS....",
                    "SQL, ARTICULO",
                    JOptionPane.INFORMATION_MESSAGE);
        } finally {
            Conexion.cerrarRecursosConsulta();
        }
        return articulo;
    }

    public static void buscarPrecioEspecialArticulo(String articulo, String tarifa, String fecha_actual) {
        controlConsulta = false;
        try {
            Configuracion.leerArchivoDePropiedades();
            Conexion.ConectarBDEmpresa();
            Conexion.cerrarRecursosConsulta();
            Conexion.consulta = "SELECT PRECIO_IVA"
                    + " FROM PRECIOS_ESPECIALES"
                    + " WHERE ARTICULO = ?"
                    + " AND TARIFA = ?"
                    + " AND FECHA_INI <= ?"
                    + " AND ? < FECHA_FIN";

            Conexion.preparacion = Conexion.conexion.prepareStatement(Conexion.consulta);
            Conexion.preparacion.setString(1, articulo);
            Conexion.preparacion.setString(2, tarifa);
            Conexion.preparacion.setString(3, fecha_actual);
            Conexion.preparacion.setString(4, fecha_actual);
            Conexion.resultado = Conexion.preparacion.executeQuery();

            if (Conexion.resultado.next()) {
                precioESpecial = Conexion.resultado.getString(1);

                controlConsulta = true;
            } else {
                controlConsulta = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(Conexion.PanelMensaje,
                    "PROBLEMAS....",
                    "SQL, ARTICULO",
                    JOptionPane.INFORMATION_MESSAGE);
        } finally {
            Conexion.cerrarRecursosConsulta();
        }
    }

    private static BigDecimal obtenerPrecioBase(BigDecimal precioBase) {
        return precioBase == null ? BigDecimal.ZERO : precioBase;
    }

    private static ResultadoCalculoImpuestos calcularPrecioConImpuestos(BigDecimal precioBase, String impuestosTexto)
            throws SQLException {
        List<String> idsImpuestos = parsearIdsImpuestos(impuestosTexto);
        if (idsImpuestos.isEmpty()) {
            return new ResultadoCalculoImpuestos(precioBase, precioBase, new ArrayList<DetalleImpuesto>());
        }

        Map<String, ImpuestoInfo> impuestosDisponibles = SQLImpuesto.consultarImpuestosPorIds(idsImpuestos);
        return CalculadoraImpuestos.calcular(precioBase, idsImpuestos, impuestosDisponibles);
    }

    private static List<String> parsearIdsImpuestos(String impuestosTexto) {
        List<String> ids = new ArrayList<String>();
        if (impuestosTexto == null || impuestosTexto.trim().isEmpty()) {
            return ids;
        }

        String[] partes = impuestosTexto.split(",");
        for (int i = 0; i < partes.length; i++) {
            String valor = partes[i] == null ? "" : partes[i].trim();
            if (!valor.isEmpty() && valor.matches("\\d+")) {
                ids.add(valor);
            }
        }
        return ids;
    }

    private static String construirDesgloseVisible(ResultadoCalculoImpuestos resultado) {
        if (resultado == null || !resultado.tieneDesglose()) {
            return "";
        }

        StringBuilder texto = new StringBuilder();
        List<DetalleImpuesto> detalles = resultado.getDesglose();
        for (int i = 0; i < detalles.size(); i++) {
            DetalleImpuesto detalle = detalles.get(i);
            if (i > 0) {
                texto.append("\n\n");
            }
            texto.append(detalle.getNombre() == null || detalle.getNombre().trim().isEmpty()
                    ? "Impuesto " + detalle.getImpuestoId()
                    : detalle.getNombre().trim());
            texto.append("\nAntes: ").append(Metodos.PrecioFormatter.formatearPrecio(detalle.getSubtotalAntes()));
            texto.append("\nPorcentaje: ").append(detalle.getPorcentajeAplicado().stripTrailingZeros().toPlainString()).append("%");
            texto.append("\nImpuesto: ").append(Metodos.PrecioFormatter.formatearPrecio(detalle.getMontoImpuesto()));
            texto.append("\nDespues: ").append(Metodos.PrecioFormatter.formatearPrecio(detalle.getSubtotalDespues()));
        }
        return texto.toString();
    }

    private static final class FilaArticuloBusqueda {

        private final String codigo;
        private final String codigoBarras;
        private final String identificacion;
        private final String descripcion;
        private final String stock;
        private final String presentacion;
        private final BigDecimal precioBase;
        private final List<String> idsImpuestos;

        private FilaArticuloBusqueda(String codigo, String codigoBarras, String identificacion,
                String descripcion, String stock, String presentacion, BigDecimal precioBase,
                List<String> idsImpuestos) {
            this.codigo = codigo;
            this.codigoBarras = codigoBarras;
            this.identificacion = identificacion;
            this.descripcion = descripcion;
            this.stock = stock;
            this.presentacion = presentacion;
            this.precioBase = precioBase;
            this.idsImpuestos = new ArrayList<String>(idsImpuestos);
        }
    }

}
