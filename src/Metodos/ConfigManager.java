package Metodos;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final String CONFIG_PATH = System.getProperty("user.dir") + "/configuracion.properties";

    private ConfigManager() {
    }

    public static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(CONFIG_PATH);
            properties.load(input);
            return properties;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    public static void saveConfiguration(String ambiente, String clave, String impresora,
            String informacion, String ipEmpresa, String formatoPrecio) throws IOException {
        Properties current = loadProperties();
        current.setProperty("ambiente", ambiente);
        current.setProperty("clave", clave);
        current.setProperty("impresora", impresora);
        current.setProperty("informacion", informacion);
        current.setProperty("ipEmpresa", ipEmpresa);
        current.setProperty("formatoPrecio", formatoPrecio);

        if (!current.containsKey("rutaEmpresa")) {
            current.setProperty("rutaEmpresa", "");
        }

        OutputStream output = null;
        try {
            output = new FileOutputStream(CONFIG_PATH);
            current.store(output, null);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    public static String getConfigPath() {
        return CONFIG_PATH;
    }
}
