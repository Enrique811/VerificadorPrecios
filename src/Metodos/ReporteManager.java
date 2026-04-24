package Metodos;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ReporteManager {

    private static final String REPORTES_DIR = System.getProperty("user.dir") + File.separator + "reportes";
    private static final String EXTENSION_JASPER = ".jasper";

    private ReporteManager() {
    }

    public static List<String> listarReportesDisponibles() {
        File directorio = new File(REPORTES_DIR);
        if (!directorio.exists() || !directorio.isDirectory()) {
            return Collections.emptyList();
        }

        File[] archivos = directorio.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name != null && name.toLowerCase().endsWith(EXTENSION_JASPER);
            }
        });

        if (archivos == null || archivos.length == 0) {
            return Collections.emptyList();
        }

        Arrays.sort(archivos, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        List<String> reportes = new ArrayList<String>(archivos.length);
        for (File archivo : archivos) {
            reportes.add(archivo.getName());
        }
        return reportes;
    }

    public static String resolverReporteConfigurado(String nombreReporteConfigurado) {
        List<String> reportes = listarReportesDisponibles();
        if (reportes.isEmpty()) {
            return "";
        }

        if (nombreReporteConfigurado != null) {
            for (String reporte : reportes) {
                if (reporte.equalsIgnoreCase(nombreReporteConfigurado.trim())) {
                    return reporte;
                }
            }
        }

        return reportes.get(0);
    }

    public static String obtenerRutaReporteSeleccionado(String nombreReporte) {
        if (nombreReporte == null || nombreReporte.trim().isEmpty()) {
            return null;
        }

        String nombreSeguro = new File(nombreReporte.trim()).getName();
        if (!nombreSeguro.toLowerCase().endsWith(EXTENSION_JASPER)) {
            return null;
        }

        File archivoReporte = new File(REPORTES_DIR, nombreSeguro);
        if (!archivoReporte.exists() || !archivoReporte.isFile()) {
            return null;
        }

        return archivoReporte.getAbsolutePath();
    }
}
