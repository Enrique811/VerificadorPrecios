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

    private static final int DEFAULT_WIDTH = 166;
    private static final int DEFAULT_HEIGHT = 28;

    public byte[] generate(String input, BarcodeType type) throws WriterException, IOException {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Barcode input cannot be null or empty.");
        }

        String value = input.trim();
        BarcodeFormat format = toBarcodeFormat(type);

        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, Integer.valueOf(0));
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter().encode(value, format, DEFAULT_WIDTH, DEFAULT_HEIGHT, hints);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", output);
        return output.toByteArray();
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
}
