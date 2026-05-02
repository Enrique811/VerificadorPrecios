package aplicacion;

import dominio.ConfiguracionApp;
import java.io.IOException;

public final class ConfigService {

    private final ConfigRepository configRepository;

    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public ConfiguracionApp cargarConfiguracion() {
        return configRepository.cargarConfiguracion();
    }

    public void guardarInformacion(String informacion) throws IOException {
        configRepository.guardarInformacion(informacion == null ? "" : informacion);
    }

    public void guardarConexion(String ipEmpresa, String usuario, String password, String rutaEmpresa) throws IOException {
        configRepository.guardarConexion(ipEmpresa, usuario, password, rutaEmpresa);
    }

    public void guardarPreferencias(String ambiente, String clave, String impresora,
            String formatoPrecio, String reporte) throws IOException {
        configRepository.guardarPreferencias(ambiente, clave, impresora, formatoPrecio, reporte);
    }
}
