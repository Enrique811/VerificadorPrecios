package infraestructura;

import SQL.SQLFechaHora;
import aplicacion.ServerClockRepository;
import java.util.Date;

public final class SqlServerClockRepository implements ServerClockRepository {

    @Override
    public String obtenerFechaActual() {
        return SQLFechaHora.obtenerFechayHoraActualDelServidor();
    }

    @Override
    public Date obtenerFechaHoraActual() {
        return SQLFechaHora.obtenerFechayHoraActualDelServidorDate();
    }
}
