package infraestructura;

import Metodos.ConfigManager;
import aplicacion.conexion.ConnectionCheckResult;
import aplicacion.conexion.ConnectionValidator;
import java.sql.Connection;
import java.sql.SQLException;

public final class LegacyConnectionValidator implements ConnectionValidator {

    private static final int LOGIN_TIMEOUT_SECONDS = 5;

    private final FirebirdConnectionFactory connectionFactory = new FirebirdConnectionFactory();

    @Override
    public ConnectionCheckResult check(ConfigManager configManager) {
        if (configManager == null || !configManager.configCompleta()) {
            return new ConnectionCheckResult(false, "La configuracion esta incompleta.");
        }

        Connection connection = null;
        try {
            java.sql.DriverManager.setLoginTimeout(LOGIN_TIMEOUT_SECONDS);
            connection = connectionFactory.openConnection(configManager);
            return new ConnectionCheckResult(true, "");
        } catch (SQLException ex) {
            return new ConnectionCheckResult(false,
                    buildUserFriendlyConnectionMessage(ex, connectionFactory.buildUrl(configManager)));
        } finally {
            java.sql.DriverManager.setLoginTimeout(0);
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println("No se pudo cerrar la conexion de prueba: " + ex.getMessage());
                }
            }
        }
    }

    private String buildUserFriendlyConnectionMessage(SQLException ex, String urlConexion) {
        String message = ex.getMessage();
        if (message == null) {
            return "No fue posible establecer la conexion.";
        }

        String normalized = message.toLowerCase();
        if (normalized.contains("timeout")) {
            return "La conexion tardo demasiado en responder.";
        }
        if (normalized.contains("password") || normalized.contains("login")
                || normalized.contains("authentication") || normalized.contains("usuario")) {
            return "El usuario o la contrasena no son validos.";
        }
        if (normalized.contains("connection refused") || normalized.contains("unable to complete network request")
                || normalized.contains("network")) {
            return "No fue posible comunicarse con el servidor.";
        }
        if (normalized.contains(".fdb") || normalized.contains("file") || normalized.contains("path")
                || urlConexion.toLowerCase().contains(".fdb")) {
            return "La ruta de la base de datos no es valida o no esta disponible.";
        }

        return "No fue posible establecer la conexion.";
    }
}
