package com.project.barcode.newimpl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class BarcodeGenerator {

    private static final int STANDARD_WIDTH = 340;
    private static final int STANDARD_HEIGHT = 56;
    private static final int CODE128_MIN_WIDTH = 500;
    private static final int CODE128_HEIGHT = 60;
    private static final int CODE128_WIDTH_PER_CHAR = 18;

    public byte[] generate(String input, BarcodeType type) throws WriterException, IOException {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Barcode input cannot be null or empty.");
        }

        String value = input.trim();
        BarcodeFormat format = toBarcodeFormat(type);

        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, Integer.valueOf(0));
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BarcodeDimensions dimensions = resolveDimensions(value, type);
        BitMatrix matrix = new MultiFormatWriter().encode(
                value,
                format,
                dimensions.getWidth(),
                dimensions.getHeight(),
                hints);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", output);
        return output.toByteArray();
    }

    private BarcodeDimensions resolveDimensions(String input, BarcodeType type) {
        if (BarcodeType.CODE128.equals(type)) {
            int dynamicWidth = Math.max(CODE128_MIN_WIDTH, input.length() * CODE128_WIDTH_PER_CHAR);
            return new BarcodeDimensions(dynamicWidth, CODE128_HEIGHT);
        }
        return new BarcodeDimensions(STANDARD_WIDTH, STANDARD_HEIGHT);
    }

    private BarcodeFormat toBarcodeFormat(BarcodeType type) {
        if (type == null) {
            return BarcodeFormat.CODE_128;
        }

        switch (type) {
            case EAN8:
                return BarcodeFormat.EAN_8;
            case EAN13:
                return BarcodeFormat.EAN_13;
            case UPCA:
                return BarcodeFormat.UPC_A;
            case CODE128:
            default:
                return BarcodeFormat.CODE_128;
        }
    }

    private static final class BarcodeDimensions {

        private final int width;
        private final int height;

        private BarcodeDimensions(int width, int height) {
            this.width = width;
            this.height = height;
        }

        private int getWidth() {
            return width;
        }

        private int getHeight() {
            return height;
        }
    }
}
