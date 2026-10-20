package una.sistemareservas.negocio;

import una.sistemareservas.datos.CategoriaDAO;
import una.sistemareservas.datos.RecursoDAO;
import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Recurso;

import java.util.ArrayList;
import java.util.List;

public class CategoriaService {

    private final CategoriaDAO categoriaDAO;
    private final RecursoDAO recursoDAO;

    public CategoriaService() {
        categoriaDAO = new CategoriaDAO();
        recursoDAO = new RecursoDAO();
    }

    /**
     * Lista todas las categorías.
     */
    public List<Categoria> listar() {
        return categoriaDAO.cargar();
    }

    /**
     * Busca categorías cuya descripción contenga el texto indicado.
     * Si el texto viene vacío, retorna todas las categorías.
     */
    public List<Categoria> buscar(String descripcion) {

        String textoBuscado = (descripcion == null) ? "" : descripcion.trim().toLowerCase();

        if (textoBuscado.isEmpty()) {
            return listar();
        }

        List<Categoria> resultado = new ArrayList<>();

        for (Categoria categoria : listar()) {

            if (categoria.getDescripcion().toLowerCase().contains(textoBuscado)) {
                resultado.add(categoria);
            }
        }

        return resultado;
    }

    /**
     * Busca una categoría por su id exacto.
     */
    public Categoria buscarPorId(String id) {
        return categoriaDAO.buscarPorId(id);
    }

    /**
     * Agrega una nueva categoría. El id se autogenera.
     *
     * @return null si la operación fue exitosa, o un mensaje de error.
     */
    public String agregar(String descripcion) {

        if (descripcion == null || descripcion.trim().isEmpty()) {
            return "Debe indicar la descripción de la categoría.";
        }

        if (existeDescripcion(descripcion, null)) {
            return "Ya existe una categoría con esa descripción.";
        }

        categoriaDAO.agregar(descripcion.trim());

        return null;
    }

    /**
     * Modifica la descripción de una categoría existente.
     *
     * @return null si la operación fue exitosa, o un mensaje de error.
     */
    public String modificar(String id, String descripcion) {

        if (descripcion == null || descripcion.trim().isEmpty()) {
            return "Debe indicar la descripción de la categoría.";
        }

        if (categoriaDAO.buscarPorId(id) == null) {
            return "No existe una categoría con ese id.";
        }

        if (existeDescripcion(descripcion, id)) {
            return "Ya existe otra categoría con esa descripción.";
        }

        categoriaDAO.modificar(id, descripcion.trim());

        return null;
    }

    /**
     * Elimina una categoría. No permite eliminarla si hay recursos
     * asociados a ella, para no dejar datos inconsistentes.
     *
     * @return null si la operación fue exitosa, o un mensaje de error.
     */
    public String eliminar(String id) {

        if (categoriaDAO.buscarPorId(id) == null) {
            return "No existe una categoría con ese id.";
        }

        if (tieneRecursosAsociados(id)) {
            return "No se puede eliminar: existen recursos asociados a esta categoría.";
        }

        categoriaDAO.eliminar(id);

        return null;
    }

    private boolean existeDescripcion(String descripcion, String idAExcluir) {

        String textoBuscado = descripcion.trim().toLowerCase();

        for (Categoria categoria : listar()) {

            boolean esLaMisma =
                    idAExcluir != null && categoria.getId().equalsIgnoreCase(idAExcluir);

            if (!esLaMisma && categoria.getDescripcion().toLowerCase().equals(textoBuscado)) {
                return true;
            }
        }

        return false;
    }

    private boolean tieneRecursosAsociados(String idCategoria) {

        for (Recurso recurso : recursoDAO.cargar()) {

            if (recurso.getCategoriaId() != null
                    && recurso.getCategoriaId().equalsIgnoreCase(idCategoria)) {
                return true;
            }
        }

        return false;
    }
}