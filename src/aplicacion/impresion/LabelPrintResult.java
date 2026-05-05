package aplicacion.impresion;

public final class LabelPrintResult {

    private final boolean success;
    private final String message;

    public LabelPrintResult(boolean success, String message) {
        this.success = success;
        this.message = message == null ? "" : message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
