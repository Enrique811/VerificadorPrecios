package infraestructura;

import Metodos.LicenseJsonValidator;
import Metodos.LicenseValidationResult;
import aplicacion.ConfigService;
import aplicacion.ServerClockService;
import aplicacion.licencia.LicenseValidationGateway;
import dominio.ConfiguracionApp;
import java.util.Date;
import javax.swing.JOptionPane;

public final class ServiceLicenseValidationGateway implements LicenseValidationGateway {

    private final ConfigService configService;
    private final ServerClockService serverClockService;

    public ServiceLicenseValidationGateway(ConfigService configService, ServerClockService serverClockService) {
        this.configService = configService;
        this.serverClockService = serverClockService;
    }

    @Override
    public boolean validarLicenciaActual() {
        ConfiguracionApp configuracion = configService.cargarConfiguracion();
        String clave = configuracion == null ? null : configuracion.getClave();
        if (clave == null || clave.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Importe o capture la licencia JSON en configuracion.");
            return false;
        }

        Date fechaServidor = serverClockService.obtenerFechaHoraActual();
        LicenseValidationResult result = LicenseJsonValidator.validate(clave, fechaServidor);
        imprimirDiagnosticoLicencia(result);
        if (!result.isLicenciaValida()) {
            mostrarErrorFatal(result.buildSummary());
            System.exit(0);
            return false;
        }

        if (!isFechaDentroDelRango(fechaServidor, result.getFechaInicio(), result.getFechaFin())) {
            mostrarErrorFatal(result.buildSummary());
            System.exit(0);
            return false;
        }

        return true;
    }

    private void imprimirDiagnosticoLicencia(LicenseValidationResult result) {
        if (result == null) {
            return;
        }
        System.out.println(result.buildSummary());
    }

    private void mostrarErrorFatal(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje);
    }

    private boolean isFechaDentroDelRango(Date fechaActual, Date fechaInicio, Date fechaFin) {
        if (fechaActual == null || fechaInicio == null || fechaFin == null) {
            return false;
        }

        return (fechaActual.after(fechaInicio) || fechaActual.equals(fechaInicio))
                && (fechaActual.before(fechaFin) || fechaActual.equals(fechaFin));
    }
}
