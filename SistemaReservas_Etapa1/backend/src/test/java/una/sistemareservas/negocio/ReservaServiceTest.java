package una.sistemareservas.negocio;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ReservaServiceTest {

    private static final String FUNCIONARIO = "1001";

    @Test
    void crearConActividadVaciaDebeDarError() {

        ResultadoReserva resultado = new ReservaService().crear(
                FUNCIONARIO, "",
                LocalDate.now().plusDays(1),
                LocalTime.of(8, 0), LocalTime.of(10, 0),
                Collections.singletonList("CAT-000001"));

        assertTrue(resultado.esErrorValidacion());
        assertEquals("Debe indicar la actividad que se realizará.",
                resultado.getMensajeError());
    }

    @Test
    void crearConFechaAnteriorDebeDarError() {

        ResultadoReserva resultado = new ReservaService().crear(
                FUNCIONARIO, "Reunión",
                LocalDate.now().minusDays(1),
                LocalTime.of(8, 0), LocalTime.of(10, 0),
                Collections.singletonList("CAT-000001"));

        assertTrue(resultado.esErrorValidacion());
        assertEquals("La fecha de la reserva no puede ser anterior a hoy.",
                resultado.getMensajeError());
    }

    @Test
    void crearConHoraInicioPosteriorDebeDarError() {

        ResultadoReserva resultado = new ReservaService().crear(
                FUNCIONARIO, "Reunión",
                LocalDate.now().plusDays(1),
                LocalTime.of(12, 0), LocalTime.of(10, 0),
                Collections.singletonList("CAT-000001"));

        assertTrue(resultado.esErrorValidacion());
        assertEquals("La hora de inicio debe ser anterior a la hora de finalización.",
                resultado.getMensajeError());
    }

    @Test
    void crearSinCategoriasDebeDarError() {

        ResultadoReserva resultado = new ReservaService().crear(
                FUNCIONARIO, "Reunión",
                LocalDate.now().plusDays(1),
                LocalTime.of(8, 0), LocalTime.of(10, 0),
                Collections.emptyList());

        assertTrue(resultado.esErrorValidacion());
        assertEquals("Debe seleccionar al menos una categoría de recurso.",
                resultado.getMensajeError());
    }
}