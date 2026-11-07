package una.sistemareservas.cliente;

import una.sistemareservas.negocio.NotificacionReserva;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import javax.swing.SwingUtilities;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class ClienteSocket {

    private final String host;
    private final int puerto;

    private Socket socket;
    private ObjectOutputStream salida;

    private volatile CompletableFuture<Respuesta> pendiente;
    private volatile Consumer<NotificacionReserva> receptorAlertas;

    public ClienteSocket(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    public String getHost() {
        return host;
    }

    public synchronized void conectar() throws IOException {
        if (estaConectado()) {
            return;
        }

        Socket conexion = new Socket(host, puerto);

        try {
            ObjectOutputStream escritor =
                    new ObjectOutputStream(conexion.getOutputStream());
            escritor.flush();

            ObjectInputStream lector =
                    new ObjectInputStream(conexion.getInputStream());

            socket = conexion;
            salida = escritor;

            Thread hilo = new Thread(
                    () -> recibirMensajes(conexion, lector),
                    "receptor-servidor"
            );

            hilo.setDaemon(true);
            hilo.start();

        } catch (IOException e) {
            conexion.close();
            throw e;
        }
    }

    public void setReceptorAlertas(
            Consumer<NotificacionReserva> receptor) {

        receptorAlertas = receptor;
    }

    private void recibirMensajes(
            Socket conexion,
            ObjectInputStream entrada) {

        try {
            while (!conexion.isClosed()) {
                Object mensaje = entrada.readObject();

                if (mensaje instanceof Respuesta respuesta) {
                    CompletableFuture<Respuesta> espera = pendiente;

                    if (espera != null) {
                        espera.complete(respuesta);
                    }

                } else if (mensaje instanceof NotificacionReserva alerta) {
                    Consumer<NotificacionReserva> receptor =
                            receptorAlertas;

                    if (receptor != null) {
                        SwingUtilities.invokeLater(() -> {
                            // Evita entregar avisos a una sesión anterior.
                            if (receptorAlertas == receptor) {
                                receptor.accept(alerta);
                            }
                        });
                    }

                } else {
                    throw new IOException(
                            "Mensaje desconocido del servidor."
                    );
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            CompletableFuture<Respuesta> espera = pendiente;

            if (espera != null) {
                espera.completeExceptionally(e);
            }

        } finally {
            try {
                conexion.close();
            } catch (IOException ignorado) {
            }
        }
    }

    public synchronized Respuesta enviarPeticion(Peticion peticion)
            throws IOException, ClassNotFoundException {

        if (!estaConectado()) {
            throw new IOException(
                    "No existe conexión con el servidor."
            );
        }

        CompletableFuture<Respuesta> espera =
                new CompletableFuture<>();

        pendiente = espera;

        try {
            salida.writeObject(peticion);
            salida.flush();
            salida.reset();

            return espera.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            cerrarConexion();

            throw new IOException(
                    "La petición fue interrumpida.", e
            );

        } catch (ExecutionException e) {
            throw new IOException(
                    "Se perdió la comunicación con el servidor.",
                    e.getCause()
            );

        } catch (IOException e) {
            cerrarConexion();
            throw e;

        } finally {
            pendiente = null;
        }
    }

    public boolean estaConectado() {
        return socket != null
                && socket.isConnected()
                && !socket.isClosed();
    }

    private void cerrarConexion() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignorado) {
        }
    }

    public void desconectar() throws IOException {
        receptorAlertas = null;

        if (socket != null) {
            socket.close();
        }
    }
}