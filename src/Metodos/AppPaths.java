package Metodos;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public final class AppPaths {

    private AppPaths() {
    }

    public static File resolveAppBaseDirectory() {
        File sourceDir = resolveCodeSourceDirectory();
        File baseDir = findResourceBase(sourceDir);
        if (baseDir != null) {
            return baseDir;
        }

        File userDir = new File(System.getProperty("user.dir"));
        baseDir = findResourceBase(userDir);
        if (baseDir != null) {
            return baseDir;
        }

        return userDir;
    }

    public static File resolveResourceDirectory(String directoryName) {
        return new File(resolveAppBaseDirectory(), directoryName);
    }

    private static File findResourceBase(File startDir) {
        File current = startDir;
        for (int i = 0; current != null && i < 4; i++) {
            if (new File(current, "reportes").isDirectory()
                    || new File(current, "lib").isDirectory()) {
                return current;
            }
            current = current.getParentFile();
        }
        return null;
    }

    private static File resolveCodeSourceDirectory() {
        try {
            URL location = AppPaths.class.getProtectionDomain().getCodeSource().getLocation();
            if (location == null) {
                return new File(System.getProperty("user.dir"));
            }

            File source = new File(location.toURI());
            return source.isFile() ? source.getParentFile() : source;
        } catch (URISyntaxException ex) {
            return new File(System.getProperty("user.dir"));
        }
    }
}
