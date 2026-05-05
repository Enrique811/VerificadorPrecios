package com.project.barcode.newimpl;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BarcodeService {

    private static final Logger LOGGER = Logger.getLogger(BarcodeService.class.getName());

    public BarcodeType detectType(String input) {
        if (input == null) {
            LOGGER.warning("Barcode detectType received null input. Falling back to CODE128.");
            return BarcodeType.CODE128;
        }

        String value = input.trim();
        if (!value.matches("\\d+")) {
            return BarcodeType.CODE128;
        }

        if (value.length() == 8) {
            return validarEAN8(value) ? BarcodeType.EAN8 : fallback("Invalid EAN-8 checksum", value);
        }

        if (value.length() == 13) {
            return validarEAN13(value) ? BarcodeType.EAN13 : fallback("Invalid EAN-13 checksum", value);
        }

        if (value.length() == 12) {
            return validarUPCA(value) ? BarcodeType.UPCA : fallback("Invalid UPC-A checksum", value);
        }

        return BarcodeType.CODE128;
    }

    private BarcodeType fallback(String reason, String input) {
        LOGGER.log(Level.INFO, "{0}: {1}. Falling back to CODE128.", new Object[]{reason, input});
        return BarcodeType.CODE128;
    }

    boolean validarEAN8(String codigo) {
        int suma = 0;
        for (int i = 0; i < 7; i++) {
            int num = codigo.charAt(i) - '0';
            suma += (i % 2 == 0) ? num * 3 : num;
        }

        int check = (10 - (suma % 10)) % 10;
        return check == (codigo.charAt(7) - '0');
    }

    boolean validarEAN13(String codigo) {
        int suma = 0;
        for (int i = 0; i < 12; i++) {
            int num = codigo.charAt(i) - '0';
            suma += (i % 2 == 0) ? num : num * 3;
        }

        int check = (10 - (suma % 10)) % 10;
        return check == (codigo.charAt(12) - '0');
    }

    boolean validarUPCA(String codigo) {
        int suma = 0;
        for (int i = 0; i < 11; i++) {
            int num = codigo.charAt(i) - '0';
            suma += (i % 2 == 0) ? num * 3 : num;
        }

        int check = (10 - (suma % 10)) % 10;
        return check == (codigo.charAt(11) - '0');
    }
}
