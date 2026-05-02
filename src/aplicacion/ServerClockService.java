package aplicacion;

import java.util.Date;

public final class ServerClockService {

    private final ServerClockRepository serverClockRepository;

    public ServerClockService(ServerClockRepository serverClockRepository) {
        this.serverClockRepository = serverClockRepository;
    }

    public String obtenerFechaActual() {
        return serverClockRepository.obtenerFechaActual();
    }

    public Date obtenerFechaHoraActual() {
        return serverClockRepository.obtenerFechaHoraActual();
    }
}
