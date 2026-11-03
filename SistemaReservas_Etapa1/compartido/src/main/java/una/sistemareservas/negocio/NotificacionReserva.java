package una.sistemareservas.negocio;

import java.io.Serializable;

public class NotificacionReserva implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String reservaId;
    private final String actividad;
    private final String funcionarioId;
    private final String funcionarioNombre;
    private final String fecha;
    private final String hora;

    public NotificacionReserva(String reservaId, String actividad,
                               String funcionarioId, String funcionarioNombre,
                               String fecha, String hora) {
        this.reservaId = reservaId;
        this.actividad = actividad;
        this.funcionarioId = funcionarioId;
        this.funcionarioNombre = funcionarioNombre;
        this.fecha = fecha;
        this.hora = hora;
    }

    public String getReservaId() { return reservaId; }
    public String getActividad() { return actividad; }
    public String getFuncionarioId() { return funcionarioId; }
    public String getFuncionarioNombre() { return funcionarioNombre; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
}