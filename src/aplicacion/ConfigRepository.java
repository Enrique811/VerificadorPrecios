package aplicacion;

import dominio.ConfiguracionApp;
import java.io.IOException;

public interface ConfigRepository {

    ConfiguracionApp cargarConfiguracion();

    void guardarInformacion(String informacion) throws IOException;

    void guardarConexion(String ipEmpresa, String usuario, String password, String rutaEmpresa) throws IOException;

    void guardarPreferencias(String ambiente, String clave, String impresora,
            String formatoPrecio, String reporte) throws IOException;
}
