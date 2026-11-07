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

    private Respuesta enviarEnConexion(ObjectOutputStream salida, ObjectInputStream entrada,
            String operacion, Object datos) throws Exception {
        salida.writeObject(new Peticion(operacion, datos));
        salida.flush();
        salida.reset();

        Object mensaje = entrada.readObject();

        assertInstanceOf(Respuesta.class, mensaje);

        return (Respuesta) mensaje;
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
    void crearReservaAvisaALosOtrosUsuariosYSePuedeCancelar()
            throws Exception {

        // El usuario 1001 usará una sola conexión.
        try (Socket oyente = new Socket("localhost", PUERTO)) {

            oyente.setSoTimeout(5000);

            ObjectOutputStream salida =
                    new ObjectOutputStream(oyente.getOutputStream());
            salida.flush();

            ObjectInputStream entrada =
                    new ObjectInputStream(oyente.getInputStream());

            // 1. Iniciar sesión en esa conexión.
            Respuesta login = enviarEnConexion(
                    salida,
                    entrada,
                    Operaciones.USUARIO_AUTENTICAR,
                    new Object[]{"1001", "1001"}
            );

            assertTrue(login.isExito());
            assertInstanceOf(Usuario.class, login.getDatos());
            assertEquals(
                    "1001",
                    ((Usuario) login.getDatos()).getId()
            );

            // 2. Suscribir esa misma conexión a las alertas.
            Respuesta suscripcion = enviarEnConexion(
                    salida,
                    entrada,
                    "ALERTAS_SUSCRIBIR",
                    null
            );

            assertTrue(
                    suscripcion.isExito(),
                    suscripcion.getMensaje()
            );

            // 3. Comprobar que sigue atendiendo peticiones.
            Respuesta ping = enviarEnConexion(
                    salida,
                    entrada,
                    Operaciones.PING,
                    null
            );

            assertTrue(ping.isExito());
            assertEquals("PONG", ping.getDatos());

            // 4. Otro usuario crea una reserva.
            LocalDate fecha = LocalDate.now().plusDays(400);

            Respuesta creada = enviar(
                    Operaciones.RESERVA_CREAR,
                    new Object[]{
                            "1002",
                            "Prueba de integracion",
                            fecha,
                            LocalTime.of(3, 0),
                            LocalTime.of(4, 0),
                            new ArrayList<>(List.of("CAT-000004"))
                    }
            );

            assertTrue(creada.isExito(), creada.getMensaje());
            assertInstanceOf(
                    ResultadoReserva.class,
                    creada.getDatos()
            );

            ResultadoReserva resultado =
                    (ResultadoReserva) creada.getDatos();

            assertTrue(
                    resultado.esExitosa(),
                    "La reserva debió crearse."
            );

            String idReserva = resultado.getReserva().getId();

            try {
                // 5. La alerta llega por la conexión del login.
                Object mensaje = entrada.readObject();

                assertInstanceOf(
                        NotificacionReserva.class,
                        mensaje
                );

                NotificacionReserva alerta =
                        (NotificacionReserva) mensaje;

                assertEquals(idReserva, alerta.getReservaId());
                assertEquals(
                        "Prueba de integracion",
                        alerta.getActividad()
                );
                assertEquals("1002", alerta.getFuncionarioId());

                // 6. Después de la alerta, puede pedir otra respuesta.
                Respuesta otroPing = enviarEnConexion(
                        salida,
                        entrada,
                        Operaciones.PING,
                        null
                );

                assertTrue(otroPing.isExito());
                assertEquals("PONG", otroPing.getDatos());

                // 7. Cerrar la suscripción en esa conexión.
                Respuesta cierre = enviarEnConexion(
                        salida,
                        entrada,
                        "ALERTAS_DESUSCRIBIR",
                        null
                );

                assertTrue(cierre.isExito());

            } finally {
                // Cancelar la reserva incluso si falla la comprobación.
                Respuesta cancelada = enviar(
                        Operaciones.RESERVA_CANCELAR,
                        new Object[]{"1002", idReserva}
                );

                assertTrue(cancelada.isExito());

                ResultadoReserva cancelacion =
                        (ResultadoReserva) cancelada.getDatos();

                assertTrue(cancelacion.esExitosa());
            }
        }
    }
}