package una.sistemareservas.negocio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioServiceTest {

    private final FuncionarioService servicio = new FuncionarioService();

    @Test
    void agregarConIdVacioDebeDarError() {
        String error = servicio.agregar("", "Nombre", "8888-8888");
        assertEquals("Debe indicar el id del funcionario.", error);
    }

    @Test
    void agregarConNombreVacioDebeDarError() {
        String error = servicio.agregar("2002", "", "8888-8888");
        assertEquals("Debe indicar el nombre del funcionario.", error);
    }

    @Test
    void agregarConTelefonoVacioDebeDarError() {
        String error = servicio.agregar("2002", "Nombre", "");
        assertEquals("Debe indicar el teléfono del funcionario.", error);
    }

    @Test
    void buscarPorIdInexistenteDebeRetornarNull() {
        assertNull(servicio.buscarPorId("9999-NO-EXISTE"));
    }

    @Test
    void eliminarFuncionarioInexistenteDebeDarError() {
        String error = servicio.eliminar("9999-NO-EXISTE");
        assertEquals("No existe un funcionario con ese id.", error);
    }

    @Test
    void modificarFuncionarioInexistenteDebeDarError() {
        String error = servicio.modificar("9999-NO-EXISTE", "Nombre", "8888-8888");
        assertEquals("No existe un funcionario con ese id.", error);
    }
}