package una.sistemareservas.contratos;

import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.negocio.ResultadoReserva;
import una.sistemareservas.negocio.ReservaExtraidaDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IReservaService {

    ResultadoReserva crear(
            String funcionarioId,
            String actividad,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            List<String> categoriaIds
    );

    ResultadoReserva cancelar(String funcionarioId, String id);

    List<Reserva> listarMisReservas(String funcionarioId);

    List<Reserva> listar();

    ReservaExtraidaDTO extraerDatosReserva(String frase);
}
