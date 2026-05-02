package aplicacion;

import Metodos.Articulos;
import dominio.ArticuloDetalle;
import java.util.Collections;
import java.util.List;

public final class ArticuloService {

    private final ArticuloRepository articuloRepository;

    public ArticuloService(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public ArticuloDetalle consultarArticulo(String codigoBarras, String tarifa) {
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            return null;
        }
        return articuloRepository.buscarPorCodigoBarras(codigoBarras.trim(), tarifa);
    }

    public List<Articulos> buscarArticulosPorDescripcion(String descripcion) {
        if (descripcion == null) {
            return Collections.emptyList();
        }
        return articuloRepository.buscarPorDescripcion(descripcion.trim());
    }
}
