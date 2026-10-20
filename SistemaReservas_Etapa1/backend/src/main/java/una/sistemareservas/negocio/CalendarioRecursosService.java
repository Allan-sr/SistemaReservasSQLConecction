package una.sistemareservas.negocio;

import una.sistemareservas.datos.CategoriaDAO;
import una.sistemareservas.datos.RecursoDAO;
import una.sistemareservas.datos.ReservaDAO;
import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.EstadoReserva;
import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.modelo.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CalendarioRecursosService {

    private final CategoriaDAO categoriaDAO;
    private final RecursoDAO recursoDAO;
    private final ReservaDAO reservaDAO;

    public CalendarioRecursosService() {
        categoriaDAO = new CategoriaDAO();
        recursoDAO = new RecursoDAO();
        reservaDAO = new ReservaDAO();
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.cargar();
    }

    public List<Recurso> listarRecursosPorCategoria(String categoriaId) {

        List<Recurso> resultado =
                new ArrayList<>();

        if (categoriaId == null ||
                categoriaId.trim().isEmpty()) {

            return resultado;
        }

        for (Recurso recurso :
                recursoDAO.cargar()) {

            if (recurso.getCategoriaId() != null &&
                    recurso.getCategoriaId()
                            .equalsIgnoreCase(categoriaId)) {

                resultado.add(recurso);
            }
        }

        return resultado;
    }

    public List<Reserva> listarReservasPorFecha(
            LocalDate fecha) {

        List<Reserva> resultado =
                new ArrayList<>();

        if (fecha == null) {
            return resultado;
        }

        for (Reserva reserva :
                reservaDAO.cargar()) {

            if (reserva.getEstado() ==
                    EstadoReserva.ACTIVA &&
                    reserva.getFecha() != null &&
                    reserva.getFecha().equals(fecha)) {

                resultado.add(reserva);
            }
        }

        return resultado;
    }

    public Reserva obtenerReservaDelRecurso(
            Recurso recurso,
            LocalDate fecha,
            LocalTime hora) {

        if (recurso == null ||
                fecha == null ||
                hora == null) {

            return null;
        }

        List<Reserva> reservas =
                listarReservasPorFecha(fecha);

        for (Reserva reserva : reservas) {

            if (!reserva.getRecursoIds()
                    .contains(recurso.getId())) {

                continue;
            }

            if (horaEstaDentroDeReserva(
                    reserva,
                    hora)) {

                return reserva;
            }
        }

        return null;
    }

    private boolean horaEstaDentroDeReserva(
            Reserva reserva,
            LocalTime hora) {

        return !hora.isBefore(
                reserva.getHoraInicio()
        )
                && hora.isBefore(
                reserva.getHoraFin()
        );
    }

    public List<LocalTime> obtenerHorasDelDia() {
        List<LocalTime> horas = new ArrayList<>();
        for (int hora = 0; hora < 24; hora++) {
            horas.add(LocalTime.of(hora, 0));
        }
        return horas;
    }
}