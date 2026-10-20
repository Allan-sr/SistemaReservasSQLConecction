package una.sistemareservas.datos;

import una.sistemareservas.modelo.Recurso;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "recursos")
@XmlAccessorType(XmlAccessType.FIELD) // Indica a JAXB que lea los campos directamente
public class RecursoLista {

    @XmlElement(name = "recurso")
    private List<Recurso> recursos = new ArrayList<>();

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<Recurso> recursos) {
        this.recursos = recursos;
    }
}