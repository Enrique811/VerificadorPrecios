package dominio;

public final class ArticuloDetalle {

    private final String codigo;
    private final String codigoBarras;
    private final String identificacion;
    private final String descripcion;
    private final String presentacion;
    private final String stock;
    private final String precioIva;
    private final String impuestos;
    private final DesglosePrecio desglosePrecio;

    public ArticuloDetalle(String codigo, String codigoBarras, String identificacion,
            String descripcion, String presentacion, String stock, String precioIva) {
        this(codigo, codigoBarras, identificacion, descripcion, presentacion, stock,
                precioIva, null, null);
    }

    public ArticuloDetalle(String codigo, String codigoBarras, String identificacion,
            String descripcion, String presentacion, String stock, String precioIva,
            DesglosePrecio desglosePrecio) {
        this(codigo, codigoBarras, identificacion, descripcion, presentacion, stock,
                precioIva, null, desglosePrecio);
    }

    public ArticuloDetalle(String codigo, String codigoBarras, String identificacion,
            String descripcion, String presentacion, String stock, String precioIva,
            String impuestos, DesglosePrecio desglosePrecio) {
        this.codigo = codigo;
        this.codigoBarras = codigoBarras;
        this.identificacion = identificacion;
        this.descripcion = descripcion;
        this.presentacion = presentacion;
        this.stock = stock;
        this.precioIva = precioIva;
        this.impuestos = impuestos;
        this.desglosePrecio = desglosePrecio;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public String getStock() {
        return stock;
    }

    public String getPrecioIva() {
        return precioIva;
    }

    public String getImpuestos() {
        return impuestos;
    }

    public DesglosePrecio getDesglosePrecio() {
        return desglosePrecio;
    }
}
