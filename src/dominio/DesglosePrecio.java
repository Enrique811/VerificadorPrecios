package dominio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DesglosePrecio {

    private final BigDecimal precioFinal;
    private final BigDecimal precioBase;
    private final List<ImpuestoCalculado> impuestos;

    public DesglosePrecio(BigDecimal precioFinal, BigDecimal precioBase, List<ImpuestoCalculado> impuestos) {
        this.precioFinal = precioFinal == null ? BigDecimal.ZERO : precioFinal;
        this.precioBase = precioBase == null ? BigDecimal.ZERO : precioBase;
        this.impuestos = impuestos == null
                ? Collections.<ImpuestoCalculado>emptyList()
                : Collections.unmodifiableList(new ArrayList<ImpuestoCalculado>(impuestos));
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public List<ImpuestoCalculado> getImpuestos() {
        return impuestos;
    }

    public boolean tieneImpuestos() {
        return !impuestos.isEmpty();
    }
}
