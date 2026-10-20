package una.sistemareservas.cliente;

import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

public class MainClientePrueba {

    public static void main(String[] args) {
        ClienteSocket cliente = new ClienteSocket("localhost", 5000);

        try {
            cliente.conectar();
            Peticion peticion = new Peticion("PING", null);
            Respuesta respuesta = cliente.enviarPeticion(peticion);
            System.out.println("Éxito: " + respuesta.isExito());
            System.out.println("Mensaje: " + respuesta.getMensaje());
            System.out.println("Datos: " + respuesta.getDatos());
            cliente.desconectar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
