package com.project.barcode.newimpl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.zxing.WriterException;

public class BarcodeFacade {

    private static final Logger LOGGER = Logger.getLogger(BarcodeFacade.class.getName());

    private final BarcodeService barcodeService;
    private final BarcodeGenerator barcodeGenerator;

    public BarcodeFacade() {
        this(new BarcodeService(), new BarcodeGenerator());
    }

    public BarcodeFacade(BarcodeService barcodeService, BarcodeGenerator barcodeGenerator) {
        this.barcodeService = barcodeService;
        this.barcodeGenerator = barcodeGenerator;
    }

    public byte[] generateBarcode(String input) {
        return generateBarcodeResult(input).getImageBytes();
    }

    public BarcodeResult generateBarcodeResult(String input) {
        if (input == null || input.trim().isEmpty()) {
            LOGGER.warning("BarcodeFacade received null or empty input.");
            return new BarcodeResult(new byte[0], BarcodeType.CODE128);
        }

        String trimmedInput = input.trim();
        BarcodeType barcodeType = barcodeService.detectType(trimmedInput);

        try {
            byte[] imageBytes = barcodeGenerator.generate(trimmedInput, barcodeType);
            return new BarcodeResult(imageBytes, barcodeType);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.WARNING, "Invalid barcode input: " + input, ex);
        } catch (WriterException ex) {
            LOGGER.log(Level.SEVERE, "ZXing failed to generate barcode for input: " + input, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to serialize barcode image for input: " + input, ex);
        }
        return new BarcodeResult(new byte[0], barcodeType);
    }
}
