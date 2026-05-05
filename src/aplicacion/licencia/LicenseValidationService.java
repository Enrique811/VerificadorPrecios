package aplicacion.licencia;

public final class LicenseValidationService {

    private final LicenseValidationGateway licenseValidationGateway;

    public LicenseValidationService(LicenseValidationGateway licenseValidationGateway) {
        this.licenseValidationGateway = licenseValidationGateway;
    }

    public boolean validarLicenciaActual() {
        return licenseValidationGateway.validarLicenciaActual();
    }
}
