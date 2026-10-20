package una.sistemareservas.negocio;

import una.sistemareservas.datos.UsuarioDAO;
import una.sistemareservas.modelo.Usuario;
import una.sistemareservas.contratos.IUsuarioService;

public class UsuarioService implements IUsuarioService{

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        usuarioDAO = new UsuarioDAO();
    }

    /**
     * Autentica un usuario.
     */
    public Usuario autenticar(String id, String clave) {

        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        if (clave == null || clave.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioDAO.buscarPorId(id);

        if (usuario == null) {
            return null;
        }

        if (!usuario.getClave().equals(clave)) {
            return null;
        }

        return usuario;
    }

    /**
     * Cambia la contraseña.
     */
    public boolean cambiarClave(
            String id,
            String claveActual,
            String nuevaClave) {

        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        if (claveActual == null || claveActual.isEmpty()) {
            return false;
        }

        if (nuevaClave == null || nuevaClave.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioDAO.buscarPorId(id);

        if (usuario == null) {
            return false;
        }

        if (!usuario.getClave().equals(claveActual)) {
            return false;
        }

        usuario.setClave(nuevaClave);

        return usuarioDAO.modificar(usuario);
    }
}