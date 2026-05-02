package infraestructura;

import Metodos.ConfigManager;
import Metodos.Configuracion;
import aplicacion.ConfigRepository;
import dominio.ConfiguracionApp;
import java.io.IOException;

public final class LegacyConfigRepository implements ConfigRepository {

    @Override
    public ConfiguracionApp cargarConfiguracion() {
        Configuracion.leerArchivoDePropiedades();
        return new ConfiguracionApp(
                Configuracion.ipEmpresa,
                Configuracion.rutaEmpresa,
                Configuracion.usuario,
                Configuracion.password,
                Configuracion.clave,
                Configuracion.informacion,
                Configuracion.impresora,
                Configuracion.ambiente,
                Configuracion.formatoPrecio,
                Configuracion.reporte);
    }

    @Override
    public void guardarInformacion(String informacion) throws IOException {
        ConfigManager.saveInformation(informacion);
    }

    @Override
    public void guardarConexion(String ipEmpresa, String usuario, String password, String rutaEmpresa) throws IOException {
        ConfigManager config = new ConfigManager();
        config.set("ipEmpresa", ipEmpresa);
        config.set("usuario", usuario);
        config.set("password", password);
        config.set("rutaEmpresa", rutaEmpresa);
        config.save();
    }

    @Override
    public void guardarPreferencias(String ambiente, String clave, String impresora,
            String formatoPrecio, String reporte) throws IOException {
        ConfigManager.saveConfiguration(ambiente, clave, impresora, formatoPrecio, reporte);
    }
}
