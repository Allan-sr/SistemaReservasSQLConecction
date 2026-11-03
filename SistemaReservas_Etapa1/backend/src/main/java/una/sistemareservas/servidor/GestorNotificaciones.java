package una.sistemareservas.servidor;

import una.sistemareservas.negocio.NotificacionReserva;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GestorNotificaciones {

    private static final GestorNotificaciones INSTANCIA = new GestorNotificaciones();

    public static GestorNotificaciones getInstancia() {
        return INSTANCIA;
    }

    // salida del cliente -> id del funcionario que inició sesión
    private final Map<ObjectOutputStream, String> suscriptores = new ConcurrentHashMap<>();

    private GestorNotificaciones() {
    }

    public void suscribir(ObjectOutputStream salida, String funcionarioId) {
        suscriptores.put(salida, funcionarioId);
        System.out.println("Suscrito a alertas: " + funcionarioId);
    }

    public void desuscribir(ObjectOutputStream salida) {
        suscriptores.remove(salida);
    }

    /** Avisa a todos menos a quien creó la reserva. */
    public void notificarATodosMenos(String idCreador, NotificacionReserva notificacion) {

        for (Map.Entry<ObjectOutputStream, String> entrada : suscriptores.entrySet()) {

            if (entrada.getValue() != null
                    && entrada.getValue().equalsIgnoreCase(idCreador)) {
                continue;
            }

            ObjectOutputStream salida = entrada.getKey();

            try {
                synchronized (salida) {
                    salida.writeObject(notificacion);
                    salida.flush();
                }
            } catch (IOException e) {
                // el cliente se desconectó: lo sacamos de la lista
                suscriptores.remove(salida);
            }
        }
    }
}