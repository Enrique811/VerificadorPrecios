/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Metodos;

import java.awt.Image;

/**
 *
 * @author gaming
 */
public class DatosReporte {
    private Image CODIGO_BARRAS;
    private String CODIGO_BARRAS_TEXTO;
    private String IDENTIFICACION;
    private String DESCRIPCION;
    private String PRECIO_VENTA;
    private String FECHA;
    private String INFORMACION;

    public String getINFORMACION() {
        return INFORMACION;
    }

    public void setINFORMACION(String INFORMACION) {
        this.INFORMACION = INFORMACION;
    }

    public DatosReporte(Image CODIGO_BARRAS, String CODIGO_BARRAS_TEXTO, String IDENTIFICACION, String DESCRIPCION, String PRECIO_VENTA, String FECHA,String INFORMACION) {
        this.CODIGO_BARRAS = CODIGO_BARRAS;
        this.CODIGO_BARRAS_TEXTO = CODIGO_BARRAS_TEXTO;
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
        return CODIGO_BARRAS;
    }

    /**
     * @param CODIGO_BARRAS the CODIGO_BARRAS to set
     */
    public void setCODIGO_BARRAS(Image CODIGO_BARRAS) {
        this.CODIGO_BARRAS = CODIGO_BARRAS;
    }

    public String getCODIGO_BARRAS_TEXTO() {
        return CODIGO_BARRAS_TEXTO;
    }

    public void setCODIGO_BARRAS_TEXTO(String CODIGO_BARRAS_TEXTO) {
        this.CODIGO_BARRAS_TEXTO = CODIGO_BARRAS_TEXTO;
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




}

