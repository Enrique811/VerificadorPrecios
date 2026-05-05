package aplicacion.impresion;

public final class LabelPrintService {

    private final LabelPrinterGateway labelPrinterGateway;

    public LabelPrintService(LabelPrinterGateway labelPrinterGateway) {
        this.labelPrinterGateway = labelPrinterGateway;
    }

    public LabelPrintResult print(LabelPrintRequest request) {
        if (request == null || request.getArticulo() == null
                || request.getArticulo().getCodigoBarras() == null
                || request.getArticulo().getCodigoBarras().trim().isEmpty()) {
            return new LabelPrintResult(false, "No hay codigo de barras para imprimir.");
        }
        return labelPrinterGateway.print(request);
    }
}
