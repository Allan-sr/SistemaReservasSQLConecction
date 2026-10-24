
package una.sistemareservas.contratos;

import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Recurso;
import java.util.List;

public interface IRecursoService {

    List<Recurso> listar();

    List<Categoria> listarCategorias();

    List<Recurso> buscar(String categoriaId, String descripcion);

    Recurso buscarPorId(String id);

    String agregar(String id, String categoriaId, String descripcion);

    String modificar(String id, String categoriaId, String descripcion);

    String eliminar(String id);
}

