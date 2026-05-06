package Metodos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CalculadoraImpuestos {

    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final int ESCALA_CALCULO = 6;
    private static final int ESCALA_MONEDA = 2;

    private CalculadoraImpuestos() {
    }

    public static ResultadoCalculoImpuestos calcular(BigDecimal precioBase,
            List<String> impuestosOrdenados, Map<String, ImpuestoInfo> impuestosDisponibles) {
        BigDecimal subtotal = normalizarMoneda(precioBase);
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
                    .divide(CIEN, ESCALA_CALCULO, RoundingMode.HALF_UP)
                    .setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
            BigDecimal subtotalDespues = subtotalAntes.add(montoImpuesto)
                    .setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);

            detalles.add(new DetalleImpuesto(impuestoId, impuesto.getNombre(),
                    subtotalAntes, porcentaje, montoImpuesto, subtotalDespues));
            subtotal = subtotalDespues;
        }

        return new ResultadoCalculoImpuestos(normalizarMoneda(precioBase), subtotal, detalles);
    }

    private static BigDecimal normalizarMoneda(BigDecimal valor) {
        BigDecimal seguro = valor == null ? BigDecimal.ZERO : valor;
        return seguro.setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }
}
