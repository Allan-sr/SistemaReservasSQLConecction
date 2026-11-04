package una.sistemareservas.negocio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {

    private final UsuarioService servicio = new UsuarioService();

    @Test
    void autenticarConIdVacioDebeDevolverNull() {
        assertNull(servicio.autenticar("", "1001"));
    }

    @Test
    void autenticarConIdNuloDebeDevolverNull() {
        assertNull(servicio.autenticar(null, "1001"));
    }

    @Test
    void autenticarConClaveVaciaDebeDevolverNull() {
        assertNull(servicio.autenticar("1001", ""));
    }

    @Test
    void cambiarClaveConIdVacioDebeFallar() {
        assertFalse(servicio.cambiarClave("", "actual", "nueva"));
    }

    @Test
    void cambiarClaveConClaveActualVaciaDebeFallar() {
        assertFalse(servicio.cambiarClave("1001", "", "nueva"));
    }

    @Test
    void cambiarClaveConNuevaClaveVaciaDebeFallar() {
        assertFalse(servicio.cambiarClave("1001", "actual", ""));
    }
}