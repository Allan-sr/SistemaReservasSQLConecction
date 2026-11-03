package una.sistemareservas.cliente;

import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.io.*;
import java.net.Socket;

public class ClienteSocket {

    private final String host;
    private final int puerto;

    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;

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

        socket = new Socket(host, puerto);

        salida = new ObjectOutputStream(socket.getOutputStream());
        salida.flush();

        entrada = new ObjectInputStream(socket.getInputStream());

        System.out.println("Conectado al servidor: " + host + ":" + puerto);
    }

    public synchronized Respuesta enviarPeticion(Peticion peticion)
            throws IOException, ClassNotFoundException {

        if (!estaConectado()) {
            throw new IOException("No existe conexión con el servidor.");
        }

        salida.writeObject(peticion);
        salida.flush();
        salida.reset();

        Object objeto = entrada.readObject();

        if (!(objeto instanceof Respuesta)) {
            throw new IOException("Respuesta desconocida del servidor.");
        }

        return (Respuesta) objeto;
    }

    public boolean estaConectado() {
        return socket != null
                && socket.isConnected()
                && !socket.isClosed();
    }

    public synchronized void desconectar() throws IOException {

        if (socket != null) {
            socket.close();
        }

        socket = null;
        entrada = null;
        salida = null;

        System.out.println("Cliente desconectado.");
    }
}


