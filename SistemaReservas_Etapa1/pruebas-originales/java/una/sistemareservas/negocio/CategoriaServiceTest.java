package una.sistemareservas.negocio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaServiceTest {

    private final CategoriaService servicio = new CategoriaService();

    @Test
    void agregarConDescripcionVaciaDebeDarError() {
        String error = servicio.agregar("");
        assertEquals("Debe indicar la descripción de la categoría.", error);
    }

    @Test
    void agregarConDescripcionNulaDebeDarError() {
        String error = servicio.agregar(null);
        assertEquals("Debe indicar la descripción de la categoría.", error);
    }

    @Test
    void eliminarCategoriaInexistenteDebeDarError() {
        String error = servicio.eliminar("CAT-NO-EXISTE-999");
        assertEquals("No existe una categoría con ese id.", error);
    }

    @Test
    void modificarCategoriaInexistenteDebeDarError() {
        String error = servicio.modificar("CAT-NO-EXISTE-999", "Descripción cualquiera");
        assertEquals("No existe una categoría con ese id.", error);
    }

    @Test
    void modificarConDescripcionVaciaDebeDarError() {
        String error = servicio.modificar("CAT-000001", "");
        assertEquals("Debe indicar la descripción de la categoría.", error);
    }
}