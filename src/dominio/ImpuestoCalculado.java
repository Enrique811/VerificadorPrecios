package dominio;

import java.math.BigDecimal;

public final class ImpuestoCalculado {

    private final String id;
    private final String nombre;
    private final BigDecimal porcentaje;
    private final BigDecimal monto;
    private final BigDecimal subtotal;

    public ImpuestoCalculado(String id, String nombre, BigDecimal porcentaje,
            BigDecimal monto, BigDecimal subtotal) {
        this.id = id;
        this.nombre = nombre;
        this.porcentaje = porcentaje == null ? BigDecimal.ZERO : porcentaje;
        this.monto = monto == null ? BigDecimal.ZERO : monto;
        this.subtotal = subtotal == null ? BigDecimal.ZERO : subtotal;
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

    public BigDecimal getMonto() {
        return monto;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
