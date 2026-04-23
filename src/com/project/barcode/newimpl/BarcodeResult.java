package com.project.barcode.newimpl;

public class BarcodeResult {

    private final byte[] imageBytes;
    private final BarcodeType type;

    public BarcodeResult(byte[] imageBytes, BarcodeType type) {
        this.imageBytes = imageBytes == null ? new byte[0] : imageBytes;
        this.type = type == null ? BarcodeType.CODE128 : type;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public BarcodeType getType() {
        return type;
    }

    public boolean isCode128() {
        return BarcodeType.CODE128.equals(type);
    }
}
