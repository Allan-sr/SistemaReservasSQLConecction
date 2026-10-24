package una.sistemareservas.pruebas;

import una.sistemareservas.negocio.ReservaExtraidaDTO;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainPruebaIA {

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", 5000)) {

            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

            String frase = "Necesito una sala de reuniones mañana de 9 a 11 para la reunión de equipo";

            salida.writeObject(new Peticion(Operaciones.RESERVA_EXTRAER_IA, new Object[]{frase}));
            salida.flush();

            Respuesta respuesta = (Respuesta) entrada.readObject();

            System.out.println("Exito: " + respuesta.isExito());
            System.out.println("Mensaje: " + respuesta.getMensaje());

            if (respuesta.getDatos() instanceof ReservaExtraidaDTO dto) {
                System.out.println("Actividad: " + dto.getActividad());
                System.out.println("Fecha: " + dto.getFecha());
                System.out.println("Hora: " + dto.getHoraInicio() + " - " + dto.getHoraFin());
                System.out.println("Categorías: " + dto.getCategorias());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}