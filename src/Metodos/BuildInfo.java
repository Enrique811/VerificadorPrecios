package Metodos;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class BuildInfo {

    private static final String BUILD_INFO_RESOURCE = "/build-info.properties";
    private static final String BUILD_TIMESTAMP_KEY = "build.timestamp";
    private static final String DEFAULT_BUILD_TIMESTAMP = "No disponible";

    private BuildInfo() {
    }

    public static String getBuildTimestamp() {
        Properties properties = new Properties();
        InputStream input = BuildInfo.class.getResourceAsStream(BUILD_INFO_RESOURCE);
        if (input == null) {
            return DEFAULT_BUILD_TIMESTAMP;
        }

        try {
            properties.load(input);
            return properties.getProperty(BUILD_TIMESTAMP_KEY, DEFAULT_BUILD_TIMESTAMP).trim();
        } catch (IOException ex) {
            return DEFAULT_BUILD_TIMESTAMP;
        } finally {
            try {
                input.close();
            } catch (IOException ex) {
            }
        }
    }
}
