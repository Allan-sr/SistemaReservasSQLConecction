package una.sistemareservas.negocio;

import una.sistemareservas.datos.CategoriaDAO;
import una.sistemareservas.datos.RecursoDAO;
import una.sistemareservas.datos.ReservaDAO;
import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.EstadoReserva;
import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.modelo.Reserva;

import java.util.ArrayList;
import java.util.List;

public class RecursoService {

    private final RecursoDAO recursoDAO;
    private final CategoriaDAO categoriaDAO;
    private final ReservaDAO reservaDAO;

    public RecursoService() {
        recursoDAO = new RecursoDAO();
        categoriaDAO = new CategoriaDAO();
        reservaDAO = new ReservaDAO();
    }

    /**
     * Lista todos los recursos.
     */
    public List<Recurso> listar() {
        return recursoDAO.cargar();
    }

    /**
     * Lista todas las categorías disponibles, para llenar el combo de filtro/formulario.
     */
    public List<Categoria> listarCategorias() {
        return categoriaDAO.cargar();
    }

    /**
     * Filtra recursos por categoría (opcional) y por descripción (opcional, coincidencia parcial).
     * Si ambos criterios vienen vacíos, retorna todos los recursos.
     */
    public List<Recurso> buscar(String categoriaId, String descripcion) {

        String categoriaBuscada = (categoriaId == null) ? "" : categoriaId.trim();
        String textoBuscado = (descripcion == null) ? "" : descripcion.trim().toLowerCase();

        List<Recurso> resultado = new ArrayList<>();

        for (Recurso recurso : listar()) {

            boolean coincideCategoria =
                    categoriaBuscada.isEmpty()
                            || categoriaBuscada.equalsIgnoreCase(recurso.getCategoriaId());

            boolean coincideDescripcion =
                    textoBuscado.isEmpty()
                            || recurso.getDescripcion().toLowerCase().contains(textoBuscado);

            if (coincideCategoria && coincideDescripcion) {
                resultado.add(recurso);
            }
        }

        return resultado;
    }

    /**
     * Busca un recurso por su id exacto.
     */
    public Recurso buscarPorId(String id) {
        return recursoDAO.buscarPorId(id);
    }

    /**
     * Agrega un nuevo recurso. El id (número de activo) lo indica el usuario.
     *
     * @return null si la operación fue exitosa, o un mensaje de error.
     */
    public String agregar(String id, String categoriaId, String descripcion) {

        String error = validarCampos(id, categoriaId, descripcion);

        if (error != null) {
            return error;
        }

        if (recursoDAO.buscarPorId(id.trim()) != null) {
            return "Ya existe un recurso con ese id.";
        }

        Categoria categoria = categoriaDAO.buscarPorId(categoriaId);

        if (categoria == null) {
            return "La categoría seleccionada no existe.";
        }

        Recurso recurso = new Recurso(id.trim(), categoria, descripcion.trim());

        recursoDAO.agregar(recurso);

        return null;
    }

    /**
     * Modifica la categoría y descripción de un recurso existente.
     *
     * @return null si la operación fue exitosa, o un mensaje de error.
     */
    public String modificar(String id, String categoriaId, String descripcion) {

        String error = validarCampos(id, categoriaId, descripcion);

        if (error != null) {
            return error;
        }

        Recurso recurso = recursoDAO.buscarPorId(id);

        if (recurso == null) {
            return "No existe un recurso con ese id.";
        }

        Categoria categoria = categoriaDAO.buscarPorId(categoriaId);

        if (categoria == null) {
            return "La categoría seleccionada no existe.";
        }

        recurso.setCategoria(categoria);
        recurso.setDescripcion(descripcion.trim());

        recursoDAO.modificar(recurso);

        return null;
    }

    /**
     * Elimina un recurso. No permite eliminarlo si está incluido
     * en alguna reserva activa, para no dejar datos inconsistentes.
     *
     * @return null si la operación fue exitosa, o un mensaje de error.
     */
    public String eliminar(String id) {

        if (recursoDAO.buscarPorId(id) == null) {
            return "No existe un recurso con ese id.";
        }

        if (tieneReservasActivas(id)) {
            return "No se puede eliminar: el recurso está asignado a una o más reservas activas.";
        }

        recursoDAO.eliminar(id);

        return null;
    }

    private String validarCampos(String id, String categoriaId, String descripcion) {

        if (id == null || id.trim().isEmpty()) {
            return "Debe indicar el id (número de activo) del recurso.";
        }

        if (categoriaId == null || categoriaId.trim().isEmpty()) {
            return "Debe seleccionar una categoría.";
        }

        if (descripcion == null || descripcion.trim().isEmpty()) {
            return "Debe indicar la descripción del recurso.";
        }

        return null;
    }

    private boolean tieneReservasActivas(String recursoId) {

        for (Reserva reserva : reservaDAO.cargar()) {

            if (reserva.getEstado() != EstadoReserva.ACTIVA) {
                continue;
            }

            if (reserva.getRecursoIds().contains(recursoId)) {
                return true;
            }
        }

        return false;
    }
}