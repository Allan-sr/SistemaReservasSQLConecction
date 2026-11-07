package una.sistemareservas.negocio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NotificacionReserva implements Serializable {

    private static final long serialVersionUID = 2L;

    private final String reservaId;
    private final String actividad;
    private final String funcionarioId;
    private final String funcionarioNombre;
    private final String fecha;
    private final String horaInicio;
    private final String horaFin;
    private final List<String> recursos;

    public NotificacionReserva(String reservaId, String actividad,
                               String funcionarioId, String funcionarioNombre,
                               String fecha, String horaInicio, String horaFin,
                               List<String> recursos) {
        this.reservaId = reservaId;
        this.actividad = actividad;
        this.funcionarioId = funcionarioId;
        this.funcionarioNombre = funcionarioNombre;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.recursos = recursos == null ? new ArrayList<>() : new ArrayList<>(recursos);
    }

    /** Constructor corto (hora ya unida como "08:00 - 09:00" y sin recursos). */
    public NotificacionReserva(String reservaId, String actividad,
                               String funcionarioId, String funcionarioNombre,
                               String fecha, String hora) {
        this(reservaId, actividad, funcionarioId, funcionarioNombre,
                fecha, hora, "", new ArrayList<>());
    }

    public String getReservaId() { return reservaId; }
    public String getActividad() { return actividad; }
    public String getFuncionarioId() { return funcionarioId; }
    public String getFuncionarioNombre() { return funcionarioNombre; }
    public String getFecha() { return fecha; }
    public String getHoraInicio() { return horaInicio; }
    public String getHoraFin() { return horaFin; }
    public List<String> getRecursos() { return new ArrayList<>(recursos); }

    /** Texto para la tarjeta de la lista, por ejemplo "08:00 - 09:00". */
    public String getHora() {
        if (horaFin == null || horaFin.isEmpty()) {
            return horaInicio;
        }
        return horaInicio + " - " + horaFin;
    }
}