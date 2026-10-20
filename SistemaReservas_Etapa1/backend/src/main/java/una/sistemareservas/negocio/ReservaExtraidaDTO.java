package una.sistemareservas.negocio;

import java.util.List;

public class ReservaExtraidaDTO {
    private String actividad;
    private String fecha;        // Formato YYYY-MM-DD
    private String horaInicio;   // Formato HH:mm (ej. 08:00)
    private String horaFin;      // Formato HH:mm (ej. 10:00)
    private List<String> categorias;

    public String getActividad() { return actividad; }
    public void setActividad(String actividad) { this.actividad = actividad; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public List<String> getCategorias() { return categorias; }
    public void setCategorias(List<String> categorias) { this.categorias = categorias; }
}