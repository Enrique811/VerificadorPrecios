package Metodos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Properties;

public final class ConfigManager {

    private static final String APPDATA_DIR = resolveAppDataDirectory();
    private static final String CONFIG_PATH = APPDATA_DIR + File.separator + "configuracion.properties";
    private static final String CONTACT_FILE_NAME = "contacto.properties";
    private static final String CONTACT_PATH = APPDATA_DIR + File.separator + CONTACT_FILE_NAME;
    private static final String DEFAULT_CONTACT_CONTENT = "correo=\n";
    private static final String DEFAULT_CONFIG_CONTENT = "# Base configuration\n"
            + "ambiente=YQ\\=\\=\n"
            + "clave=\n"
            + "formatoPrecio=TVg\\=\\=\n"
            + "impresora=\n"
            + "informacion=\n"
            + "ipEmpresa=bG9jYWxob3N0\n"
            + "password=bWFzdGVya2V5\n"
            + "reporte=\n"
            + "rutaEmpresa=\n"
            + "usuario=U1lTREJB\n";
    public static final String DEFAULT_USUARIO = "SYSDBA";
    public static final String DEFAULT_PASSWORD = "masterkey";
    private final Properties properties = new Properties();

    public ConfigManager() {
        reload();
    }

    public static Properties loadProperties() throws IOException {
        ensureConfigFileExists();
        Properties rawProperties = loadRawProperties();
        Properties decodedProperties = new Properties();
        for (String key : rawProperties.stringPropertyNames()) {
            String value = rawProperties.getProperty(key, "");
            decodedProperties.setProperty(key, decodeBase64(value));
        }
        return decodedProperties;
    }

    public static void saveInformation(String informacion) throws IOException {
        ensureConfigFileExists();
        Properties current = loadProperties();
        current.setProperty("informacion", informacion);
        storeProperties(current);
    }

    public boolean exists() {
        return new File(CONFIG_PATH).exists();
    }

    public boolean configCompleta() {
        return hasValue("ipEmpresa")
                && hasValue("usuario")
                && hasValue("password")
                && hasValue("rutaEmpresa");
    }

    public String get(String key) {
        return properties.getProperty(key, "");
    }

    public void set(String key, String value) {
        properties.setProperty(key, value == null ? "" : value.trim());
    }

    public void save() throws IOException {
        storeProperties(properties);
    }

    public void reload() {
        properties.clear();
        if (!exists()) {
            try {
                ensureConfigFileExists();
            } catch (IOException ex) {
                properties.clear();
                return;
            }
        }
        try {
            ensureWritableConfigFile();
            properties.putAll(loadProperties());
            migrateLegacyKeys(properties);
        } catch (IOException ex) {
            properties.clear();
        }
    }

    public static void saveConfiguration(String ambiente, String clave, String impresora,
            String formatoPrecio, String reporte) throws IOException {
        ensureConfigFileExists();
        Properties current = loadProperties();
        current.setProperty("ambiente", ambiente);
        current.setProperty("clave", clave);
        current.setProperty("impresora", impresora);
        current.setProperty("formatoPrecio", formatoPrecio);
        current.setProperty("reporte", reporte);

        if (!current.containsKey("rutaEmpresa")) {
            current.setProperty("rutaEmpresa", "");
        }

        storeProperties(current);
    }

    public static String getConfigPath() {
        return CONFIG_PATH;
    }

    public static String getContactConfigPath() {
        return CONTACT_PATH;
    }

    public static String getLicenseRequestEmail() {
        try {
            Properties contactProperties = loadContactProperties();
            return contactProperties.getProperty("correo", "").trim();
        } catch (IOException ex) {
            return "";
        }
    }

    public static boolean isFirstConfiguration() {
        File configFile = new File(CONFIG_PATH);
        if (!configFile.exists()) {
            return true;
        }

        try {
            Properties current = loadProperties();
            String clave = current.getProperty("clave", "");
            String impresora = current.getProperty("impresora", "");
            String reporte = current.getProperty("reporte", "");

            if (isBlank(clave)) {
                return true;
            }

            return isBlank(impresora) && isBlank(reporte);
        } catch (IOException ex) {
            return true;
        }
    }

    public static String getStoredLicenseKey() {
        try {
            Properties current = loadProperties();
            return current.getProperty("clave", "");
        } catch (IOException ex) {
            return "";
        }
    }

    public static boolean hasExistingLicense() {
        return !isBlank(getStoredLicenseKey());
    }

    public static Properties loadContactProperties() throws IOException {
        ensureContactFileExists();
        Properties properties = new Properties();
        InputStreamReader input = null;
        try {
            input = new InputStreamReader(new FileInputStream(CONTACT_PATH), StandardCharsets.UTF_8);
            properties.load(input);
            return properties;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    public static void syncContactFileFromAppDirectory() {
        try {
            ensureConfigDirectoryExists();
            File sourceFile = new File(AppPaths.resolveAppBaseDirectory(), CONTACT_FILE_NAME);
            Path targetPath = new File(CONTACT_PATH).toPath();

            if (sourceFile.isFile()) {
                Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                return;
            }

            ensureContactFileExists();
        } catch (IOException ex) {
            try {
                ensureContactFileExists();
            } catch (IOException ignored) {
            }
        }
    }

    private static void ensureConfigFileExists() throws IOException {
        ensureConfigDirectoryExists();
        if (!existsFile()) {
            createDefaultConfigFile();
        } else {
            ensureWritableConfigFile();
        }
    }

    private static void createDefaultConfigFile() throws IOException {
        ensureConfigDirectoryExists();
        Path path = new File(CONFIG_PATH).toPath();
        Files.writeString(path, DEFAULT_CONFIG_CONTENT, StandardCharsets.UTF_8);
        ensureWritableConfigFile();
    }

    private static void ensureWritableConfigFile() {
        File file = new File(CONFIG_PATH);
        file.setReadable(true, false);
        file.setWritable(true, false);
        file.setExecutable(false, false);
    }

    private static boolean existsFile() {
        return new File(CONFIG_PATH).exists();
    }

    private static void ensureConfigDirectoryExists() throws IOException {
        Files.createDirectories(new File(APPDATA_DIR).toPath());
    }

    private static void ensureContactFileExists() throws IOException {
        ensureConfigDirectoryExists();
        Path path = new File(CONTACT_PATH).toPath();
        if (!Files.exists(path)) {
            Files.writeString(path, DEFAULT_CONTACT_CONTENT, StandardCharsets.UTF_8);
        }
    }

    private static String resolveAppDataDirectory() {
        String appData = System.getenv("APPDATA");
        if (appData != null && !appData.trim().isEmpty()) {
            return appData + File.separator + "VerificadorPrecios";
        }

        String userHome = System.getProperty("user.home", "").trim();
        if (!userHome.isEmpty()) {
            String osName = System.getProperty("os.name", "").toLowerCase();
            if (osName.contains("win")) {
                return userHome + File.separator + "AppData" + File.separator
                        + "Roaming" + File.separator + "VerificadorPrecios";
            }
            return userHome + File.separator + ".verificadorprecios";
        }

        return System.getProperty("user.dir") + File.separator + "VerificadorPrecios";
    }

    private static Properties loadRawProperties() throws IOException {
        Properties properties = new Properties();
        InputStreamReader input = null;
        try {
            input = new InputStreamReader(new FileInputStream(CONFIG_PATH), StandardCharsets.UTF_8);
            properties.load(input);
            migrateLegacyKeys(properties);
            return properties;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    private static void storeProperties(Properties decodedProperties) throws IOException {
        Properties encodedProperties = new Properties();
        for (String key : decodedProperties.stringPropertyNames()) {
            String value = decodedProperties.getProperty(key, "");
            encodedProperties.setProperty(key, encodeBase64(value));
        }

        OutputStreamWriter output = null;
        try {
            output = new OutputStreamWriter(new FileOutputStream(CONFIG_PATH), StandardCharsets.UTF_8);
            encodedProperties.store(output, null);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    private static String encodeBase64(String value) {
        String safeValue = value == null ? "" : value;
        return Base64.getEncoder().encodeToString(safeValue.getBytes(StandardCharsets.UTF_8));
    }

    private boolean hasValue(String key) {
        String value = get(key);
        return value != null && !value.trim().isEmpty();
    }

    private static void migrateLegacyKeys(Properties properties) {
        String usuario = properties.getProperty("usuario", "").trim();
        String db = properties.getProperty("db", "").trim();
        if (usuario.isEmpty() && !db.isEmpty()) {
            properties.setProperty("usuario", db);
        }
        properties.remove("db");
    }

    private static String decodeBase64(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(value);
            String decodedValue = new String(decodedBytes, StandardCharsets.UTF_8);
            String normalizedSource = value.replace("\r", "").replace("\n", "");
            String reencodedValue = Base64.getEncoder().encodeToString(decodedBytes);
            if (!reencodedValue.equals(normalizedSource)) {
                return value;
            }
            for (int i = 0; i < decodedValue.length(); i++) {
                char currentChar = decodedValue.charAt(i);
                if (Character.isISOControl(currentChar) && !Character.isWhitespace(currentChar)) {
                    return value;
                }
            }
            return decodedValue;
        } catch (IllegalArgumentException ex) {
            return value;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
