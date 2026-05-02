package aplicacion;

import Metodos.Articulos;
import dominio.ArticuloDetalle;
import java.util.List;

public interface ArticuloRepository {

    ArticuloDetalle buscarPorCodigoBarras(String codigoBarras, String tarifa);

    List<Articulos> buscarPorDescripcion(String descripcion);
}
