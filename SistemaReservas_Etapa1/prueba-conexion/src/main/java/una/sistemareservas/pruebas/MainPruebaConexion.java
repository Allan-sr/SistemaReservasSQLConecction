package una.sistemareservas.pruebas;

import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainPruebaConexion {

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", 5000)) {

            ObjectOutputStream salida =
                    new ObjectOutputStream(socket.getOutputStream());

            salida.flush();

            ObjectInputStream entrada =
                    new ObjectInputStream(socket.getInputStream());

            Peticion peticion = new Peticion("PING", null);

            salida.writeObject(peticion);
            salida.flush();

            Respuesta respuesta = (Respuesta) entrada.readObject();

            System.out.println("Exito: " + respuesta.isExito());
            System.out.println("Mensaje: " + respuesta.getMensaje());
            System.out.println("Datos: " + respuesta.getDatos());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

