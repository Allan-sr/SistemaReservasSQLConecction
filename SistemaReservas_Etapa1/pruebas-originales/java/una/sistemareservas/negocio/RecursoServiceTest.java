package una.sistemareservas.negocio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecursoServiceTest {

    private final RecursoService servicio = new RecursoService();

    @Test
    void agregarConIdVacioDebeDarError() {
        String error = servicio.agregar("", "CAT-000001", "Descripción");
        assertEquals("Debe indicar el id (número de activo) del recurso.", error);
    }

    @Test
    void agregarConCategoriaVaciaDebeDarError() {
        String error = servicio.agregar("REC-001", "", "Descripción");
        assertEquals("Debe seleccionar una categoría.", error);
    }

    @Test
    void agregarConDescripcionVaciaDebeDarError() {
        String error = servicio.agregar("REC-001", "CAT-000001", "");
        assertEquals("Debe indicar la descripción del recurso.", error);
    }

    @Test
    void eliminarRecursoInexistenteDebeDarError() {
        String error = servicio.eliminar("REC-NO-EXISTE-999");
        assertEquals("No existe un recurso con ese id.", error);
    }

    @Test
    void modificarRecursoInexistenteDebeDarError() {
        String error = servicio.modificar("REC-NO-EXISTE-999", "CAT-000001", "Descripción");
        assertEquals("No existe un recurso con ese id.", error);
    }
}
