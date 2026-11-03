package una.sistemareservas.cliente;

import una.sistemareservas.negocio.NotificacionReserva;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Escucha en segundo plano los avisos que manda el servidor por el puerto de
 * alertas. Cuando llega uno, avisa a la pantalla (en el hilo de Swing).
 */
public class ListenerAlertas {

    private final String host;
    private final int puerto;
    private final String funcionarioId;
    private final Consumer<NotificacionReserva> alRecibir;

    private volatile boolean activo = false;
    private Socket socket;

    public ListenerAlertas(String host, int puerto, String funcionarioId,
                           Consumer<NotificacionReserva> alRecibir) {
        this.host = host;
        this.puerto = puerto;
        this.funcionarioId = funcionarioId;
        this.alRecibir = alRecibir;
    }

    public void iniciar() {

        activo = true;

        Thread hilo = new Thread(this::escuchar, "listener-alertas");
        hilo.setDaemon(true);
        hilo.start();
    }

    public void detener() {

        activo = false;

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignorado) {
            // al cerrar no importa el error
        }
    }

    private void escuchar() {

        try {
            socket = new Socket(host, puerto);

            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();

            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

            // Primero nos identificamos con el servidor
            salida.writeObject(funcionarioId);
            salida.flush();

            System.out.println("Escuchando alertas como " + funcionarioId);

            while (activo) {

                Object objeto = entrada.readObject();

                if (objeto instanceof NotificacionReserva notificacion) {
                    // Swing solo se toca desde su propio hilo
                    SwingUtilities.invokeLater(() -> alRecibir.accept(notificacion));
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            if (activo) {
                System.err.println("Se perdió la conexión de alertas: " + e.getMessage());
            }
        }
    }
}