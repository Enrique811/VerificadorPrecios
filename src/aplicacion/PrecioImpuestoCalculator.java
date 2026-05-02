package aplicacion;

import dominio.DesglosePrecio;
import dominio.ImpuestoCalculado;
import dominio.ImpuestoDefinicion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PrecioImpuestoCalculator {

    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final int SCALE_INTERNA = 8;

    private PrecioImpuestoCalculator() {
    }

    public static DesglosePrecio calcular(BigDecimal precioBase, List<ImpuestoDefinicion> impuestos) {
        BigDecimal precioBaseSeguro = precioBase == null ? BigDecimal.ZERO : precioBase;
        List<ImpuestoDefinicion> impuestosSeguros = impuestos == null
                ? Collections.<ImpuestoDefinicion>emptyList()
                : impuestos;

        if (impuestosSeguros.isEmpty()) {
            return new DesglosePrecio(precioBaseSeguro, precioBaseSeguro, Collections.<ImpuestoCalculado>emptyList());
        }

        List<ImpuestoCalculado> detalle = new ArrayList<ImpuestoCalculado>();
        BigDecimal subtotal = precioBaseSeguro;
        for (ImpuestoDefinicion impuesto : impuestosSeguros) {
            BigDecimal monto = subtotal
                    .multiply(impuesto.getPorcentaje())
                    .divide(CIEN, SCALE_INTERNA, RoundingMode.HALF_UP);
            subtotal = subtotal.add(monto);
            detalle.add(new ImpuestoCalculado(
                    impuesto.getId(),
                    impuesto.getNombre(),
                    impuesto.getPorcentaje(),
                    monto,
                    subtotal));
        }

        return new DesglosePrecio(subtotal, precioBaseSeguro, detalle);
    }
}
