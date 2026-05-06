package Metodos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultadoCalculoImpuestos {

    private final BigDecimal precioBase;
    private final BigDecimal precioFinal;
    private final List<DetalleImpuesto> desglose;

    public ResultadoCalculoImpuestos(BigDecimal precioBase, BigDecimal precioFinal,
            List<DetalleImpuesto> desglose) {
        this.precioBase = precioBase == null ? BigDecimal.ZERO : precioBase;
        this.precioFinal = precioFinal == null ? BigDecimal.ZERO : precioFinal;
        this.desglose = desglose == null
                ? Collections.<DetalleImpuesto>emptyList()
                : Collections.unmodifiableList(new ArrayList<DetalleImpuesto>(desglose));
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public List<DetalleImpuesto> getDesglose() {
        return desglose;
    }

    public boolean tieneDesglose() {
        return !desglose.isEmpty();
    }
}
