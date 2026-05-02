package Metodos;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public final class PrecioFormatter {

    private static final String FORMATO_CO = "CO";
    private static final String FORMATO_MX = "MX";

    private PrecioFormatter() {
    }

    public static String formatearPrecio(double precio) {
        return formatearPrecio(BigDecimal.valueOf(precio));
    }

    public static String formatearPrecio(BigDecimal precio) {
        BigDecimal precioSeguro = precio == null ? BigDecimal.ZERO : precio;
        String formatoPrecio = Configuracion.formatoPrecio;
        if (formatoPrecio == null || formatoPrecio.trim().isEmpty()) {
            formatoPrecio = FORMATO_CO;
        }

        if (FORMATO_MX.equalsIgnoreCase(formatoPrecio)) {
            DecimalFormatSymbols simbolos = DecimalFormatSymbols.getInstance(Locale.US);
            DecimalFormat decimalFormat = new DecimalFormat("0.00", simbolos);
            return "$" + decimalFormat.format(precioSeguro);
        }

        // Colombia mantiene el comportamiento especial actual:
        // formato monetario sin decimales por requerimiento existente.
        Locale colombia = new Locale("es", "CO");
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(colombia);
        formatoMoneda.setMaximumFractionDigits(0);
        return formatoMoneda.format(precioSeguro);
    }
}
