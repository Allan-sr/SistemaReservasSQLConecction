package una.sistemareservas.negocio;

import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Reserva;

import java.util.Collections;
import java.util.List;

public class ResultadoReserva {

    public enum Tipo {
        EXITO,
        SIN_DISPONIBILIDAD,
        ERROR_VALIDACION
    }

    private final Tipo tipo;
    private final Reserva reserva;
    private final List<Categoria> categoriasNoDisponibles;
    private final String mensajeError;

    private ResultadoReserva(Tipo tipo, Reserva reserva,
                             List<Categoria> categoriasNoDisponibles,
                             String mensajeError) {
        this.tipo = tipo;
        this.reserva = reserva;
        this.categoriasNoDisponibles = categoriasNoDisponibles;
        this.mensajeError = mensajeError;
    }

    public static ResultadoReserva exito(Reserva reserva) {
        return new ResultadoReserva(Tipo.EXITO, reserva, Collections.emptyList(), null);
    }

    public static ResultadoReserva sinDisponibilidad(List<Categoria> categoriasNoDisponibles) {
        return new ResultadoReserva(Tipo.SIN_DISPONIBILIDAD, null, categoriasNoDisponibles, null);
    }

    public static ResultadoReserva error(String mensajeError) {
        return new ResultadoReserva(Tipo.ERROR_VALIDACION, null, Collections.emptyList(), mensajeError);
    }

    public boolean esExitosa() {
        return tipo == Tipo.EXITO;
    }

    public boolean esSinDisponibilidad() {
        return tipo == Tipo.SIN_DISPONIBILIDAD;
    }

    public boolean esErrorValidacion() {
        return tipo == Tipo.ERROR_VALIDACION;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public List<Categoria> getCategoriasNoDisponibles() {
        return categoriasNoDisponibles;
    }

    public String getMensajeError() {
        return mensajeError;
    }
}
