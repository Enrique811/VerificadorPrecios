package Metodos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CalculadoraImpuestos {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private CalculadoraImpuestos() {
    }

    public static ResultadoCalculoImpuestos calcular(BigDecimal precioBase,
            List<String> impuestosOrdenados, Map<String, ImpuestoInfo> impuestosDisponibles) {
        BigDecimal subtotal = valorSeguro(precioBase);
        if (impuestosOrdenados == null || impuestosOrdenados.isEmpty()
                || impuestosDisponibles == null || impuestosDisponibles.isEmpty()) {
            return new ResultadoCalculoImpuestos(subtotal, subtotal, new ArrayList<DetalleImpuesto>());
        }

        List<DetalleImpuesto> detalles = new ArrayList<DetalleImpuesto>();
        for (int i = 0; i < impuestosOrdenados.size(); i++) {
            String impuestoId = impuestosOrdenados.get(i);
            ImpuestoInfo impuesto = impuestosDisponibles.get(impuestoId);
            if (impuesto == null) {
                continue;
            }

            BigDecimal subtotalAntes = subtotal;
            BigDecimal porcentaje = impuesto.getPorcentaje() == null
                    ? BigDecimal.ZERO : impuesto.getPorcentaje();
            BigDecimal montoImpuesto = subtotalAntes
                    .multiply(porcentaje)
                    .divide(CIEN);
            BigDecimal subtotalDespues = subtotalAntes.add(montoImpuesto);

            detalles.add(new DetalleImpuesto(impuestoId, impuesto.getNombre(),
                    subtotalAntes, porcentaje, montoImpuesto, subtotalDespues));
            subtotal = subtotalDespues;
        }

        return new ResultadoCalculoImpuestos(valorSeguro(precioBase), subtotal, detalles);
    }

    private static BigDecimal valorSeguro(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}
