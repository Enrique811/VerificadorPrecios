/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQL;

import Conexion.Conexion;
import Metodos.Articulos;
import Metodos.Configuracion;
import dominio.ImpuestoDefinicion;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    public static String precio_venta_iva;
    public static String impuestos;
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
            + " A.PVENTA AS PRECIO_IVA,"
            + " A.IMPUESTOS AS IMPUESTOS,"
            + " B.CANTIDAD_ACTUAL AS STOCK"
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
        precio_venta_iva = Conexion.resultado.getString("PRECIO_IVA");
        impuestos = Conexion.resultado.getString("IMPUESTOS");
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
            e.printStackTrace(System.out);
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
                precio_venta_iva = Conexion.resultado.getString("PRECIO_IVA");
                impuestos = null;
                controlConsulta = true;
            } else {
                controlConsulta = false;
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
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

            while (Conexion.resultado.next()) {
                Articulos art = new Articulos(
                        Conexion.resultado.getString("CODIGO"),
                        Conexion.resultado.getString("CODIGO_BARRAS"),
                        Conexion.resultado.getString("IDENTIFICACION"),
                        Conexion.resultado.getString("DESCRIPCION"),
                        stockSeguro(Conexion.resultado.getString("STOCK")),
                        Conexion.resultado.getString("PRESENTACION"),
                        Conexion.resultado.getString("PRECIO_IVA")
                );

                articulo.add(art);
                controlConsulta = true;
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
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
            e.printStackTrace(System.out);
            JOptionPane.showMessageDialog(Conexion.PanelMensaje,
                    "PROBLEMAS....",
                    "SQL, ARTICULO",
                    JOptionPane.INFORMATION_MESSAGE);
        } finally {
            Conexion.cerrarRecursosConsulta();
        }
    }

    public static List<ImpuestoDefinicion> obtenerImpuestosPorCadena(String impuestosCsv) {
        if (impuestosCsv == null || impuestosCsv.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<ImpuestoDefinicion> impuestosEncontrados = new ArrayList<ImpuestoDefinicion>();
        try {
            Conexion.ConectarBDEmpresa();
            for (String impuestoId : impuestosCsv.split(",")) {
                String idNormalizado = impuestoId == null ? "" : impuestoId.trim();
                if (idNormalizado.isEmpty()) {
                    continue;
                }

                Conexion.cerrarRecursosConsulta();
                Conexion.consulta = "SELECT * FROM IMPUESTOS WHERE ID = ?";
                Conexion.preparacion = Conexion.conexion.prepareStatement(Conexion.consulta);
                Conexion.preparacion.setString(1, idNormalizado);
                Conexion.resultado = Conexion.preparacion.executeQuery();

                if (Conexion.resultado.next()) {
                    impuestosEncontrados.add(new ImpuestoDefinicion(
                            idNormalizado,
                            resolverNombreImpuesto(),
                            resolverPorcentajeImpuesto()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
            JOptionPane.showMessageDialog(Conexion.PanelMensaje,
                    "PROBLEMAS AL CONSULTAR IMPUESTOS....",
                    "SQL, IMPUESTOS",
                    JOptionPane.INFORMATION_MESSAGE);
        } finally {
            Conexion.cerrarRecursosConsulta();
        }
        return impuestosEncontrados;
    }

    private static String resolverNombreImpuesto() throws SQLException {
        String nombre = obtenerTextoColumna("DESCRIPCION", "NOMBRE", "IMPUESTO", "DETALLE", "CONCEPTO");
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Impuesto";
        }
        return nombre.trim();
    }

    private static BigDecimal resolverPorcentajeImpuesto() throws SQLException {
        BigDecimal porcentaje = obtenerDecimalColumna("PORCENTAJE", "VALOR", "TASA", "TARIFA", "IMPORTE");
        return porcentaje == null ? BigDecimal.ZERO : porcentaje;
    }

    private static String obtenerTextoColumna(String... candidatos) throws SQLException {
        String columna = buscarColumnaPorNombre(false, candidatos);
        if (columna != null) {
            return Conexion.resultado.getString(columna);
        }

        ResultSetMetaData metaData = Conexion.resultado.getMetaData();
        int columnas = metaData.getColumnCount();
        for (int i = 1; i <= columnas; i++) {
            if (esColumnaTexto(metaData.getColumnType(i)) && !"ID".equalsIgnoreCase(metaData.getColumnLabel(i))) {
                return Conexion.resultado.getString(i);
            }
        }
        return null;
    }

    private static BigDecimal obtenerDecimalColumna(String... candidatos) throws SQLException {
        String columna = buscarColumnaPorNombre(true, candidatos);
        if (columna != null) {
            return Conexion.resultado.getBigDecimal(columna);
        }

        ResultSetMetaData metaData = Conexion.resultado.getMetaData();
        int columnas = metaData.getColumnCount();
        for (int i = 1; i <= columnas; i++) {
            if ("ID".equalsIgnoreCase(metaData.getColumnLabel(i))) {
                continue;
            }
            if (esColumnaNumerica(metaData.getColumnType(i))) {
                return Conexion.resultado.getBigDecimal(i);
            }
        }
        return null;
    }

    private static String buscarColumnaPorNombre(boolean numerica, String... candidatos) throws SQLException {
        ResultSetMetaData metaData = Conexion.resultado.getMetaData();
        int columnas = metaData.getColumnCount();
        for (String candidato : candidatos) {
            for (int i = 1; i <= columnas; i++) {
                String label = metaData.getColumnLabel(i);
                String nombre = metaData.getColumnName(i);
                if (!coincideNombreColumna(candidato, label) && !coincideNombreColumna(candidato, nombre)) {
                    continue;
                }
                int tipo = metaData.getColumnType(i);
                if (numerica && !esColumnaNumerica(tipo)) {
                    continue;
                }
                if (!numerica && !esColumnaTexto(tipo)) {
                    continue;
                }
                return label;
            }
        }
        return null;
    }

    private static boolean coincideNombreColumna(String candidato, String valorColumna) {
        if (candidato == null || valorColumna == null) {
            return false;
        }

        String candidatoNormalizado = candidato.trim().toUpperCase();
        String columnaNormalizada = valorColumna.trim().toUpperCase();
        return candidatoNormalizado.equals(columnaNormalizada)
                || columnaNormalizada.contains(candidatoNormalizado);
    }

    private static boolean esColumnaNumerica(int tipoSql) {
        return tipoSql == Types.DECIMAL
                || tipoSql == Types.NUMERIC
                || tipoSql == Types.DOUBLE
                || tipoSql == Types.FLOAT
                || tipoSql == Types.REAL
                || tipoSql == Types.INTEGER
                || tipoSql == Types.SMALLINT
                || tipoSql == Types.BIGINT;
    }

    private static boolean esColumnaTexto(int tipoSql) {
        return tipoSql == Types.CHAR
                || tipoSql == Types.VARCHAR
                || tipoSql == Types.LONGVARCHAR;
    }

}
