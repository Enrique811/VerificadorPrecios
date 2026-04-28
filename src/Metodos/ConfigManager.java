package Metodos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

public final class ConfigManager {

    private static final String CONFIG_PATH = System.getProperty("user.dir") + "/configuracion.properties";
    public static final String DEFAULT_USUARIO = "SYSDBA";
    public static final String DEFAULT_PASSWORD = "masterkey";
    private final Properties properties = new Properties();

    public ConfigManager() {
        reload();
    }

    public static Properties loadProperties() throws IOException {
        Properties rawProperties = loadRawProperties();
        Properties decodedProperties = new Properties();
        for (String key : rawProperties.stringPropertyNames()) {
            String value = rawProperties.getProperty(key, "");
            decodedProperties.setProperty(key, "clave".equals(key) ? value : decodeBase64(value));
        }
        return decodedProperties;
    }

    public static void saveInformation(String informacion) throws IOException {
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
            return;
        }
        try {
            properties.putAll(loadProperties());
            migrateLegacyKeys(properties);
        } catch (IOException ex) {
            properties.clear();
        }
    }

    public static void saveConfiguration(String ambiente, String clave, String impresora,
            String formatoPrecio, String reporte) throws IOException {
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
            encodedProperties.setProperty(key, "clave".equals(key) ? value : encodeBase64(value));
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
}
