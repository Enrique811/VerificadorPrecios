package Metodos;

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
        String formatoPrecio = Configuracion.formatoPrecio;
        if (formatoPrecio == null || formatoPrecio.trim().isEmpty()) {
            formatoPrecio = FORMATO_CO;
        }

        if (FORMATO_MX.equalsIgnoreCase(formatoPrecio)) {
            DecimalFormatSymbols simbolos = DecimalFormatSymbols.getInstance(Locale.US);
            DecimalFormat decimalFormat = new DecimalFormat("0.00", simbolos);
            return "$" + decimalFormat.format(precio);
        }

        // Colombia mantiene el comportamiento especial actual:
        // formato monetario sin decimales por requerimiento existente.
        Locale colombia = new Locale("es", "CO");
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(colombia);
        formatoMoneda.setMaximumFractionDigits(0);
        return formatoMoneda.format(precio);
    }
}
