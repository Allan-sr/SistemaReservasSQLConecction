package una.sistemareservas.contratos;
import una.sistemareservas.modelo.Usuario;

public interface IUsuarioService {

    boolean cambiarClave(
            String idUsuario,
            String claveActual,
            String nuevaClave
    );

    Usuario autenticar(String id, String clave);
}
