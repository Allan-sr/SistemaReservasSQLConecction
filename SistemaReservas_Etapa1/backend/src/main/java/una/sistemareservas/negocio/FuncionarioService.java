package una.sistemareservas.negocio;

import una.sistemareservas.datos.UsuarioDAO;
import una.sistemareservas.modelo.Funcionario;
import una.sistemareservas.modelo.Rol;
import una.sistemareservas.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioService {

    private final UsuarioDAO usuarioDAO;

    public FuncionarioService() {
        usuarioDAO = new UsuarioDAO();
    }

    /**
     * Lista todos los funcionarios registrados (excluye administradores).
     */
    public List<Funcionario> listar() {

        List<Funcionario> funcionarios = new ArrayList<>();

        for (Usuario usuario : usuarioDAO.cargar()) {

            if (usuario instanceof Funcionario funcionario) {
                funcionarios.add(funcionario);
            }
        }

        return funcionarios;
    }

    /**
     * Busca funcionarios por id o por nombre (coincidencia parcial).
     * Si ambos criterios vienen vacíos, retorna todos los funcionarios.
     */
    public List<Funcionario> buscar(String id, String nombre) {

        List<Funcionario> resultado = new ArrayList<>();

        String idBuscado = (id == null) ? "" : id.trim().toLowerCase();
        String nombreBuscado = (nombre == null) ? "" : nombre.trim().toLowerCase();

        for (Funcionario funcionario : listar()) {

            boolean coincideId =
                    idBuscado.isEmpty()
                            || funcionario.getId().toLowerCase().contains(idBuscado);

            boolean coincideNombre =
                    nombreBuscado.isEmpty()
                            || funcionario.getNombre().toLowerCase().contains(nombreBuscado);

            if (coincideId && coincideNombre) {
                resultado.add(funcionario);
            }
        }

        return resultado;
    }

    /**
     * Busca un funcionario por su id exacto.
     */
    public Funcionario buscarPorId(String id) {

        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioDAO.buscarPorId(id.trim());

        if (usuario instanceof Funcionario funcionario) {
            return funcionario;
        }

        return null;
    }

    /**
     * Agrega un nuevo funcionario. La clave inicial queda igual al id,
     * tal como lo pide el enunciado; el funcionario podrá cambiarla después.
     *
     * @return null si la operación fue exitosa, o un mensaje de error.
     */
    public String agregar(String id, String nombre, String telefono) {

        if (id == null || id.trim().isEmpty()) {
            return "Debe indicar el id del funcionario.";
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            return "Debe indicar el nombre del funcionario.";
        }

        if (telefono == null || telefono.trim().isEmpty()) {
            return "Debe indicar el teléfono del funcionario.";
        }

        if (usuarioDAO.buscarPorId(id.trim()) != null) {
            return "Ya existe un usuario con ese id.";
        }

        Funcionario funcionario =
                new Funcionario(id.trim(), Rol.FUNCIONARIO, nombre.trim(), telefono.trim());

        usuarioDAO.agregar(funcionario);

        return null;
    }

    /**
     * Modifica el nombre y teléfono de un funcionario existente.
     * La clave y el rol no se tocan desde esta pantalla.
     *
     * @return null si la operación fue exitosa, o un mensaje de error.
     */
    public String modificar(String id, String nombre, String telefono) {

        if (nombre == null || nombre.trim().isEmpty()) {
            return "Debe indicar el nombre del funcionario.";
        }

        if (telefono == null || telefono.trim().isEmpty()) {
            return "Debe indicar el teléfono del funcionario.";
        }

        Funcionario funcionario = buscarPorId(id);

        if (funcionario == null) {
            return "No existe un funcionario con ese id.";
        }

        funcionario.setNombre(nombre.trim());
        funcionario.setTelefono(telefono.trim());

        usuarioDAO.modificar(funcionario);

        return null;
    }

    /**
     * Elimina un funcionario por id.
     *
     * @return null si la operación fue exitosa, o un mensaje de error.
     */
    public String eliminar(String id) {

        Funcionario funcionario = buscarPorId(id);

        if (funcionario == null) {
            return "No existe un funcionario con ese id.";
        }

        usuarioDAO.eliminar(funcionario.getId());

        return null;
    }
}