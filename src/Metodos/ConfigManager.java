package Metodos;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class ConfigManager {

    private static final String CONFIG_PATH = System.getProperty("user.dir") + "/configuracion.properties";

    private ConfigManager() {
    }

    public static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        InputStreamReader input = null;
        try {
            input = new InputStreamReader(new FileInputStream(CONFIG_PATH), StandardCharsets.UTF_8);
            properties.load(input);
            return properties;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    public static void saveConfiguration(String ambiente, String clave, String impresora,
            String informacion, String ipEmpresa, String formatoPrecio, String reporte) throws IOException {
        Properties current = loadProperties();
        current.setProperty("ambiente", ambiente);
        current.setProperty("clave", clave);
        current.setProperty("impresora", impresora);
        current.setProperty("informacion", informacion);
        current.setProperty("ipEmpresa", ipEmpresa);
        current.setProperty("formatoPrecio", formatoPrecio);
        current.setProperty("reporte", reporte);

        if (!current.containsKey("rutaEmpresa")) {
            current.setProperty("rutaEmpresa", "");
        }

        OutputStreamWriter output = null;
        try {
            output = new OutputStreamWriter(new FileOutputStream(CONFIG_PATH), StandardCharsets.UTF_8);
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
