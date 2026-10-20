package una.sistemareservas.negocio;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import una.sistemareservas.modelo.Reserva;

import static org.junit.jupiter.api.Assertions.*;

class CalendarioActividadesServiceTest {

    @Test
    void obtenerLunesDebeDevolverElLunesDeLaSemana() {

        CalendarioActividadesService servicio =
                new CalendarioActividadesService();

        LocalDate fecha =
                LocalDate.of(2026, 9, 16); // miércoles

        LocalDate resultado =
                servicio.obtenerLunes(fecha);

        assertEquals(
                DayOfWeek.MONDAY,
                resultado.getDayOfWeek()
        );

        assertEquals(
                LocalDate.of(2026, 9, 14),
                resultado
        );
    }

    @Test
    void listarReservasSemanaConFechaNulaDebeDevolverListaVacia() {

        CalendarioActividadesService servicio =
                new CalendarioActividadesService();

        List<Reserva> resultado =
                servicio.listarReservasSemana(null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}