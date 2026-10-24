
package una.sistemareservas.contratos;

import una.sistemareservas.modelo.Reserva;

import java.time.LocalDate;
import java.util.List;

public interface ICalendarioActividadesService {

    List<Reserva> listarReservasSemana(LocalDate fecha);

    LocalDate obtenerLunes(LocalDate fecha);
}
