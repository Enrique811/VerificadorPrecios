package infraestructura;

import Metodos.ConfigManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class FirebirdConnectionFactory {

    private static final String DRIVER = "org.firebirdsql.jdbc.FBDriver";
    private static final String DEFAULT_USER = "SYSDBA";
    private static final String DEFAULT_PASSWORD = "masterkey";
    private static final String CHARSET_SUFFIX = "?lc_ctype=ISO8859_1";

    public Connection openConnection() throws SQLException {
        return openConnection(new ConfigManager());
    }

    public Connection openConnection(ConfigManager configManager) throws SQLException {
        if (configManager == null) {
            throw new SQLException("La configuracion esta incompleta.");
        }

        String ipEmpresa = valueOrEmpty(configManager.get("ipEmpresa")).trim();
        String rutaEmpresa = normalizeDatabasePath(configManager.get("rutaEmpresa"));
        String usuario = resolveConfiguredValue(configManager.get("usuario"), DEFAULT_USER);
        String password = configManager.get("password");
        if (password == null || password.isEmpty()) {
            password = DEFAULT_PASSWORD;
        }

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("No fue posible cargar el controlador Firebird.", ex);
        }

        String url = buildUrl(ipEmpresa, rutaEmpresa);
        return DriverManager.getConnection(url, usuario, password);
    }

    public String buildUrl(ConfigManager configManager) {
        if (configManager == null) {
            return "";
        }
        return buildUrl(valueOrEmpty(configManager.get("ipEmpresa")).trim(),
                normalizeDatabasePath(configManager.get("rutaEmpresa")));
    }

    private String buildUrl(String ipEmpresa, String rutaEmpresa) {
        return "jdbc:firebirdsql://" + ipEmpresa + "/" + rutaEmpresa + CHARSET_SUFFIX;
    }

    private String resolveConfiguredValue(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }

    private String normalizeDatabasePath(String rutaEmpresa) {
        if (rutaEmpresa == null) {
            return "";
        }
        return rutaEmpresa.trim().replace('\\', '/');
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
