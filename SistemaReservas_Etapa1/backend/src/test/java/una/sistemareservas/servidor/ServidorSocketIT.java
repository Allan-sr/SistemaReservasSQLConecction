package una.sistemareservas.servidor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import una.sistemareservas.modelo.Usuario;
import una.sistemareservas.negocio.NotificacionReserva;
import una.sistemareservas.negocio.ResultadoReserva;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración: levanta el servidor real (puertos de prueba distintos
 * a los de producción) y le habla por sockets. Necesita Oracle encendido, el
 * script cargado y la variable de entorno RESERVAS_DB_PASSWORD.
 */
class ServidorSocketIT {

    private static final int PUERTO = 5099;
    private static final int PUERTO_ALERTAS = 5098;

    @BeforeAll
    static void iniciarServidores() throws Exception {

        Thread servidor = new Thread(() -> {
            try {
                new ServidorSocket(PUERTO).iniciar();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        servidor.setDaemon(true);
        servidor.start();

        Thread alertas = new Thread(() -> new ServidorAlertas(PUERTO_ALERTAS).iniciar());
        alertas.setDaemon(true);
        alertas.start();

        Thread.sleep(800); // le damos tiempo a abrir los puertos
    }

    /** Envía una petición y devuelve la respuesta (una conexión por petición). */
    private Respuesta enviar(String operacion, Object datos) throws Exception {

        try (Socket socket = new Socket("localhost", PUERTO)) {

            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

            salida.writeObject(new Peticion(operacion, datos));
            salida.flush();

            return (Respuesta) entrada.readObject();
        }
    }

    @Test
    void pingDebeResponderPong() throws Exception {

        Respuesta respuesta = enviar(Operaciones.PING, null);

        assertTrue(respuesta.isExito());
        assertEquals("PONG", respuesta.getDatos());
    }

    @Test
    void autenticarConCredencialesValidasDevuelveElUsuario() throws Exception {

        Respuesta respuesta = enviar(Operaciones.USUARIO_AUTENTICAR,
                new Object[]{"admin", "admin"});

        assertTrue(respuesta.isExito());
        assertNotNull(respuesta.getDatos(), "Si falla, revisa que 'admin' conserve su clave original");
        assertEquals("admin", ((Usuario) respuesta.getDatos()).getId());
    }

    @Test
    void autenticarConClaveIncorrectaDevuelveNull() throws Exception {

        Respuesta respuesta = enviar(Operaciones.USUARIO_AUTENTICAR,
                new Object[]{"admin", "clave-incorrecta"});

        assertTrue(respuesta.isExito());   // el servidor procesó bien la petición
        assertNull(respuesta.getDatos());  // pero las credenciales no sirven
    }

    @Test
    void listarCategoriasDevuelveLasDeLaBaseDeDatos() throws Exception {

        Respuesta respuesta = enviar(Operaciones.CATEGORIA_LISTAR, null);

        assertTrue(respuesta.isExito());
        assertFalse(((List<?>) respuesta.getDatos()).isEmpty());
    }

    @Test
    void operacionDesconocidaDevuelveError() throws Exception {

        Respuesta respuesta = enviar("OPERACION_QUE_NO_EXISTE", null);

        assertFalse(respuesta.isExito());
    }

    @Test
    void crearReservaAvisaALosOtrosUsuariosYSePuedeCancelar() throws Exception {

        // 1) El funcionario 1001 se suscribe a las alertas (como hace el frontend)
        try (Socket oyente = new Socket("localhost", PUERTO_ALERTAS)) {

            oyente.setSoTimeout(5000); // si en 5 s no llega nada, la prueba falla

            ObjectOutputStream salida = new ObjectOutputStream(oyente.getOutputStream());
            salida.flush();
            ObjectInputStream entrada = new ObjectInputStream(oyente.getInputStream());

            salida.writeObject("1001");
            salida.flush();
            Thread.sleep(500); // tiempo para que el servidor registre la suscripción

            // 2) El funcionario 1002 crea una reserva lejana, para no chocar con otras
            LocalDate fecha = LocalDate.now().plusDays(400);

            Respuesta creada = enviar(Operaciones.RESERVA_CREAR, new Object[]{
                    "1002", "Prueba de integracion", fecha,
                    LocalTime.of(3, 0), LocalTime.of(4, 0),
                    new ArrayList<>(List.of("CAT-000004"))
            });

            assertTrue(creada.isExito());
            ResultadoReserva resultado = (ResultadoReserva) creada.getDatos();
            assertTrue(resultado.esExitosa(), "La reserva debió crearse");

            String idReserva = resultado.getReserva().getId();

            // 3) 1001 debe recibir la alerta de la reserva de 1002
            NotificacionReserva alerta = (NotificacionReserva) entrada.readObject();

            assertEquals(idReserva, alerta.getReservaId());
            assertEquals("Prueba de integracion", alerta.getActividad());
            assertEquals("1002", alerta.getFuncionarioId());

            // 4) Limpieza: se cancela la reserva para no dejarla ocupando el recurso
            Respuesta cancelada = enviar(Operaciones.RESERVA_CANCELAR,
                    new Object[]{"1002", idReserva});

            assertTrue(cancelada.isExito());
            assertTrue(((ResultadoReserva) cancelada.getDatos()).esExitosa());
        }
    }
}