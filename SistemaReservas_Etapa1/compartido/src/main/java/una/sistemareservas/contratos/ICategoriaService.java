
package una.sistemareservas.contratos;

import una.sistemareservas.modelo.Categoria;
import java.util.List;

public interface ICategoriaService {

    List<Categoria> listar();

    List<Categoria> buscar(String descripcion);

    String agregar(String descripcion);

    String modificar(String id, String descripcion);

    String eliminar(String id);
}


