package aplicacion.conexion;

public final class ConnectionCheckResult {

    private final boolean success;
    private final String message;

    public ConnectionCheckResult(boolean success, String message) {
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
