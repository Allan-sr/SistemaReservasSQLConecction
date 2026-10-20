package una.sistemareservas.proxy;

import una.sistemareservas.contratos.IUsuarioService;

public class UsuarioServiceProxy implements IUsuarioService {

    @Override
    public boolean cambiarClave(String idUsuario, String claveActual, String nuevaClave) {
        throw new UnsupportedOperationException("Pendiente: enviar solicitud al Backend mediante sockets");
    }
}