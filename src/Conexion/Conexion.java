package Conexion;

import Metodos.ConfigManager;
import Metodos.Configuracion;
import Metodos.LicenseJsonValidator;
import Metodos.LicenseValidationResult;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Conexion {

    private static final String DRIVER = "org.firebirdsql.jdbc.FBDriver";
    private static final String USER = "SYSDBA";
    private static final String PASSWORD = "masterkey";
    private static final String CHARSET_SUFFIX = "?lc_ctype=ISO8859_1";
    private static final String CONNECTION_ERROR_MESSAGE = "ERROR AL CONECTARSE CON LA BASE DE DATOS ... EL PROGRAMA FINALIZARA, Y EJECUTE NUEVAMENTE   ";
    private static final int LOGIN_TIMEOUT_SECONDS = 5;
    private static final DateTimeFormatter LOG_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String driver = DRIVER;
    public static String url = null;
    public static String usuario = USER;
    public static String password = PASSWORD;
    private static String ultimoErrorConexion = "";

    public static Connection conexion = null;
    public static PreparedStatement preparacion = null;
    public static ResultSet resultado = null;
    public static String consulta = null;

    public static JPanel PanelMensaje = null;

    public static void ConectarBDEmpresa() {
        try {
            if (estaConexionActiva()) {
                return;
            }

            Class.forName(driver);
            usuario = obtenerUsuarioConfigurado();
            password = obtenerPasswordConfigurado();
            url = construirUrlConexion();
            conexion = DriverManager.getConnection(url, usuario, password);
            System.out.println(url);
        } catch (ClassNotFoundException | SQLException e) {
            mostrarErrorFatal(CONNECTION_ERROR_MESSAGE + Configuracion.rutaEmpresa);
            System.exit(0);
        }
    }

    public static boolean tieneLicenciavalida() {
        if (Configuracion.clave == null || Configuracion.clave.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Importe o capture la licencia JSON en configuracion.");
            return false;
        }

        LicenseValidationResult result = LicenseJsonValidator.validate(Configuracion.clave);
        imprimirDiagnosticoLicencia(result);
        if (!result.isLicenciaValida()) {
            mostrarErrorFatal(result.buildSummary());
            System.exit(0);
            return false;
        }

        Date fechaHoyServidor = result.getFechaServidor();
        if (!isFechaDentroDelRango(fechaHoyServidor, result.getFechaInicio(), result.getFechaFin())) {
            mostrarErrorFatal(result.buildSummary());
            System.exit(0);
            return false;
        }

        return true;
    }

    public static boolean isFechaDentroDelRango(Date fechaActual, Date fechaInicio, Date fechaFin) {
        if (fechaActual == null || fechaInicio == null || fechaFin == null) {
            return false;
        }

        return (fechaActual.after(fechaInicio) || fechaActual.equals(fechaInicio))
                && (fechaActual.before(fechaFin) || fechaActual.equals(fechaFin));
    }

    public static void DesconectarBDEmpresa() {
        try {
            cerrarRecursosConsulta();
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
            conexion = null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERROR AL DESCONECTARSE CON LA BASE DE DATOS ... ");
            System.exit(0);
        }
    }

    public static void cerrarRecursosConsulta() {
        cerrarResultado();
        cerrarPreparacion();
    }

    public static boolean probarConexion(ConfigManager config) {
        if (config == null || !config.configCompleta()) {
            ultimoErrorConexion = "La configuracion esta incompleta.";
            return false;
        }

        String ipEmpresa = config.get("ipEmpresa").trim();
        String rutaEmpresa = config.get("rutaEmpresa").trim();
        String usuarioConfig = config.get("usuario").trim();
        String passwordConfig = config.get("password");
        String urlConexion = buildFirebirdUrl(ipEmpresa, rutaEmpresa);

        Connection conexionPrueba = null;
        try {
            Class.forName(driver);
            DriverManager.setLoginTimeout(LOGIN_TIMEOUT_SECONDS);
            conexionPrueba = DriverManager.getConnection(urlConexion, usuarioConfig, passwordConfig);
            ultimoErrorConexion = "";
            return true;
        } catch (ClassNotFoundException | SQLException ex) {
            ultimoErrorConexion = buildUserFriendlyConnectionMessage(ex);
            registrarErrorConexion(buildTechnicalConnectionErrorMessage(urlConexion, ex), ex);
            return false;
        } finally {
            DriverManager.setLoginTimeout(0);
            if (conexionPrueba != null) {
                try {
                    conexionPrueba.close();
                } catch (SQLException ex) {
                    System.err.println("No se pudo cerrar la conexion de prueba: " + ex.getMessage());
                }
            }
        }
    }

    private static boolean estaConexionActiva() throws SQLException {
        return conexion != null && !conexion.isClosed();
    }

    public static String getUltimoErrorConexion() {
        return ultimoErrorConexion == null ? "" : ultimoErrorConexion;
    }

    private static String construirUrlConexion() {
        return buildFirebirdUrl(Configuracion.ipEmpresa, Configuracion.rutaEmpresa);
    }

    private static String buildFirebirdUrl(String ipEmpresa, String rutaEmpresa) {
        String host = ipEmpresa == null ? "" : ipEmpresa.trim();
        String databasePath = normalizeDatabasePath(rutaEmpresa);
        return "jdbc:firebirdsql://" + host + "/" + databasePath + CHARSET_SUFFIX;
    }

    private static String normalizeDatabasePath(String rutaEmpresa) {
        if (rutaEmpresa == null) {
            return "";
        }

        return rutaEmpresa.trim().replace('\\', '/');
    }

    private static String obtenerUsuarioConfigurado() {
        if (Configuracion.usuario == null || Configuracion.usuario.trim().isEmpty()) {
            return USER;
        }
        return Configuracion.usuario.trim();
    }

    private static String obtenerPasswordConfigurado() {
        if (Configuracion.password == null || Configuracion.password.isEmpty()) {
            return PASSWORD;
        }
        return Configuracion.password;
    }

    private static void imprimirDiagnosticoLicencia(LicenseValidationResult result) {
        if (result == null) {
            return;
        }
        System.out.println(result.buildSummary());
    }

    private static void mostrarErrorFatal(String mensaje) {
        JOptionPane.showMessageDialog(PanelMensaje, mensaje);
    }

    private static String buildUserFriendlyConnectionMessage(Exception ex) {
        if (ex instanceof ClassNotFoundException) {
            return "No fue posible iniciar el controlador de base de datos.";
        }

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
        if (normalized.contains(".fdb") || normalized.contains("file") || normalized.contains("path")) {
            return "La ruta de la base de datos no es valida o no esta disponible.";
        }

        return "No fue posible establecer la conexion.";
    }

    private static String buildTechnicalConnectionErrorMessage(String urlConexion, Exception ex) {
        String message = ex.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = ex.getClass().getName();
        }
        return "URL: " + sanitizeConnectionUrl(urlConexion) + " | Error: " + message;
    }

    private static String sanitizeConnectionUrl(String urlConexion) {
        if (urlConexion == null || urlConexion.trim().isEmpty()) {
            return "(sin URL)";
        }

        int protocolSeparator = urlConexion.indexOf("://");
        int firstSlashAfterHost = protocolSeparator >= 0
                ? urlConexion.indexOf('/', protocolSeparator + 3)
                : -1;
        int querySeparator = urlConexion.indexOf('?');

        if (firstSlashAfterHost < 0) {
            return urlConexion;
        }

        String prefix = urlConexion.substring(0, firstSlashAfterHost + 1);
        String suffix = querySeparator >= 0 ? urlConexion.substring(querySeparator) : "";
        return prefix + "***" + suffix;
    }

    private static void registrarErrorConexion(String resumen, Exception ex) {
        try {
            Path logPath = resolveConnectionLogPath();
            Files.createDirectories(logPath.getParent());

            StringWriter stackTraceWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stackTraceWriter);
            try {
                ex.printStackTrace(printWriter);
            } finally {
                printWriter.close();
            }

            String contenido = "[" + LocalDateTime.now().format(LOG_TIMESTAMP) + "] "
                    + resumen + System.lineSeparator()
                    + stackTraceWriter.toString()
                    + System.lineSeparator();

            Files.write(logPath,
                    contenido.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException ioEx) {
            System.err.println("No se pudo registrar el error de conexion: " + ioEx.getMessage());
        }
    }

    private static Path resolveConnectionLogPath() {
        File configFile = new File(ConfigManager.getConfigPath());
        File configDir = configFile.getParentFile();
        if (configDir == null) {
            configDir = new File(System.getProperty("user.dir"));
        }
        return new File(new File(configDir, "logs"), "conexion.log").toPath();
    }

    private static void cerrarResultado() {
        try {
            if (resultado != null && !resultado.isClosed()) {
                resultado.close();
            }
        } catch (SQLException e) {
            System.err.println("No se pudo cerrar ResultSet: " + e.getMessage());
        } finally {
            resultado = null;
        }
    }

    private static void cerrarPreparacion() {
        try {
            if (preparacion != null && !preparacion.isClosed()) {
                preparacion.close();
            }
        } catch (SQLException e) {
            System.err.println("No se pudo cerrar PreparedStatement: " + e.getMessage());
        } finally {
            preparacion = null;
        }
    }
}
