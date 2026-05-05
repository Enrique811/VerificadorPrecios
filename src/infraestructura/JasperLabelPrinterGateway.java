package infraestructura;

import Metodos.DatosReporte;
import Metodos.ReporteManager;
import aplicacion.ServerClockService;
import aplicacion.impresion.LabelPrintRequest;
import aplicacion.impresion.LabelPrintResult;
import aplicacion.impresion.LabelPrinterGateway;
import com.project.barcode.newimpl.BarcodeFacade;
import com.project.barcode.newimpl.BarcodeResult;
import dominio.ArticuloDetalle;
import dominio.ConfiguracionApp;
import java.util.ArrayList;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import net.sf.jasperreports.view.JasperViewer;

public final class JasperLabelPrinterGateway implements LabelPrinterGateway {

    private final BarcodeFacade barcodeFacade;
    private final ServerClockService serverClockService;

    public JasperLabelPrinterGateway(BarcodeFacade barcodeFacade, ServerClockService serverClockService) {
        this.barcodeFacade = barcodeFacade;
        this.serverClockService = serverClockService;
    }

    @Override
    public LabelPrintResult print(LabelPrintRequest request) {
        try {
            ArticuloDetalle articulo = request.getArticulo();
            ConfiguracionApp configuracion = request.getConfiguracion();
            String codigoOriginal = articulo.getCodigoBarras() == null
                    ? ""
                    : articulo.getCodigoBarras().trim();

            BarcodeResult barcodeResult = barcodeFacade.generateBarcodeResult(codigoOriginal);
            byte[] barcodeBytes = barcodeResult.getImageBytes();
            if (barcodeBytes.length == 0) {
                return new LabelPrintResult(false, "No se pudo generar el codigo de barras");
            }

            String reporteConfigurado = configuracion == null ? "" : configuracion.getReporte();
            String reporteSeleccionado = ReporteManager.resolverReporteConfigurado(reporteConfigurado);
            String rutaReporte = ReporteManager.obtenerRutaReporteSeleccionado(reporteSeleccionado);
            if (rutaReporte == null) {
                return new LabelPrintResult(false, "No se encontro un reporte .jasper valido en /reportes.");
            }

            ArrayList<DatosReporte> parametros = new ArrayList<DatosReporte>();
            parametros.add(new DatosReporte(barcodeBytes,
                    codigoOriginal,
                    barcodeResult.isCode128(),
                    codigoOriginal,
                    request.getDescripcion(),
                    request.getPrecio(),
                    serverClockService.obtenerFechaActual(),
                    request.getInformacion()));

            JRDataSource dataSource = new JRBeanCollectionDataSource(parametros);
            JasperPrint informe = JasperFillManager.fillReport(rutaReporte, null, dataSource);

            if (configuracion != null && "a".equalsIgnoreCase(configuracion.getAmbiente())) {
                JasperViewer viewer = new JasperViewer(informe, false);
                viewer.setTitle("Vista previa de etiqueta");
                viewer.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                viewer.getRootPane().registerKeyboardAction(e -> viewer.dispose(),
                        javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                        javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
                viewer.setVisible(true);
                return new LabelPrintResult(true, "");
            }

            if (configuracion != null && "b".equalsIgnoreCase(configuracion.getAmbiente())) {
                PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
                PrintService impresoraSeleccionada = null;

                for (PrintService service : services) {
                    if (service.getName().equalsIgnoreCase(configuracion.getImpresora())) {
                        impresoraSeleccionada = service;
                        break;
                    }
                }

                if (impresoraSeleccionada == null) {
                    return new LabelPrintResult(false, "No se encontro la impresora: " + configuracion.getImpresora());
                }

                JRPrintServiceExporter exportador = new JRPrintServiceExporter();
                exportador.setExporterInput(new SimpleExporterInput(informe));

                SimplePrintServiceExporterConfiguration config = new SimplePrintServiceExporterConfiguration();
                config.setPrintService(impresoraSeleccionada);
                config.setPrintRequestAttributeSet(new javax.print.attribute.HashPrintRequestAttributeSet());
                config.setDisplayPageDialog(false);
                config.setDisplayPrintDialog(false);

                exportador.setConfiguration(config);
                exportador.exportReport();
                return new LabelPrintResult(true, "");
            }

            String ambienteActual = configuracion == null ? "" : configuracion.getAmbiente();
            return new LabelPrintResult(false, "Configurar ambiente: " + ambienteActual);
        } catch (JRException ex) {
            ex.printStackTrace(System.out);
            return new LabelPrintResult(false, "No se pudo generar la etiqueta");
        }
    }
}
