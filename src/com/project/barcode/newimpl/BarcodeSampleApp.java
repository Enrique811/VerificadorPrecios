package com.project.barcode.newimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BarcodeSampleApp {

    private static void writeSample(String name, String value, BarcodeFacade facade) throws IOException {
        byte[] barcode = facade.generateBarcode(value);
        File directory = new File("build/barcode-samples");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File output = new File(directory, name + ".png");
        FileOutputStream stream = new FileOutputStream(output);
        try {
            stream.write(barcode);
        } finally {
            stream.close();
        }
    }
}
