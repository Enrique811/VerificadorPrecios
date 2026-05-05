package infraestructura;

import Conexion.Conexion;
import aplicacion.licencia.LicenseValidationGateway;

public final class LegacyLicenseValidationGateway implements LicenseValidationGateway {

    @Override
    public boolean validarLicenciaActual() {
        return Conexion.tieneLicenciavalida();
    }
}
