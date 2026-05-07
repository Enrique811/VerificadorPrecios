/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQL;

import Conexion.Conexion;
import Metodos.Articulos;
import Metodos.Configuracion;
import java.sql.SQLException;
import java.util.ArrayList;
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
            + " A.PFINAL AS PRECIO_IVA,"
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
                precio_venta_iva = Conexion.resultado.getString("PRECIO_IVA");
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

}
