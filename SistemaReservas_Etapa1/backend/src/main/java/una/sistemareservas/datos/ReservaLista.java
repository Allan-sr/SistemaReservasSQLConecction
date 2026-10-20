package una.sistemareservas.datos;

import una.sistemareservas.modelo.Reserva;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "reservas")
@XmlAccessorType(XmlAccessType.FIELD) // Forzar lectura directa sobre el atributo y evitar duplicidad con el getter
public class ReservaLista {

    @XmlElement(name = "reserva")
    private List<Reserva> reservas = new ArrayList<>();

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}