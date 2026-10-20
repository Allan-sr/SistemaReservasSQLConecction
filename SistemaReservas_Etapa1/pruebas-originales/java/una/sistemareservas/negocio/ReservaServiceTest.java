package una.sistemareservas.negocio;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ReservaServiceTest {

    @Test
    void crearConActividadVaciaDebeDarError() {

        ReservaService servicio = new ReservaService();

        ResultadoReserva resultado = servicio.crear(
                "",
                LocalDate.now().plusDays(1),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                Collections.singletonList("CAT-001")
        );

        assertNotNull(resultado);
        assertTrue(resultado.esErrorValidacion());

        assertEquals(
                "Debe indicar la actividad que se realizará.",
                resultado.getMensajeError()
        );
    }

    @Test
    void crearConFechaAnteriorDebeDarError() {

        ReservaService servicio = new ReservaService();

        ResultadoReserva resultado = servicio.crear(
                "Reunión",
                LocalDate.now().minusDays(1),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                Collections.singletonList("CAT-001")
        );

        assertNotNull(resultado);
        assertTrue(resultado.esErrorValidacion());

        assertEquals(
                "La fecha de la reserva no puede ser anterior a hoy.",
                resultado.getMensajeError()
        );
    }

    @Test
    void crearConHoraInicioPosteriorDebeDarError() {

        ReservaService servicio = new ReservaService();

        ResultadoReserva resultado = servicio.crear(
                "Reunión",
                LocalDate.now().plusDays(1),
                LocalTime.of(12, 0),
                LocalTime.of(10, 0),
                Collections.singletonList("CAT-001")
        );

        assertNotNull(resultado);
        assertTrue(resultado.esErrorValidacion());

        assertEquals(
                "La hora de inicio debe ser anterior a la hora de finalización.",
                resultado.getMensajeError()
        );
    }

    @Test
    void crearSinCategoriasDebeDarError() {

        ReservaService servicio = new ReservaService();

        ResultadoReserva resultado = servicio.crear(
                "Reunión",
                LocalDate.now().plusDays(1),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                Collections.emptyList()
        );

        assertNotNull(resultado);
        assertTrue(resultado.esErrorValidacion());

        assertEquals(
                "Debe seleccionar al menos una categoría de recurso.",
                resultado.getMensajeError()
        );
    }
}