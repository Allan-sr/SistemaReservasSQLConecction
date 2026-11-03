package una.sistemareservas.servidor;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Segundo servidor (puerto de alertas). Cada cliente se conecta, dice quién es
 * (su id de funcionario) y queda suscrito para recibir avisos.
 */
public class ServidorAlertas {

    private final int puerto;

    public ServidorAlertas(int puerto) {
        this.puerto = puerto;
    }

    public void iniciar() {

        try (ServerSocket servidor = new ServerSocket(puerto)) {

            System.out.println("Servidor de alertas iniciado en el puerto " + puerto);

            while (true) {
                Socket cliente = servidor.accept();

                Thread hilo = new Thread(() -> atenderSuscriptor(cliente));
                hilo.setDaemon(true);
                hilo.start();
            }

        } catch (IOException e) {
            System.err.println("Error en el servidor de alertas: " + e.getMessage());
        }
    }

    private void atenderSuscriptor(Socket cliente) {

        ObjectOutputStream salida = null;

        try (
                Socket conexion = cliente
        ) {

            salida = new ObjectOutputStream(conexion.getOutputStream());
            salida.flush();

            ObjectInputStream entrada = new ObjectInputStream(conexion.getInputStream());

            // Lo primero que manda el cliente es el id del funcionario
            String funcionarioId = (String) entrada.readObject();

            GestorNotificaciones.getInstancia().suscribir(salida, funcionarioId);

            // Espera hasta que el cliente se desconecte
            while (entrada.readObject() != null) {
                // el cliente no envía nada más; solo se queda a la escucha
            }

        } catch (EOFException e) {
            // desconexión normal
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Suscriptor de alertas desconectado: " + e.getMessage());
        } finally {
            if (salida != null) {
                GestorNotificaciones.getInstancia().desuscribir(salida);
            }
        }
    }
}