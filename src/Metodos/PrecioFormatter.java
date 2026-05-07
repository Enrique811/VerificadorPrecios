package Metodos;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        String formatoPrecio = obtenerFormatoPrecio();

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

    public static String formatearPrecioDesglose(BigDecimal precio) {
        BigDecimal precioSeguro = precio == null ? BigDecimal.ZERO : precio;
        DecimalFormatSymbols simbolos = DecimalFormatSymbols.getInstance(Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", simbolos);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return "$" + decimalFormat.format(precioSeguro);
    }

    private static String obtenerFormatoPrecio() {
        String formatoPrecio = Configuracion.formatoPrecio;
        if (formatoPrecio == null || formatoPrecio.trim().isEmpty()) {
            return FORMATO_CO;
        }
        return formatoPrecio;
    }
}
