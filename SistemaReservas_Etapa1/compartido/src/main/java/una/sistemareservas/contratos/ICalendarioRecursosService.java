
package una.sistemareservas.contratos;

import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.modelo.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ICalendarioRecursosService {

    List<Categoria> listarCategorias();

    List<Recurso> listarRecursosPorCategoria(String categoriaId);

    List<LocalTime> obtenerHorasDelDia();

    Reserva obtenerReservaDelRecurso(
            Recurso recurso,
            LocalDate fecha,
            LocalTime hora
    );
}
