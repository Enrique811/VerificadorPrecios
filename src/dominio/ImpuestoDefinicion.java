package dominio;

import java.math.BigDecimal;

public final class ImpuestoDefinicion {

    private final String id;
    private final String nombre;
    private final BigDecimal porcentaje;

    public ImpuestoDefinicion(String id, String nombre, BigDecimal porcentaje) {
        this.id = id;
        this.nombre = nombre;
        this.porcentaje = porcentaje == null ? BigDecimal.ZERO : porcentaje;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }
}
