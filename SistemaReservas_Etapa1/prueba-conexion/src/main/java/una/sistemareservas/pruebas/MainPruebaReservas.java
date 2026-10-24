package una.sistemareservas.pruebas;
import una.sistemareservas.negocio.ResultadoReserva;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MainPruebaReservas {

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", 5000)) {

            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

            // 1) Crear una reserva para mañana con la categoría CAT-000002 (Laptop)
            Respuesta r1 = enviar(salida, entrada, Operaciones.RESERVA_CREAR,
                    new Object[]{"1001", "Prueba de red", LocalDate.now().plusDays(1),
                            LocalTime.of(10, 0), LocalTime.of(11, 0), List.of("CAT-000002")});

            ResultadoReserva crear = (ResultadoReserva) r1.getDatos();
            System.out.println("CREAR -> error validación: " + crear.esErrorValidacion()
                    + " | sin disponibilidad: " + crear.esSinDisponibilidad());

            // 2) Listar las reservas del funcionario 1001
            Respuesta r2 = enviar(salida, entrada, Operaciones.RESERVA_LISTAR_MIAS,
                    new Object[]{"1001"});
            List<?> mias = (List<?>) r2.getDatos();
            System.out.println("LISTAR_MIAS -> cantidad: " + mias.size());

            // 3) Cancelar la reserva recién creada, para dejar todo como estaba
            if (crear.getReserva() != null) {
                String id = crear.getReserva().getId();
                Respuesta r3 = enviar(salida, entrada, Operaciones.RESERVA_CANCELAR,
                        new Object[]{"1001", id});
                ResultadoReserva cancelar = (ResultadoReserva) r3.getDatos();
                System.out.println("CANCELAR " + id + " -> error: " + cancelar.esErrorValidacion());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Respuesta enviar(ObjectOutputStream salida, ObjectInputStream entrada,
                                    String operacion, Object datos) throws Exception {
        salida.writeObject(new Peticion(operacion, datos));
        salida.flush();
        salida.reset();
        Respuesta respuesta = (Respuesta) entrada.readObject();
        System.out.println(operacion + " -> exito: " + respuesta.isExito()
                + " | " + respuesta.getMensaje());
        return respuesta;
    }
}