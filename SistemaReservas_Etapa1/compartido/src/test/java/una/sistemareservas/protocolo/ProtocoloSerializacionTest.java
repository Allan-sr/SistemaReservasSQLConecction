package una.sistemareservas.protocolo;

import org.junit.jupiter.api.Test;
import una.sistemareservas.negocio.NotificacionReserva;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ProtocoloSerializacionTest {

    /** Simula lo que hace el socket: escribe el objeto a bytes y lo lee de nuevo. */
    @SuppressWarnings("unchecked")
    private <T> T viajar(T objeto) throws Exception {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        try (ObjectOutputStream salida = new ObjectOutputStream(bytes)) {
            salida.writeObject(objeto);
        }

        try (ObjectInputStream entrada =
                     new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (T) entrada.readObject();
        }
    }

    @Test
    void peticionConservaOperacionYDatos() throws Exception {

        Peticion original = new Peticion(
                Operaciones.USUARIO_AUTENTICAR,
                new Object[]{"1001", "1001"}
        );

        Peticion copia = viajar(original);

        assertEquals(Operaciones.USUARIO_AUTENTICAR, copia.getOperacion());
        assertArrayEquals(new Object[]{"1001", "1001"}, (Object[]) copia.getDatos());
    }

    @Test
    void respuestaConservaExitoDatosYMensaje() throws Exception {

        Respuesta copia = viajar(new Respuesta(true, "PONG", "Comunicación exitosa"));

        assertTrue(copia.isExito());
        assertEquals("PONG", copia.getDatos());
        assertEquals("Comunicación exitosa", copia.getMensaje());
    }

    @Test
    void notificacionDeReservaConservaSusCampos() throws Exception {

        NotificacionReserva copia = viajar(new NotificacionReserva(
                "RES-000099", "Reunión", "1002", "Maria Rodriguez",
                "2026-11-10", "08:00 - 09:00"));

        assertEquals("RES-000099", copia.getReservaId());
        assertEquals("Reunión", copia.getActividad());
        assertEquals("1002", copia.getFuncionarioId());
        assertEquals("Maria Rodriguez", copia.getFuncionarioNombre());
        assertEquals("2026-11-10", copia.getFecha());
        assertEquals("08:00 - 09:00", copia.getHora());
    }
}