package aplicacion.conexion;

import Metodos.ConfigManager;

public final class ConnectionCheckService {

    private final ConnectionValidator connectionValidator;

    public ConnectionCheckService(ConnectionValidator connectionValidator) {
        this.connectionValidator = connectionValidator;
    }

    public ConnectionCheckResult check(ConfigManager configManager) {
        return connectionValidator.check(configManager);
    }
}
