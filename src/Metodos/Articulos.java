/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Metodos;

import java.math.BigDecimal;

/**
 *
 * @author gaming
 */
public class Articulos {
    private String codigo;
    private String codigoBarras;
    private String identificacion;
    private String descripcion;
    private String stock;
    private String presentacion;
    private BigDecimal precioFinal;
    private String desgloseImpuestos;

    // Constructor
    public Articulos(String codigo, String codigoBarras, String identificacion, String descripcion, 
                    String stock, String presentacion, BigDecimal precioFinal, String desgloseImpuestos) {
        this.codigo = codigo;
        this.codigoBarras = codigoBarras;
        this.identificacion = identificacion;
        this.descripcion = descripcion;
        this.stock = stock;
        this.presentacion = presentacion;
        this.precioFinal = precioFinal;
        this.desgloseImpuestos = desgloseImpuestos;
    }

    // Getters y Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
    }

    public String getDesgloseImpuestos() {
        return desgloseImpuestos;
    }

    public void setDesgloseImpuestos(String desgloseImpuestos) {
        this.desgloseImpuestos = desgloseImpuestos;
    }

    @Override
    public String toString() {
        return "Articulo{" +
                "codigo='" + codigo + '\'' +
                ", codigoBarras='" + codigoBarras + '\'' +
                ", identificacion='" + identificacion + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", stock='" + stock + '\'' +
                ", presentacion='" + presentacion + '\'' +
                ", precioFinal='" + precioFinal + '\'' +
                ", desgloseImpuestos='" + desgloseImpuestos + '\'' +
                '}';
    }
}

