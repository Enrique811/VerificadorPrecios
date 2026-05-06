package Metodos;

import java.math.BigDecimal;

public class DetalleImpuesto {

    private final String impuestoId;
    private final String nombre;
    private final BigDecimal subtotalAntes;
    private final BigDecimal porcentajeAplicado;
    private final BigDecimal montoImpuesto;
    private final BigDecimal subtotalDespues;

    public DetalleImpuesto(String impuestoId, String nombre, BigDecimal subtotalAntes,
            BigDecimal porcentajeAplicado, BigDecimal montoImpuesto, BigDecimal subtotalDespues) {
        this.impuestoId = impuestoId;
        this.nombre = nombre;
        this.subtotalAntes = subtotalAntes;
        this.porcentajeAplicado = porcentajeAplicado;
        this.montoImpuesto = montoImpuesto;
        this.subtotalDespues = subtotalDespues;
    }

    public String getImpuestoId() {
        return impuestoId;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getSubtotalAntes() {
        return subtotalAntes;
    }

    public BigDecimal getPorcentajeAplicado() {
        return porcentajeAplicado;
    }

    public BigDecimal getMontoImpuesto() {
        return montoImpuesto;
    }

    public BigDecimal getSubtotalDespues() {
        return subtotalDespues;
    }
}
