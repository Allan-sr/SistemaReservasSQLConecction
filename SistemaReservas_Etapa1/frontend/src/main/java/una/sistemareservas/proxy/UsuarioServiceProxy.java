package una.sistemareservas.proxy;

import una.sistemareservas.cliente.ClienteSocket;
import una.sistemareservas.contratos.IUsuarioService;
import una.sistemareservas.modelo.Usuario;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.io.IOException;

public class UsuarioServiceProxy implements IUsuarioService {

    // La "radio" para hablar con el servidor. Se asigna una sola vez.
    private final ClienteSocket cliente;

    // Quien crea el proxy le entrega la radio.
    public UsuarioServiceProxy(ClienteSocket cliente) {
        this.cliente = cliente;
    }

    @Override
    public Usuario autenticar(String id, String clave) {
        try {
            // 1) La comanda: qué se pide + los detalles en orden (id, clave)
            Peticion peticion = new Peticion(
                    Operaciones.USUARIO_AUTENTICAR,
                    new Object[]{id, clave}
            );

            // 2) Enviarla y esperar la respuesta del servidor
            Respuesta respuesta = cliente.enviarPeticion(peticion);

            // 3) Si el servidor no pudo procesar el pedido, es un fallo real
            if (!respuesta.isExito()) {
                throw new RuntimeException(respuesta.getMensaje());
            }

            // 4) Entregar el resultado: el Usuario, o null si las credenciales eran malas
            return (Usuario) respuesta.getDatos();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("No se pudo comunicar con el servidor", e);
        }
    }

    @Override
    public boolean cambiarClave(String idUsuario, String claveActual, String nuevaClave) {
        throw new UnsupportedOperationException("Pendiente: enviar solicitud al Backend mediante sockets");
    }
}