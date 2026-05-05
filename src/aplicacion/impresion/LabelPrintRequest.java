package aplicacion.impresion;

import dominio.ArticuloDetalle;
import dominio.ConfiguracionApp;

public final class LabelPrintRequest {

    private final ArticuloDetalle articulo;
    private final String descripcion;
    private final String precio;
    private final String informacion;
    private final ConfiguracionApp configuracion;

    public LabelPrintRequest(ArticuloDetalle articulo, String descripcion, String precio,
            String informacion, ConfiguracionApp configuracion) {
        this.articulo = articulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.informacion = informacion;
        this.configuracion = configuracion;
    }

    public ArticuloDetalle getArticulo() {
        return articulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPrecio() {
        return precio;
    }

    public String getInformacion() {
        return informacion;
    }

    public ConfiguracionApp getConfiguracion() {
        return configuracion;
    }
}
