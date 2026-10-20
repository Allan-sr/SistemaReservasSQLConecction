package una.sistemareservas.negocio;

import una.sistemareservas.datos.CategoriaDAO;
import una.sistemareservas.datos.RecursoDAO;
import una.sistemareservas.datos.ReservaDAO;
import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.EstadoReserva;
import una.sistemareservas.modelo.Funcionario;
import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.contratos.IReservaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReservaService implements IReservaService{

    private final ReservaDAO reservaDAO;
    private final RecursoDAO recursoDAO;
    private final CategoriaDAO categoriaDAO;
    private Funcionario funcionarioActual;

    public ReservaService() {
        reservaDAO = new ReservaDAO();
        recursoDAO = new RecursoDAO();
        categoriaDAO = new CategoriaDAO();
    }

    /**
     * Lista todas las categorías, para llenar la selección múltiple del formulario.
     */
    public List<Categoria> listarCategoriasDisponibles() {
        return categoriaDAO.cargar();
    }

    public void setFuncionarioActual(Funcionario funcionario) {
        this.funcionarioActual = funcionario;
    }

    /**
     * Lista las reservas del funcionario que tiene la sesión activa,
     * ordenadas por fecha y hora de inicio (más recientes primero).
     */
    public List<Reserva> listarMisReservas() {

        Funcionario funcionarioActual = obtenerFuncionarioActual();

        List<Reserva> resultado = new ArrayList<>();

        for (Reserva reserva : reservaDAO.cargar()) {
            if (funcionarioActual != null
                    && funcionarioActual.getId().equalsIgnoreCase(reserva.getFuncionarioId())) {
                resultado.add(reserva);
            }
        }

        resultado.sort(
                Comparator.comparing(Reserva::getFecha)
                        .thenComparing(Reserva::getHoraInicio)
                        .reversed()
        );

        return resultado;
    }

    /**
     * Lista todas las reservas del sistema sin filtrar por funcionario.
     */
    public List<Reserva> listar() {
        return reservaDAO.cargar();
    }

    public ResultadoReserva crear(String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, List<String> categoriaIds) {

        String error = validarDatos(actividad, fecha, horaInicio, horaFin, categoriaIds);

        if (error != null) {
            return ResultadoReserva.error(error);
        }

        Funcionario funcionarioActual = obtenerFuncionarioActual();

        if (funcionarioActual == null) {
            return ResultadoReserva.error("No hay un funcionario en sesión.");
        }

        List<Reserva> reservasActivas = reservasActivas();

        List<Categoria> categoriasNoDisponibles = new ArrayList<>();
        List<Recurso> recursosAsignados = new ArrayList<>();

        for (String categoriaId : categoriaIds) {

            Categoria categoria = categoriaDAO.buscarPorId(categoriaId);

            if (categoria == null) {
                continue;
            }

            Recurso recursoDisponible =
                    buscarRecursoDisponible(categoria, fecha, horaInicio, horaFin, reservasActivas);

            if (recursoDisponible == null) {
                categoriasNoDisponibles.add(categoria);
            } else {
                recursosAsignados.add(recursoDisponible);
            }
        }

        if (!categoriasNoDisponibles.isEmpty()) {
            return ResultadoReserva.sinDisponibilidad(categoriasNoDisponibles);
        }

        Reserva nuevaReserva = new Reserva(
                generarSiguienteId(),
                actividad.trim(),
                fecha,
                horaInicio,
                horaFin,
                funcionarioActual
        );

        for (Recurso recurso : recursosAsignados) {
            nuevaReserva.agregarRecurso(recurso);
        }

        reservaDAO.agregar(nuevaReserva);

        return ResultadoReserva.exito(nuevaReserva);
    }

    /**
     * Cancela una reserva futura del funcionario en sesión, liberando
     * todos los recursos que tenía asignados.
     */
    public ResultadoReserva cancelar(String id) {

        Reserva reserva = reservaDAO.buscarPorId(id);

        if (reserva == null) {
            return ResultadoReserva.error("No existe una reserva con ese id.");
        }

        Funcionario funcionarioActual = obtenerFuncionarioActual();

        if (funcionarioActual == null
                || !funcionarioActual.getId().equalsIgnoreCase(reserva.getFuncionarioId())) {
            return ResultadoReserva.error("Esa reserva no pertenece al funcionario en sesión.");
        }

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            return ResultadoReserva.error("Esa reserva ya se encuentra cancelada.");
        }

        if (reserva.getFecha().isBefore(LocalDate.now())) {
            return ResultadoReserva.error("No se puede cancelar una reserva cuya fecha ya pasó.");
        }

        reserva.cancelar();
        reservaDAO.modificar(reserva);

        return ResultadoReserva.exito(reserva);
    }

    /**
     * Busca, entre los recursos de la categoría dada, el primero que no
     * tenga cruce de horario con ninguna reserva activa en esa fecha.
     */
    private Recurso buscarRecursoDisponible(Categoria categoria, LocalDate fecha,
                                            LocalTime horaInicio, LocalTime horaFin,
                                            List<Reserva> reservasActivas) {

        List<Recurso> recursosDeCategoria = new ArrayList<>();

        for (Recurso recurso : recursoDAO.cargar()) {
            if (recurso.getCategoriaId() != null
                    && recurso.getCategoriaId().equalsIgnoreCase(categoria.getId())) {
                recursosDeCategoria.add(recurso);
            }
        }

        for (Recurso recurso : recursosDeCategoria) {

            boolean ocupado = false;

            for (Reserva reserva : reservasActivas) {

                if (!reserva.getRecursoIds().contains(recurso.getId())) {
                    continue;
                }

                if (haySolapamiento(reserva, fecha, horaInicio, horaFin)) {
                    ocupado = true;
                    break;
                }
            }

            if (!ocupado) {
                return recurso;
            }
        }

        return null;
    }

    private boolean haySolapamiento(Reserva reserva, LocalDate fecha,
                                    LocalTime horaInicio, LocalTime horaFin) {

        if (!reserva.getFecha().equals(fecha)) {
            return false;
        }

        return reserva.getHoraInicio().isBefore(horaFin)
                && horaInicio.isBefore(reserva.getHoraFin());
    }

    private List<Reserva> reservasActivas() {

        List<Reserva> activas = new ArrayList<>();

        for (Reserva reserva : reservaDAO.cargar()) {
            if (reserva.getEstado() == EstadoReserva.ACTIVA) {
                activas.add(reserva);
            }
        }

        return activas;
    }

    private String validarDatos(String actividad, LocalDate fecha,
                                LocalTime horaInicio, LocalTime horaFin,
                                List<String> categoriaIds) {

        if (actividad == null || actividad.trim().isEmpty()) {
            return "Debe indicar la actividad que se realizará.";
        }

        if (fecha == null) {
            return "Debe indicar la fecha de la reserva.";
        }

        if (fecha.isBefore(LocalDate.now())) {
            return "La fecha de la reserva no puede ser anterior a hoy.";
        }

        if (horaInicio == null || horaFin == null) {
            return "Debe indicar la hora de inicio y la hora de finalización.";
        }

        if (!horaInicio.isBefore(horaFin)) {
            return "La hora de inicio debe ser anterior a la hora de finalización.";
        }

        if (categoriaIds == null || categoriaIds.isEmpty()) {
            return "Debe seleccionar al menos una categoría de recurso.";
        }

        return null;
    }

    private Funcionario obtenerFuncionarioActual() {
        return funcionarioActual;
    }

    private String generarSiguienteId() {

        int maximo = 0;

        for (Reserva reserva : reservaDAO.cargar()) {

            String id = reserva.getId();

            if (id != null && id.startsWith("RES-")) {

                try {
                    int numero = Integer.parseInt(id.substring(4));
                    maximo = Math.max(maximo, numero);
                } catch (NumberFormatException ignorado) {
                    // Id con formato inesperado; se ignora para el cálculo.
                }
            }
        }

        return String.format("RES-%06d", maximo + 1);
    }
}
