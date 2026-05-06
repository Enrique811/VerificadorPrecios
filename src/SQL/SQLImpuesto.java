package SQL;

import Conexion.Conexion;
import Metodos.ImpuestoInfo;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SQLImpuesto {

    private SQLImpuesto() {
    }

    public static Map<String, ImpuestoInfo> consultarImpuestosPorIds(List<String> ids) throws SQLException {
        Map<String, ImpuestoInfo> impuestos = new LinkedHashMap<String, ImpuestoInfo>();
        if (ids == null || ids.isEmpty()) {
            return impuestos;
        }

        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                inClause.append(",");
            }
            inClause.append("?");
        }

        String consulta = "SELECT ID, NOMBRE, PORCENTAJE FROM IMPUESTOS WHERE ID IN (" + inClause.toString() + ")";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = Conexion.conexion.prepareStatement(consulta);
            for (int i = 0; i < ids.size(); i++) {
                statement.setString(i + 1, ids.get(i));
            }
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("ID");
                impuestos.put(id, new ImpuestoInfo(
                        id,
                        resultSet.getString("NOMBRE"),
                        resultSet.getBigDecimal("PORCENTAJE") == null
                                ? BigDecimal.ZERO : resultSet.getBigDecimal("PORCENTAJE")));
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }

        return impuestos;
    }
}
