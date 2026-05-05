package infraestructura;

import Metodos.Articulos;
import aplicacion.ArticuloRepository;
import aplicacion.PrecioImpuestoCalculator;
import dominio.ArticuloDetalle;
import dominio.DesglosePrecio;
import dominio.ImpuestoDefinicion;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;

public final class SqlArticuloRepository implements ArticuloRepository {

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

    private final FirebirdConnectionFactory connectionFactory = new FirebirdConnectionFactory();

    @Override
    public ArticuloDetalle buscarPorCodigoBarras(String codigoBarras, String tarifa) {
        try (Connection connection = connectionFactory.openConnection();
                PreparedStatement statement = connection.prepareStatement(
                        SELECT_PRODUCTO_BASE + " WHERE A.CODIGO = ?")) {
            statement.setString(1, codigoBarras);
            ResultSet result = statement.executeQuery();
            try {
                if (!result.next()) {
                    return null;
                }

                String precioIva = result.getString("PRECIO_IVA");
                String impuestosCsv = result.getString("IMPUESTOS");
                List<ImpuestoDefinicion> impuestos = obtenerImpuestosPorCadena(connection, impuestosCsv);
                DesglosePrecio desglose = PrecioImpuestoCalculator.calcular(
                        parsearPrecio(precioIva),
                        impuestos);

                return new ArticuloDetalle(
                        result.getString("CODIGO"),
                        result.getString("CODIGO_BARRAS"),
                        result.getString("IDENTIFICACION"),
                        result.getString("DESCRIPCION"),
                        result.getString("PRESENTACION"),
                        stockSeguro(result.getString("STOCK")),
                        precioIva,
                        impuestosCsv,
                        desglose);
            } finally {
                result.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            JOptionPane.showMessageDialog(null,
                    "PROBLEMAS....",
                    "SQL, ARTICULO",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }

    @Override
    public List<Articulos> buscarPorDescripcion(String descripcion) {
        ArrayList<Articulos> articulos = new ArrayList<Articulos>();
        try (Connection connection = connectionFactory.openConnection();
                PreparedStatement statement = connection.prepareStatement(
                        SELECT_PRODUCTO_BASE
                        + " WHERE UPPER(A.DESCRIPCION) LIKE UPPER(?)"
                        + " ORDER BY A.DESCRIPCION")) {
            statement.setString(1, descripcion + "%");
            ResultSet result = statement.executeQuery();
            try {
                while (result.next()) {
                    articulos.add(new Articulos(
                            result.getString("CODIGO"),
                            result.getString("CODIGO_BARRAS"),
                            result.getString("IDENTIFICACION"),
                            result.getString("DESCRIPCION"),
                            stockSeguro(result.getString("STOCK")),
                            result.getString("PRESENTACION"),
                            result.getString("PRECIO_IVA")));
                }
            } finally {
                result.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            JOptionPane.showMessageDialog(null,
                    "PROBLEMAS....",
                    "SQL, ARTICULO",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        return articulos;
    }

    private List<ImpuestoDefinicion> obtenerImpuestosPorCadena(Connection connection, String impuestosCsv)
            throws SQLException {
        if (impuestosCsv == null || impuestosCsv.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<ImpuestoDefinicion> impuestosEncontrados = new ArrayList<ImpuestoDefinicion>();
        for (String impuestoId : impuestosCsv.split(",")) {
            String idNormalizado = impuestoId == null ? "" : impuestoId.trim();
            if (idNormalizado.isEmpty()) {
                continue;
            }

            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM IMPUESTOS WHERE ID = ?")) {
                statement.setString(1, idNormalizado);
                ResultSet result = statement.executeQuery();
                try {
                    if (result.next()) {
                        impuestosEncontrados.add(new ImpuestoDefinicion(
                                idNormalizado,
                                resolverNombreImpuesto(result),
                                resolverPorcentajeImpuesto(result)));
                    }
                } finally {
                    result.close();
                }
            }
        }
        return impuestosEncontrados;
    }

    private String resolverNombreImpuesto(ResultSet result) throws SQLException {
        String nombre = obtenerTextoColumna(result, "DESCRIPCION", "NOMBRE", "IMPUESTO", "DETALLE", "CONCEPTO");
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Impuesto";
        }
        return nombre.trim();
    }

    private BigDecimal resolverPorcentajeImpuesto(ResultSet result) throws SQLException {
        BigDecimal porcentaje = obtenerDecimalColumna(result, "PORCENTAJE", "VALOR", "TASA", "TARIFA", "IMPORTE");
        return porcentaje == null ? BigDecimal.ZERO : porcentaje;
    }

    private String obtenerTextoColumna(ResultSet result, String... candidatos) throws SQLException {
        String columna = buscarColumnaPorNombre(result, false, candidatos);
        if (columna != null) {
            return result.getString(columna);
        }

        ResultSetMetaData metaData = result.getMetaData();
        int columnas = metaData.getColumnCount();
        for (int i = 1; i <= columnas; i++) {
            if (esColumnaTexto(metaData.getColumnType(i)) && !"ID".equalsIgnoreCase(metaData.getColumnLabel(i))) {
                return result.getString(i);
            }
        }
        return null;
    }

    private BigDecimal obtenerDecimalColumna(ResultSet result, String... candidatos) throws SQLException {
        String columna = buscarColumnaPorNombre(result, true, candidatos);
        if (columna != null) {
            return result.getBigDecimal(columna);
        }

        ResultSetMetaData metaData = result.getMetaData();
        int columnas = metaData.getColumnCount();
        for (int i = 1; i <= columnas; i++) {
            if ("ID".equalsIgnoreCase(metaData.getColumnLabel(i))) {
                continue;
            }
            if (esColumnaNumerica(metaData.getColumnType(i))) {
                return result.getBigDecimal(i);
            }
        }
        return null;
    }

    private String buscarColumnaPorNombre(ResultSet result, boolean numerica, String... candidatos)
            throws SQLException {
        ResultSetMetaData metaData = result.getMetaData();
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

    private boolean coincideNombreColumna(String candidato, String valorColumna) {
        if (candidato == null || valorColumna == null) {
            return false;
        }

        String candidatoNormalizado = candidato.trim().toUpperCase();
        String columnaNormalizada = valorColumna.trim().toUpperCase();
        return candidatoNormalizado.equals(columnaNormalizada)
                || columnaNormalizada.contains(candidatoNormalizado);
    }

    private boolean esColumnaNumerica(int tipoSql) {
        return tipoSql == Types.DECIMAL
                || tipoSql == Types.NUMERIC
                || tipoSql == Types.DOUBLE
                || tipoSql == Types.FLOAT
                || tipoSql == Types.REAL
                || tipoSql == Types.INTEGER
                || tipoSql == Types.SMALLINT
                || tipoSql == Types.BIGINT;
    }

    private boolean esColumnaTexto(int tipoSql) {
        return tipoSql == Types.CHAR
                || tipoSql == Types.VARCHAR
                || tipoSql == Types.LONGVARCHAR;
    }

    private String stockSeguro(String stock) {
        return (stock == null || stock.trim().isEmpty()) ? "Sin registro" : stock;
    }

    private BigDecimal parsearPrecio(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(valor.trim());
    }
}
