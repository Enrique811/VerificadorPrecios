package aplicacion;

import java.util.Date;

public interface ServerClockRepository {

    String obtenerFechaActual();

    Date obtenerFechaHoraActual();
}
