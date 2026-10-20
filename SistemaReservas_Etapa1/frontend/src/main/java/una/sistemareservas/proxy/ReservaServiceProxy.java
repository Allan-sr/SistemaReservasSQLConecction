
package una.sistemareservas.proxy;

import una.sistemareservas.contratos.IReservaService;
import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.negocio.ResultadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaServiceProxy implements IReservaService {

    @Override
    public ResultadoReserva crear(
            String actividad,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            List<String> categoriaIds) {

        throw new UnsupportedOperationException(
                "Pendiente: enviar petición al Backend");
    }

    @Override
    public ResultadoReserva cancelar(String id) {

        throw new UnsupportedOperationException(
                "Pendiente: enviar petición al Backend");
    }

    @Override
    public List<Reserva> listarMisReservas() {

        throw new UnsupportedOperationException(
                "Pendiente: solicitar reservas al Backend");
    }
}
