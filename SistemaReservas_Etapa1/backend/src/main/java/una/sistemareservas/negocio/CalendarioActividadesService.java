package una.sistemareservas.negocio;

import una.sistemareservas.datos.ReservaDAO;
import una.sistemareservas.modelo.EstadoReserva;
import una.sistemareservas.modelo.Reserva;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarioActividadesService {

    private final ReservaDAO reservaDAO;

    public CalendarioActividadesService() {
        reservaDAO = new ReservaDAO();
    }

    public List<Reserva> listarReservasSemana(
            LocalDate fecha) {

        List<Reserva> resultado =
                new ArrayList<>();

        if (fecha == null) {
            return resultado;
        }

        LocalDate lunes =
                fecha.with(DayOfWeek.MONDAY);

        LocalDate domingo =
                lunes.plusDays(6);

        for (Reserva reserva :
                reservaDAO.cargar()) {

            if (reserva.getEstado()
                    != EstadoReserva.ACTIVA) {

                continue;
            }

            if (reserva.getFecha() == null) {
                continue;
            }

            if (!reserva.getFecha().isBefore(lunes)
                    && !reserva.getFecha().isAfter(domingo)) {

                resultado.add(reserva);
            }
        }

        return resultado;
    }

    public LocalDate obtenerLunes(
            LocalDate fecha) {

        return fecha.with(DayOfWeek.MONDAY);
    }
}