package una.sistemareservas.pruebas;

import una.sistemareservas.modelo.Usuario;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainPruebaLogin {

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", 5000)) {

            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

            // Prueba 1: credenciales correctas
            probar(salida, entrada, "admin", "admin");

            // Prueba 2: clave incorrecta
            probar(salida, entrada, "admin", "claveMala");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void probar(ObjectOutputStream salida, ObjectInputStream entrada,
                               String id, String clave) throws Exception {

        salida.writeObject(new Peticion(Operaciones.USUARIO_AUTENTICAR, new Object[]{id, clave}));
        salida.flush();
        salida.reset();

        Respuesta respuesta = (Respuesta) entrada.readObject();

        System.out.println("Probando " + id + " / " + clave);
        System.out.println("  Exito: " + respuesta.isExito());
        System.out.println("  Mensaje: " + respuesta.getMensaje());

        if (respuesta.getDatos() instanceof Usuario usuario) {
            System.out.println("  Usuario: " + usuario.getId() + " - " + usuario.getNombreCompleto());
        } else {
            System.out.println("  Usuario: null");
        }
    }
}