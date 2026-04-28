package Conexion;

import Metodos.ConfigManager;
import Metodos.Configuracion;
import static Metodos.Configuracion.decryptDates;
import static SQL.SQLFechaHora.obtenerFechayHoraActualDelServidorDate;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Conexion {

    private static final String DRIVER = "org.firebirdsql.jdbc.FBDriver";
    private static final String USER = "SYSDBA";
    private static final String PASSWORD = "masterkey";
    private static final String CHARSET_SUFFIX = "?lc_ctype=ISO8859_1";
    private static final String CONNECTION_ERROR_MESSAGE = "ERROR AL CONECTARSE CON LA BASE DE DATOS ... EL PROGRAMA FINALIZARA, Y EJECUTE NUEVAMENTE   ";
    private static final String LICENSE_EXPIRED_MESSAGE = "LA LICENCIA A EXPIRADO O VERIFICAR CONFIGURACION DE FECHA Y HORA";
    private static final String LICENSE_READ_ERROR_MESSAGE = "ERROR AL LEER LICENCIA";
    private static final String LICENSE_MISSING_MESSAGE = "CAPTURE LA LICENCIA PRIMERO EN CONFIGURACION";

    public static String driver = DRIVER;
    public static String url = null;
    public static String usuario = USER;
    public static String password = PASSWORD;

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
        Date fechaHoySistema = new Date();

        if (Configuracion.clave == null || Configuracion.clave.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, LICENSE_MISSING_MESSAGE);
            return false;
        }

        try {
            SecretKey secretKey = generateFixedSecretKey(Configuracion.key);
            Date[] decryptedDates = decryptDates(Configuracion.clave, secretKey);

            boolean licenciaVigenteSistema = isFechaDentroDelRango(fechaHoySistema, decryptedDates[0], decryptedDates[1]);

            if (!licenciaVigenteSistema) {
                mostrarErrorFatal(LICENSE_EXPIRED_MESSAGE + Configuracion.rutaEmpresa);
                System.exit(0);
            }

            Date fechaHoyServidor = obtenerFechayHoraActualDelServidorDate();
            boolean licenciaVigenteServidor = fechaHoyServidor != null
                    && isFechaDentroDelRango(fechaHoyServidor, decryptedDates[0], decryptedDates[1]);

            if (!licenciaVigenteServidor) {
                mostrarErrorFatal(LICENSE_EXPIRED_MESSAGE + Configuracion.rutaEmpresa);
                System.exit(0);
            }

            imprimirRangoLicencia(decryptedDates[0], decryptedDates[1], fechaHoySistema);
            return true;
        } catch (Exception ex) {
            mostrarErrorFatal(LICENSE_READ_ERROR_MESSAGE);
            System.exit(0);
            return false;
        }
    }

    public static boolean isFechaDentroDelRango(Date fechaActual, Date fechaInicio, Date fechaFin) {
        if (fechaActual == null || fechaInicio == null || fechaFin == null) {
            return false;
        }

        return (fechaActual.after(fechaInicio) || fechaActual.equals(fechaInicio))
                && (fechaActual.before(fechaFin) || fechaActual.equals(fechaFin));
    }

    public static SecretKey generateFixedSecretKey(String clave) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] claveBytes = digest.digest(clave.getBytes());
        return new SecretKeySpec(claveBytes, "AES");
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
            return false;
        }

        String ipEmpresa = config.get("ipEmpresa").trim();
        String rutaEmpresa = config.get("rutaEmpresa").trim();
        String usuarioConfig = config.get("usuario").trim();
        String passwordConfig = config.get("password");
        String urlConexion = "jdbc:firebirdsql://" + ipEmpresa + "/" + rutaEmpresa + CHARSET_SUFFIX;

        Connection conexionPrueba = null;
        try {
            Class.forName(driver);
            conexionPrueba = DriverManager.getConnection(urlConexion, usuarioConfig, passwordConfig);
            return true;
        } catch (ClassNotFoundException | SQLException ex) {
            return false;
        } finally {
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

    private static String construirUrlConexion() {
        return "jdbc:firebirdsql://" + Configuracion.ipEmpresa + "/" + Configuracion.rutaEmpresa + CHARSET_SUFFIX;
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

    private static void imprimirRangoLicencia(Date fechaInicio, Date fechaFin, Date fechaHoy) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Fecha de inicio desencriptada: " + dateFormat.format(fechaInicio));
        System.out.println("Fecha de fin desencriptada: " + dateFormat.format(fechaFin));
        System.out.println(dateFormat.format(fechaHoy));
    }

    private static void mostrarErrorFatal(String mensaje) {
        JOptionPane.showMessageDialog(PanelMensaje, mensaje);
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
