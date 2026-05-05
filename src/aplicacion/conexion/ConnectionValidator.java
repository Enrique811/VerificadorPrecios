package aplicacion.conexion;

import Metodos.ConfigManager;

public interface ConnectionValidator {

    ConnectionCheckResult check(ConfigManager configManager);
}
