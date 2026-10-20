package una.sistemareservas.negocio;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import una.sistemareservas.datos.ReservaDAO;
import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Funcionario;
import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.modelo.Rol;
import una.sistemareservas.util.Sesion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaServiceIT {

    private ReservaService servicio;

    @BeforeEach
    void prepararSesion() {

        Funcionario funcionario = new Funcionario(
                "1001",
                "1001",
                Rol.FUNCIONARIO,
                "Funcionario de prueba",
                "8888-8888"
        );

        Sesion.iniciar(funcionario);

        servicio = new ReservaService();
    }

    @AfterEach
    void cerrarSesion() {
        Sesion.cerrar();
    }

    @Test
    void crearReservaDebeGuardarLaReservaEnElDAO() {

        String actividad = "Prueba de integración";

        LocalDate fecha = LocalDate.now().plusDays(30);

        LocalTime horaInicio = LocalTime.of(8, 0);
        LocalTime horaFin = LocalTime.of(9, 0);

        Categoria categoriaSala = servicio
                .listarCategoriasDisponibles()
                .stream()
                .filter(categoria ->
                        categoria.getDescripcion()
                                .equalsIgnoreCase("Sala de reuniones"))
                .findFirst()
                .orElse(null);

        assertNotNull(
                categoriaSala,
                "Debe existir la categoría Sala de reuniones."
        );

        ResultadoReserva resultado = servicio.crear(
                actividad,
                fecha,
                horaInicio,
                horaFin,
                List.of(categoriaSala.getId())
        );

        assertNotNull(resultado);

        assertTrue(
                resultado.esExitosa(),
                "La reserva debería crearse correctamente. "
                        + resultado.getMensajeError()
        );

        assertNotNull(resultado.getReserva());

        String idReserva =
                resultado.getReserva().getId();

        ReservaDAO reservaDAO = new ReservaDAO();

        Reserva reservaGuardada =
                reservaDAO.buscarPorId(idReserva);

        assertNotNull(
                reservaGuardada,
                "La reserva debería existir en reservas.xml."
        );

        assertEquals(
                actividad,
                reservaGuardada.getActividad()
        );

        assertEquals(
                fecha,
                reservaGuardada.getFecha()
        );

        assertEquals(
                horaInicio,
                reservaGuardada.getHoraInicio()
        );

        assertEquals(
                horaFin,
                reservaGuardada.getHoraFin()
        );

        assertEquals(
                "1001",
                reservaGuardada.getFuncionarioId()
        );

        assertFalse(
                reservaGuardada.getRecursoIds().isEmpty(),
                "La reserva debería tener al menos un recurso asignado."
        );
    }
}