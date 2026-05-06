package Metodos;

import java.math.BigDecimal;

public class ImpuestoInfo {

    private final String id;
    private final String nombre;
    private final BigDecimal porcentaje;

    public ImpuestoInfo(String id, String nombre, BigDecimal porcentaje) {
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
