package com.project.barcode.newimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BarcodeSampleApp {

    public static void main(String[] args) throws IOException {
        BarcodeFacade facade = new BarcodeFacade();
        writeSample("ean8", "55123457", facade);
        writeSample("ean13", "5901234123457", facade);
        writeSample("upca", "036000291452", facade);
        writeSample("code128", "ABC-123-XYZ", facade);
    }

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
