package una.sistemareservas.contratos;

import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.negocio.ResultadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IReservaService {

    ResultadoReserva crear(
            String actividad,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            List<String> categoriaIds
    );

    ResultadoReserva cancelar(String id);

    List<Reserva> listarMisReservas();
}

