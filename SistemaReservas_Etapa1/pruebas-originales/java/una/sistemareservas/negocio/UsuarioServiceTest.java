package una.sistemareservas.negocio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {

    private final UsuarioService servicio = new UsuarioService();

    @Test
    void autenticarConIdVacioDebeRetornarNull() {
        assertNull(servicio.autenticar("", "clave123"));
    }

    @Test
    void autenticarConClaveVaciaDebeRetornarNull() {
        assertNull(servicio.autenticar("admin", ""));
    }

    @Test
    void autenticarConCredencialesIncorrectasDebeRetornarNull() {
        assertNull(servicio.autenticar("USUARIO-NO-EXISTE-999", "cualquierClave"));
    }

    @Test
    void cambiarClaveConIdVacioDebeRetornarFalse() {
        assertFalse(servicio.cambiarClave("", "actual", "nueva"));
    }

    @Test
    void cambiarClaveConClaveActualVaciaDebeRetornarFalse() {
        assertFalse(servicio.cambiarClave("admin", "", "nueva"));
    }

    @Test
    void cambiarClaveConNuevaClaveVaciaDebeRetornarFalse() {
        assertFalse(servicio.cambiarClave("admin", "actual", ""));
    }

    @Test
    void cambiarClaveUsuarioInexistenteDebeRetornarFalse() {
        assertFalse(servicio.cambiarClave("USUARIO-NO-EXISTE-999", "actual", "nueva"));
    }
}