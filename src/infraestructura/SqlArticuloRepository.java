package infraestructura;

import Metodos.Articulos;
import SQL.SQLArticulo;
import aplicacion.ArticuloRepository;
import aplicacion.PrecioImpuestoCalculator;
import dominio.ArticuloDetalle;
import dominio.DesglosePrecio;
import dominio.ImpuestoDefinicion;
import java.math.BigDecimal;
import java.util.List;

public final class SqlArticuloRepository implements ArticuloRepository {

    @Override
    public ArticuloDetalle buscarPorCodigoBarras(String codigoBarras, String tarifa) {
        SQLArticulo.buscarArticuloPorCodigoBarra(codigoBarras, tarifa);
        if (!SQLArticulo.controlConsulta) {
            return null;
        }
        List<ImpuestoDefinicion> impuestos = SQLArticulo.obtenerImpuestosPorCadena(SQLArticulo.impuestos);
        DesglosePrecio desglose = PrecioImpuestoCalculator.calcular(
                parsearPrecio(SQLArticulo.precio_venta_iva),
                impuestos);

        return new ArticuloDetalle(
                SQLArticulo.codigo,
                SQLArticulo.codigo_barras,
                SQLArticulo.identificacion,
                SQLArticulo.descripcion,
                SQLArticulo.presentacion,
                SQLArticulo.formato,
                SQLArticulo.precio_venta_iva,
                SQLArticulo.impuestos,
                desglose);
    }

    @Override
    public List<Articulos> buscarPorDescripcion(String descripcion) {
        return SQLArticulo.buscarArticuloPorDescripcion(descripcion);
    }

    private BigDecimal parsearPrecio(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(valor.trim());
    }
}
