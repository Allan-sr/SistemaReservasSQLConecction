package una.sistemareservas.negocio;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import una.sistemareservas.modelo.Recurso;

import static org.junit.jupiter.api.Assertions.*;

class CalendarioRecursosServiceTest {

    @Test
    void obtenerHorasDelDiaDebeDevolver24Horas() {

        CalendarioRecursosService servicio =
                new CalendarioRecursosService();

        List<LocalTime> horas =
                servicio.obtenerHorasDelDia();

        assertNotNull(horas);

        assertEquals(
                24,
                horas.size()
        );

        assertEquals(
                LocalTime.of(0, 0),
                horas.get(0)
        );

        assertEquals(
                LocalTime.of(23, 0),
                horas.get(23)
        );
    }

    @Test
    void listarRecursosPorCategoriaConIdNuloDebeDevolverListaVacia() {

        CalendarioRecursosService servicio =
                new CalendarioRecursosService();

        List<Recurso> resultado =
                servicio.listarRecursosPorCategoria(null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarRecursosPorCategoriaConIdVacioDebeDevolverListaVacia() {

        CalendarioRecursosService servicio =
                new CalendarioRecursosService();

        List<Recurso> resultado =
                servicio.listarRecursosPorCategoria("");

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
