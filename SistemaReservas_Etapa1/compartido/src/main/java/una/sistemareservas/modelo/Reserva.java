package una.sistemareservas.modelo;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import una.sistemareservas.datos.LocalDateAdapter;
import una.sistemareservas.datos.LocalTimeAdapter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "reserva")
@XmlAccessorType(XmlAccessType.FIELD)
public class Reserva {

    @XmlElement
    private String id;

    @XmlElement
    private String actividad;

    @XmlElement
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fecha;

    @XmlElement
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaInicio;

    @XmlElement
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaFin;

    @XmlElement
    private String funcionarioId;

    @XmlElement(name = "recursoId")
    private List<String> recursoIds = new ArrayList<>();

    @XmlElement
    private EstadoReserva estado;

    @XmlTransient
    private Funcionario funcionario;

    @XmlTransient
    private List<Recurso> recursos = new ArrayList<>();

    public Reserva() {
    }

    public Reserva(String id, String actividad, LocalDate fecha,
                   LocalTime horaInicio, LocalTime horaFin,
                   Funcionario funcionario) {
        this.id = id;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        setFuncionario(funcionario);
        this.recursos = new ArrayList<>();
        this.estado = EstadoReserva.ACTIVA;
    }

    public void agregarRecurso(Recurso recurso) {
        recursos.add(recurso);
        recursoIds.add(recurso.getId());
    }

    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA;
    }

    public String getId() {
        return id;
    }

    public String getActividad() {
        return actividad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public String getFuncionarioId() {
        return funcionarioId;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
        this.funcionarioId = (funcionario != null) ? funcionario.getId() : null;
    }

    public List<String> getRecursoIds() {
        return recursoIds;
    }

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<Recurso> recursos) {
        this.recursos = recursos;
    }

    public EstadoReserva getEstado() {
        return estado;
    }
}