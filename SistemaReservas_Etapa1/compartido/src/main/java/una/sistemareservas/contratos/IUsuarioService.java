package una.sistemareservas.contratos;

public interface IUsuarioService {

    boolean cambiarClave(
            String idUsuario,
            String claveActual,
            String nuevaClave
    );
}
