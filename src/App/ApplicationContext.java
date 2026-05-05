package App;

import aplicacion.ArticuloService;
import aplicacion.ConfigService;
import aplicacion.ServerClockService;
import aplicacion.conexion.ConnectionCheckService;
import aplicacion.impresion.LabelPrintService;
import aplicacion.licencia.LicenseValidationService;
import com.project.barcode.newimpl.BarcodeFacade;
import infraestructura.JasperLabelPrinterGateway;
import infraestructura.LegacyConfigRepository;
import infraestructura.LegacyConnectionValidator;
import infraestructura.ServiceLicenseValidationGateway;
import infraestructura.SqlArticuloRepository;
import infraestructura.SqlServerClockRepository;

public final class ApplicationContext {

    private final ArticuloService articuloService;
    private final ConfigService configService;
    private final ServerClockService serverClockService;
    private final ConnectionCheckService connectionCheckService;
    private final LicenseValidationService licenseValidationService;
    private final LabelPrintService labelPrintService;

    public ApplicationContext() {
        this.configService = new ConfigService(new LegacyConfigRepository());
        this.articuloService = new ArticuloService(new SqlArticuloRepository());
        this.serverClockService = new ServerClockService(new SqlServerClockRepository());
        this.connectionCheckService = new ConnectionCheckService(new LegacyConnectionValidator());
        this.licenseValidationService = new LicenseValidationService(
                new ServiceLicenseValidationGateway(configService, serverClockService));
        this.labelPrintService = new LabelPrintService(
                new JasperLabelPrinterGateway(new BarcodeFacade(), serverClockService));
    }

    public ArticuloService getArticuloService() {
        return articuloService;
    }

    public ConfigService getConfigService() {
        return configService;
    }

    public ServerClockService getServerClockService() {
        return serverClockService;
    }

    public ConnectionCheckService getConnectionCheckService() {
        return connectionCheckService;
    }

    public LicenseValidationService getLicenseValidationService() {
        return licenseValidationService;
    }

    public LabelPrintService getLabelPrintService() {
        return labelPrintService;
    }
}
