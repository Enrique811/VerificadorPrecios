/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Metodos;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author gaming
 */
public class DatosReporte {
    private byte[] CODIGO_BARRAS;
    private String CODIGO_BARRAS_TEXTO;
    private boolean IS_CODE128;
    private String IDENTIFICACION;
    private String DESCRIPCION;
    private String PRECIO_VENTA;
    private String FECHA;
    private String INFORMACION;
    private byte[] CODIGO_BARRAS_2;
    private String CODIGO_BARRAS_TEXTO_2;
    private String DESCRIPCION_2;
    private String PRECIO_VENTA_2;
    private String FECHA_2;
    private String INFORMACION_2;
    private byte[] CODIGO_BARRAS_3;
    private String CODIGO_BARRAS_TEXTO_3;
    private String DESCRIPCION_3;
    private String PRECIO_VENTA_3;
    private String FECHA_3;
    private String INFORMACION_3;

    public String getINFORMACION() {
        return INFORMACION;
    }

    public void setINFORMACION(String INFORMACION) {
        this.INFORMACION = INFORMACION;
    }

    public DatosReporte(byte[] CODIGO_BARRAS, String CODIGO_BARRAS_TEXTO, boolean IS_CODE128, String IDENTIFICACION, String DESCRIPCION, String PRECIO_VENTA, String FECHA, String INFORMACION) {
        this.CODIGO_BARRAS = CODIGO_BARRAS;
        this.CODIGO_BARRAS_TEXTO = CODIGO_BARRAS_TEXTO;
        this.IS_CODE128 = IS_CODE128;
        this.IDENTIFICACION = IDENTIFICACION;
        this.DESCRIPCION = DESCRIPCION;
        this.PRECIO_VENTA = PRECIO_VENTA;
        this.FECHA = FECHA;
        this.INFORMACION = INFORMACION;
    
    }

    public String getFECHA() {
        return FECHA;
    }

    public void setFECHA(String FECHA) {
        this.FECHA = FECHA;
    }
    /**
     * @return the CODIGO_BARRAS
     */
    public Image getCODIGO_BARRAS() {
        if (CODIGO_BARRAS == null || CODIGO_BARRAS.length == 0) {
            return null;
        }

        try {
            return ImageIO.read(new ByteArrayInputStream(CODIGO_BARRAS));
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * @param CODIGO_BARRAS the CODIGO_BARRAS to set
     */
    public void setCODIGO_BARRAS(byte[] CODIGO_BARRAS) {
        this.CODIGO_BARRAS = CODIGO_BARRAS;
    }

    public byte[] getCODIGO_BARRAS_BYTES() {
        return CODIGO_BARRAS;
    }

    public String getCODIGO_BARRAS_TEXTO() {
        return CODIGO_BARRAS_TEXTO;
    }

    public void setCODIGO_BARRAS_TEXTO(String CODIGO_BARRAS_TEXTO) {
        this.CODIGO_BARRAS_TEXTO = CODIGO_BARRAS_TEXTO;
    }

    public boolean isIS_CODE128() {
        return IS_CODE128;
    }

    public boolean getIS_CODE128() {
        return IS_CODE128;
    }

    public void setIS_CODE128(boolean IS_CODE128) {
        this.IS_CODE128 = IS_CODE128;
    }

    /**
     * @return the IDENTIFICACION
     */
    public String getIDENTIFICACION() {
        return IDENTIFICACION;
    }

    /**
     * @param IDENTIFICACION the IDENTIFICACION to set
     */
    public void setIDENTIFICACION(String IDENTIFICACION) {
        this.IDENTIFICACION = IDENTIFICACION;
    }

    /**
     * @return the DESCRIPCION
     */
    public String getDESCRIPCION() {
        return DESCRIPCION;
    }

    /**
     * @param DESCRIPCION the DESCRIPCION to set
     */
    public void setDESCRIPCION(String DESCRIPCION) {
        this.DESCRIPCION = DESCRIPCION;
    }

    /**
     * @return the PRECIO_VENTA
     */
    public String getPRECIO_VENTA() {
        return PRECIO_VENTA;
    }

    /**
     * @param PRECIO_VENTA the PRECIO_VENTA to set
     */
    public void setPRECIO_VENTA(String PRECIO_VENTA) {
        this.PRECIO_VENTA = PRECIO_VENTA;
    }

    public Image getCODIGO_BARRAS_2() {
        return leerImagen(CODIGO_BARRAS_2);
    }

    public void setCODIGO_BARRAS_2(byte[] CODIGO_BARRAS_2) {
        this.CODIGO_BARRAS_2 = CODIGO_BARRAS_2;
    }

    public String getCODIGO_BARRAS_TEXTO_2() {
        return CODIGO_BARRAS_TEXTO_2;
    }

    public void setCODIGO_BARRAS_TEXTO_2(String CODIGO_BARRAS_TEXTO_2) {
        this.CODIGO_BARRAS_TEXTO_2 = CODIGO_BARRAS_TEXTO_2;
    }

    public String getDESCRIPCION_2() {
        return DESCRIPCION_2;
    }

    public void setDESCRIPCION_2(String DESCRIPCION_2) {
        this.DESCRIPCION_2 = DESCRIPCION_2;
    }

    public String getPRECIO_VENTA_2() {
        return PRECIO_VENTA_2;
    }

    public void setPRECIO_VENTA_2(String PRECIO_VENTA_2) {
        this.PRECIO_VENTA_2 = PRECIO_VENTA_2;
    }

    public String getFECHA_2() {
        return FECHA_2;
    }

    public void setFECHA_2(String FECHA_2) {
        this.FECHA_2 = FECHA_2;
    }

    public String getINFORMACION_2() {
        return INFORMACION_2;
    }

    public void setINFORMACION_2(String INFORMACION_2) {
        this.INFORMACION_2 = INFORMACION_2;
    }

    public Image getCODIGO_BARRAS_3() {
        return leerImagen(CODIGO_BARRAS_3);
    }

    public void setCODIGO_BARRAS_3(byte[] CODIGO_BARRAS_3) {
        this.CODIGO_BARRAS_3 = CODIGO_BARRAS_3;
    }

    public String getCODIGO_BARRAS_TEXTO_3() {
        return CODIGO_BARRAS_TEXTO_3;
    }

    public void setCODIGO_BARRAS_TEXTO_3(String CODIGO_BARRAS_TEXTO_3) {
        this.CODIGO_BARRAS_TEXTO_3 = CODIGO_BARRAS_TEXTO_3;
    }

    public String getDESCRIPCION_3() {
        return DESCRIPCION_3;
    }

    public void setDESCRIPCION_3(String DESCRIPCION_3) {
        this.DESCRIPCION_3 = DESCRIPCION_3;
    }

    public String getPRECIO_VENTA_3() {
        return PRECIO_VENTA_3;
    }

    public void setPRECIO_VENTA_3(String PRECIO_VENTA_3) {
        this.PRECIO_VENTA_3 = PRECIO_VENTA_3;
    }

    public String getFECHA_3() {
        return FECHA_3;
    }

    public void setFECHA_3(String FECHA_3) {
        this.FECHA_3 = FECHA_3;
    }

    public String getINFORMACION_3() {
        return INFORMACION_3;
    }

    public void setINFORMACION_3(String INFORMACION_3) {
        this.INFORMACION_3 = INFORMACION_3;
    }

    private Image leerImagen(byte[] codigoBarras) {
        if (codigoBarras == null || codigoBarras.length == 0) {
            return null;
        }

        try {
            return ImageIO.read(new ByteArrayInputStream(codigoBarras));
        } catch (IOException ex) {
            return null;
        }
    }




}

